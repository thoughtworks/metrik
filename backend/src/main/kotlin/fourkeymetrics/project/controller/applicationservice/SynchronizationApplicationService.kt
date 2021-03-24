package fourkeymetrics.project.controller.applicationservice

import fourkeymetrics.project.repository.ProjectRepository
import fourkeymetrics.project.repository.PipelineRepository
import fourkeymetrics.project.service.PipelineService
import fourkeymetrics.exception.ApplicationException
import fourkeymetrics.project.model.PipelineType
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class SynchronizationApplicationService {
    private var logger = LoggerFactory.getLogger(this.javaClass.name)

    @Autowired
    @Qualifier("jenkinsPipelineService")
    private lateinit var jenkinsPipelineService: PipelineService

    @Autowired
    @Qualifier("bambooPipelineService")
    private lateinit var bambooPipelineService: PipelineService

    @Autowired
    private lateinit var projectRepository: ProjectRepository

    @Autowired
    private lateinit var pipelineRepository: PipelineRepository


    fun synchronize(projectId: String): Long? {
        logger.info("Started synchronization for project [$projectId]")
        val project = projectRepository.findById(projectId)

        val currentTimeMillis = System.currentTimeMillis()
        val pipelines = pipelineRepository.findByProjectId(project.id)

        logger.info("Synchronizing [${pipelines.size}] pipelines under project [$projectId]")
        pipelines.parallelStream().forEach {
            try {
                if(it.type == PipelineType.JENKINS) {
                    jenkinsPipelineService.syncBuilds(it.id)
                } else if(it.type == PipelineType.BAMBOO) {
                    bambooPipelineService.syncBuilds(it.id)
                }
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