package fourkeymetrics.dashboard.controller.applicationservice

import fourkeymetrics.dashboard.repository.DashboardRepository1
import fourkeymetrics.dashboard.repository.PipelineRepository
import fourkeymetrics.dashboard.service.PipelineService
import fourkeymetrics.exception.ApplicationException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class SynchronizationApplicationService {
    private var logger = LoggerFactory.getLogger(this.javaClass.name)

    @Autowired
    private lateinit var pipelineService: PipelineService

    @Autowired
    private lateinit var dashboardRepository: DashboardRepository1

    @Autowired
    private lateinit var pipelineRepository: PipelineRepository


    fun synchronize(dashboardId: String): Long? {
        val dashboard = dashboardRepository.findById(dashboardId)

        val currentTimeMillis = System.currentTimeMillis()
        val pipelines = pipelineRepository.findByDashboardId(dashboard.id)

        pipelines.parallelStream().forEach {
            try {
                pipelineService.syncBuilds(it.id)
            } catch (e: RuntimeException) {
                logger.error("Synchronize failed for pipeline [${it.id}], error message: [${e.message}]")
                throw ApplicationException(HttpStatus.INTERNAL_SERVER_ERROR, "Synchronize failed")
            }
        }

        return dashboardRepository.updateSynchronizationTime(dashboardId, currentTimeMillis)
    }

    fun getLastSyncTimestamp(dashboardId: String): Long? {
        val dashboard = dashboardRepository.findById(dashboardId)
        return dashboard.synchronizationTimestamp
    }
}