package fourkeymetrics.project.controller.applicationservice

import fourkeymetrics.project.repository.ProjectRepository
import fourkeymetrics.project.repository.PipelineRepository
import fourkeymetrics.project.service.PipelineService
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
    private lateinit var projectRepository: ProjectRepository

    @Autowired
    private lateinit var pipelineRepository: PipelineRepository


    fun synchronize(projectId: String): Long? {
        val project = projectRepository.findById(projectId)

        val currentTimeMillis = System.currentTimeMillis()
        val pipelines = pipelineRepository.findByProjectId(project.id)

        pipelines.parallelStream().forEach {
            try {
                pipelineService.syncBuilds(it.id)
            } catch (e: RuntimeException) {
                logger.error("Synchronize failed for pipeline [${it.id}], error message: [${e.message}]")
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