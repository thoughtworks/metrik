package fourkeymetrics.datasource.pipeline.builddata.controller

import fourkeymetrics.common.model.Build
import fourkeymetrics.dashboard.controller.SynchronizationService
import fourkeymetrics.dashboard.model.Pipeline
import fourkeymetrics.dashboard.repository.DashboardRepository
import fourkeymetrics.dashboard.repository.UpdateRecord
import fourkeymetrics.dashboard.repository.UpdateRepository
import fourkeymetrics.dashboard.service.jenkins.JenkinsPipelineService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@Import(SynchronizationService::class, DashboardRepository::class)
internal class SynchronizationServiceTest {
    @Autowired
    private lateinit var synchronizationService: SynchronizationService

    @MockBean
    private lateinit var updateRepository: UpdateRepository

    @MockBean
    private lateinit var dashboardRepository: DashboardRepository

    @MockBean
    private lateinit var jenkins: JenkinsPipelineService

    private fun <T> any(type: Class<T>): T = Mockito.any(type)


    @Test
    internal fun `should sync builds when there is no previous update`() {
        val dashboardId = "fake-dashboard-id"
        val pipelineId = "fake-pipeline-id"
        val builds = listOf(Build())

        `when`(updateRepository.getLastUpdate(dashboardId)).thenReturn(null)
        `when`(dashboardRepository.getPipelineConfiguration(dashboardId)).thenReturn(listOf(Pipeline(id = pipelineId)))
        `when`(jenkins.syncBuilds(dashboardId, pipelineId, 0)).thenReturn(builds)

        val updatedTimestamp = synchronizationService.synchronize(dashboardId)

        verify(jenkins, times(1)).syncBuilds(dashboardId, pipelineId, 0)

        assertThat(updatedTimestamp).isNotNull
    }

    @Test
    internal fun `should save builds from 2 weeks ago of previous update to now`() {
        val dashboardId = "fake-dashboard-id"
        val pipelineId = "fake-pipeline-id"
        val builds = listOf(Build())
        val lastUpdateRecord = UpdateRecord(
            dashboardId,
            1610668800000
        )

        val startTimestamp = lastUpdateRecord.updateTimestamp - 14 * 24 * 60 * 60 * 1000L

        `when`(updateRepository.getLastUpdate(dashboardId)).thenReturn(lastUpdateRecord)
        `when`(dashboardRepository.getPipelineConfiguration(dashboardId)).thenReturn(listOf(Pipeline(id = pipelineId)))
        `when`(jenkins.syncBuilds(dashboardId, pipelineId, startTimestamp)).thenReturn(builds)

        val updateTimestamp = synchronizationService.synchronize(dashboardId)

        verify(jenkins, times(1)).syncBuilds(dashboardId, pipelineId, startTimestamp)

        assertThat(updateTimestamp).isNotNull
    }

    @Test
    internal fun `should not save any builds if fetch data from pipeline failed`() {
        val dashboardId = "fake-dashboard-id"
        val pipelineId = "fake-pipeline-id"
        val lastUpdateRecord = UpdateRecord(
            dashboardId,
            1610668800000
        )

        val startTimestamp = lastUpdateRecord.updateTimestamp - 14 * 24 * 60 * 60 * 1000L

        `when`(updateRepository.getLastUpdate(dashboardId)).thenReturn(lastUpdateRecord)
        `when`(dashboardRepository.getPipelineConfiguration(dashboardId)).thenReturn(listOf(Pipeline(id = pipelineId)))
        `when`(jenkins.syncBuilds(dashboardId, pipelineId, startTimestamp)).thenThrow(RuntimeException())

        val updateTimestamp = synchronizationService.synchronize(dashboardId)

        verify(updateRepository, never()).save(any(UpdateRecord::class.java))

        assertThat(updateTimestamp).isEqualTo(lastUpdateRecord.updateTimestamp)
    }
}