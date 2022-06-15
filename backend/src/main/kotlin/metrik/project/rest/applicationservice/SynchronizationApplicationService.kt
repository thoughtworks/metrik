package metrik.project.rest.applicationservice

import metrik.project.domain.repository.PipelineRepository
import metrik.project.domain.repository.ProjectRepository
import metrik.project.domain.service.factory.PipelineServiceFactory
import metrik.project.exception.SynchronizationException
import metrik.project.rest.vo.response.SyncProgress
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.CachePut
import org.springframework.stereotype.Service

@Service
class SynchronizationApplicationService(
    @Autowired private val pipelineServiceFactory: PipelineServiceFactory,
    @Autowired private val pipelineRepository: PipelineRepository,
    @Autowired private val projectRepository: ProjectRepository,
) {
    private var logger = LoggerFactory.getLogger(this.javaClass.name)

    @CachePut("4km_cache")
    fun synchronize(projectId: String): Long? {
        val emptyEmitCb: (SyncProgress) -> Unit = { logger.info("emptyProgressEvt") }
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
                pipelineServiceFactory.getService(it.type).syncBuildsProgressively(it, emitCb)
            } catch (
                @Suppress("TooGenericExceptionCaught")
                e: RuntimeException
            ) {
                logger.error("Synchronize failed for pipeline [${it.id} - ${it.name}], error: [$e]")
                throw SynchronizationException("Synchronize failed")
            }
        }
        return projectRepository.updateSynchronizationTime(projectId, currentTimeMillis)
    }

    fun getLastSyncTimestamp(projectId: String): Long? {
        val project = projectRepository.findById(projectId)
        return project.synchronizationTimestamp
    }
}
