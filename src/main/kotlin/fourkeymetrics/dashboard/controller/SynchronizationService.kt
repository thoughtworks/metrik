package fourkeymetrics.dashboard.controller

import fourkeymetrics.dashboard.model.Pipeline
import fourkeymetrics.dashboard.repository.DashboardRepository
import fourkeymetrics.dashboard.repository.UpdateRepository
import fourkeymetrics.dashboard.service.PipelineService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SynchronizationService {
    companion object {
        private const val TWO_WEEKS_TIMESTAMP = 14 * 24 * 60 * 60 * 1000L
    }

    @Autowired
    private lateinit var updateRepository: UpdateRepository

    @Autowired
    private lateinit var pipelineService: PipelineService

    @Autowired
    private lateinit var dashboardRepository: DashboardRepository


    fun synchronize(dashboardId: String): Long? {
        val lastSyncRecord = updateRepository.getLastUpdate(dashboardId)
        val currentTimeMillis = System.currentTimeMillis()
        val pipelines = getPipelines(dashboardId)

        var synchronizeFailed = false
        pipelines.forEach {
            try {
                if (lastSyncRecord == null) {
                    pipelineService.syncBuilds(dashboardId, it.id, 0)
                } else {
                    pipelineService.syncBuilds(dashboardId, it.id, lastSyncRecord.updateTimestamp - TWO_WEEKS_TIMESTAMP)
                }
            } catch (e: RuntimeException) {
                synchronizeFailed = true
            }
        }

        if (synchronizeFailed) {
            return lastSyncRecord?.updateTimestamp
        }
        return currentTimeMillis
    }

    private fun getPipelines(dashboardId: String): List<Pipeline> {
        return dashboardRepository.getPipelineConfiguration(dashboardId)
    }
}