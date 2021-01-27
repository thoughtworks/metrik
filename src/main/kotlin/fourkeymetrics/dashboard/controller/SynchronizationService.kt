package fourkeymetrics.dashboard.controller

import fourkeymetrics.common.model.Build
import fourkeymetrics.dashboard.repository.BuildRepository
import fourkeymetrics.dashboard.service.PipelineService
import fourkeymetrics.datasource.UpdateRecord
import fourkeymetrics.datasource.UpdateRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class SynchronizationService {
    companion object {
        private const val TWO_WEEKS_TIMESTAMP = 14 * 24 * 60 * 60 * 1000L
    }

    @Autowired
    private lateinit var buildRepository: BuildRepository

    @Autowired
    private lateinit var updateRepository: UpdateRepository

    @Autowired
    private lateinit var pipelineService: PipelineService


    fun update(dashboardId: String, pipelineId: String): Long? {
        val lastUpdate = updateRepository.getLastUpdate(dashboardId)
        val currentTimeMillis = System.currentTimeMillis()
        val builds: List<Build>

        try {
            builds = if (lastUpdate == null) {
                pipelineService.syncBuilds(dashboardId, pipelineId, 0)
            } else {
                pipelineService.syncBuilds(
                    dashboardId,
                    pipelineId,
                    lastUpdate.updateTimestamp - TWO_WEEKS_TIMESTAMP
                )
            }
        } catch (e: RuntimeException) {
            return null
        }

        buildRepository.save(builds)
        updateRepository.save(UpdateRecord(dashboardId, currentTimeMillis))

        return currentTimeMillis
    }
}