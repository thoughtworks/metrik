package fourkeymetrics.datasource.pipeline.builddata.controller

import fourkeymetrics.common.model.Build
import fourkeymetrics.dashboard.controller.SynchronizationApplicationService
import fourkeymetrics.dashboard.model.Dashboard
import fourkeymetrics.dashboard.model.Pipeline
import fourkeymetrics.dashboard.repository.DashboardRepository
import fourkeymetrics.dashboard.service.jenkins.JenkinsPipelineService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@Import(SynchronizationApplicationService::class, DashboardRepository::class)
internal class SynchronizationApplicationServiceTest {
    @Autowired
    private lateinit var synchronizationApplicationService: SynchronizationApplicationService

    @MockBean
    private lateinit var dashboardRepository: DashboardRepository

    @MockBean
    private lateinit var jenkins: JenkinsPipelineService

    @BeforeEach
    internal fun setUp() {
        `when`(dashboardRepository.getDashBoardDetailById(anyString())).thenReturn(Dashboard())
    }

    @Test
    internal fun `should sync builds when there is no previous update`() {
        val dashboardId = "fake-dashboard-id"
        val pipelineId = "fake-pipeline-id"
        val builds = listOf(Build())

        `when`(dashboardRepository.getLastSyncRecord(dashboardId)).thenReturn(null)
        `when`(dashboardRepository.getPipelineConfiguration(dashboardId)).thenReturn(listOf(Pipeline(id = pipelineId)))
        `when`(jenkins.syncBuilds(dashboardId, pipelineId)).thenReturn(builds)

        val updatedTimestamp = synchronizationApplicationService.synchronize(dashboardId)

        verify(jenkins, times(1)).syncBuilds(dashboardId, pipelineId)
        verify(dashboardRepository, times(1)).updateSynchronizationTime(anyString(), anyLong())

        assertThat(updatedTimestamp).isNotNull
    }

    @Test
    internal fun `should save builds from 2 weeks ago of previous update to now`() {
        val dashboardId = "fake-dashboard-id"
        val pipelineId = "fake-pipeline-id"
        val builds = listOf(Build())
        val lastSyncTimestamp = 1610668800000

        `when`(dashboardRepository.getLastSyncRecord(dashboardId)).thenReturn(lastSyncTimestamp)
        `when`(dashboardRepository.getPipelineConfiguration(dashboardId)).thenReturn(listOf(Pipeline(id = pipelineId)))
        `when`(dashboardRepository.updateSynchronizationTime(anyString(), anyLong())).thenReturn(lastSyncTimestamp + 1)
        `when`(jenkins.syncBuilds(dashboardId, pipelineId)).thenReturn(builds)

        val updateTimestamp = synchronizationApplicationService.synchronize(dashboardId)

        verify(jenkins, times(1)).syncBuilds(dashboardId, pipelineId)
        verify(dashboardRepository, times(1)).updateSynchronizationTime(anyString(), anyLong())

        assertThat(updateTimestamp).isGreaterThan(lastSyncTimestamp)
    }

    @Test
    internal fun `should not save any builds if fetch data from pipeline failed`() {
        val dashboardId = "fake-dashboard-id"
        val pipelineId = "fake-pipeline-id"
        val lastSyncTimestamp = 1610668800000

        `when`(dashboardRepository.getLastSyncRecord(dashboardId)).thenReturn(lastSyncTimestamp)
        `when`(dashboardRepository.getPipelineConfiguration(dashboardId)).thenReturn(listOf(Pipeline(id = pipelineId)))
        `when`(dashboardRepository.updateSynchronizationTime(anyString(), anyLong())).thenReturn(lastSyncTimestamp + 1)
        `when`(jenkins.syncBuilds(dashboardId, pipelineId)).thenThrow(RuntimeException())

        val updateTimestamp = synchronizationApplicationService.synchronize(dashboardId)

        verify(dashboardRepository, never()).updateSynchronizationTime(anyString(), anyLong())

        assertThat(updateTimestamp).isEqualTo(lastSyncTimestamp)
    }

    @Test
    internal fun `should get last synchronization time`() {
        val dashboardId = "fake-dashboard-id"
        val lastSyncTimestamp = 1610668800000

        `when`(dashboardRepository.getLastSyncRecord(dashboardId)).thenReturn(lastSyncTimestamp)

        val updatedTimestamp = synchronizationApplicationService.getLastSyncTimestamp(dashboardId)

        assertThat(updatedTimestamp).isEqualTo(lastSyncTimestamp)
    }
}