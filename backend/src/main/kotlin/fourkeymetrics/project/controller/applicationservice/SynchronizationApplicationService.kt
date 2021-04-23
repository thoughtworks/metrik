package fourkeymetrics.project.controller.applicationservice

import fourkeymetrics.exception.ApplicationException
import fourkeymetrics.project.repository.PipelineRepository
import fourkeymetrics.project.repository.ProjectRepository
import fourkeymetrics.project.service.factory.PipelineServiceFactory
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class SynchronizationApplicationService(
    @Autowired private val pipelineServiceFactory: PipelineServiceFactory,
    @Autowired private val pipelineRepository: PipelineRepository,
    @Autowired private val projectRepository: ProjectRepository,
) {
    private var logger = LoggerFactory.getLogger(this.javaClass.name)

    fun synchronize(projectId: String): Long? {
        val emptyEmitCb: (SyncProgress) -> Unit = { logger.info("abc") }
        return synchronize(projectId, emptyEmitCb)
    }

    fun synchronize(projectId: String, emitCb: (SyncProgress) -> Unit): Long? {
        logger.info("Started synchronization for project [$projectId]")
        val project = projectRepository.findById(projectId)
        val currentTimeMillis = System.currentTimeMillis()
        val pipelines = pipelineRepository.findByProjectId(project.id)
        logger.info("Synchronizing [${pipelines.size}] pipelines under project [$projectId]")

        pipelines.parallelStream().forEach {
            try {
                pipelineServiceFactory.getService(it.type).syncBuildsProgressively(it.id, emitCb)
            } catch (e: RuntimeException) {
                logger.error("Synchronize failed for pipeline [${it.id}], error: [$e]")
                throw ApplicationException(HttpStatus.INTERNAL_SERVER_ERROR, "Synchronize failed")
            }
        }
        return projectRepository.updateSynchronizationTime(projectId, currentTimeMillis)
    }

    fun getLastSyncTimestamp(projectId: String): Long? {
        val project = projectRepository.findById(projectId)
        return project.synchronizationTimestamp
    }
}