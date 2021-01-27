package fourkeymetrics.dashboard.controller

import fourkeymetrics.dashboard.service.PipelineService
import fourkeymetrics.datasource.UpdateRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class SynchronizationService {
    companion object {
        private const val TWO_WEEKS_TIMESTAMP = 14 * 24 * 60 * 60 * 1000L
    }

    @Autowired
    private lateinit var updateRepository: UpdateRepository

    @Autowired
    private lateinit var pipelineService: PipelineService


    fun update(dashboardId: String, pipelineId: String): Long? {
        val lastUpdate = updateRepository.getLastUpdate(dashboardId)
        val currentTimeMillis = System.currentTimeMillis()

        try {
            if (lastUpdate == null) {
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

        return currentTimeMillis
    }
}