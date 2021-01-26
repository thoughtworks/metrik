package fourkeymetrics.operation

import fourkeymetrics.datasource.UpdateRecord
import fourkeymetrics.datasource.UpdateRepository
import fourkeymetrics.datasource.pipeline.builddata.BuildRepository
import fourkeymetrics.datasource.pipeline.builddata.Jenkins
import fourkeymetrics.metric.model.Build
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.lang.Exception
import java.lang.RuntimeException

@ExtendWith(SpringExtension::class)
@Import(UpdatingService::class)
internal class UpdatingServiceTest {
    @Autowired
    private lateinit var updatingService: UpdatingService

    @MockBean
    private lateinit var buildRepository: BuildRepository

    @MockBean
    private lateinit var updateRepository: UpdateRepository

    @MockBean
    private lateinit var jenkins: Jenkins

    private fun <T> any(type: Class<T>): T = Mockito.any(type)


    @Test
    internal fun `should save all builds when there is no previous update`() {
        val dashboardId = "fake-dashboard-id"
        val pipelineId = "fake-pipeline-id"
        val builds = listOf(Build())

        `when`(updateRepository.getLastUpdate(dashboardId)).thenReturn(null)
        `when`(jenkins.fetchBuilds(dashboardId, pipelineId, 0)).thenReturn(builds)

        val updatedTimestamp = updatingService.update(dashboardId, pipelineId)

        verify(buildRepository, times(1)).save(builds)
        verify(updateRepository, times(1)).save(any(UpdateRecord::class.java))

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
        `when`(jenkins.fetchBuilds(dashboardId, pipelineId, startTimestamp)).thenReturn(builds)

        val updateTimestamp = updatingService.update(dashboardId, pipelineId)

        verify(buildRepository, times(1)).save(builds)
        verify(updateRepository, times(1)).save(any(UpdateRecord::class.java))

        assertThat(updateTimestamp).isNotNull
    }

    @Test
    internal fun `should not save any builds if fetch data from pipeline failed`() {
        val dashboardId = "fake-dashboard-id"
        val pipelineId = "fake-pipeline-id"
        val builds = listOf(Build())
        val lastUpdateRecord = UpdateRecord(
            dashboardId,
            1610668800000
        )

        val startTimestamp = lastUpdateRecord.updateTimestamp - 14 * 24 * 60 * 60 * 1000L

        `when`(updateRepository.getLastUpdate(dashboardId)).thenReturn(lastUpdateRecord)
        `when`(jenkins.fetchBuilds(dashboardId, pipelineId, startTimestamp)).thenThrow(RuntimeException())

        val updateTimestamp = updatingService.update(dashboardId, pipelineId)

        verify(buildRepository, never()).save(builds)
        verify(updateRepository, never()).save(any(UpdateRecord::class.java))

        assertThat(updateTimestamp).isNull()
    }
}