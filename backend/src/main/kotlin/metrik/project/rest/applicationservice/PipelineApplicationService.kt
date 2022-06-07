package metrik.project.rest.applicationservice

import metrik.metrics.exception.BadRequestException
import metrik.project.domain.model.PipelineConfiguration
import metrik.project.domain.repository.BuildRepository
import metrik.project.domain.repository.PipelineRepository
import metrik.project.domain.repository.ProjectRepository
import metrik.project.domain.service.factory.PipelineServiceFactory
import metrik.project.rest.vo.response.PipelineStagesResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PipelineApplicationService(
    @Autowired private val pipelineServiceFactory: PipelineServiceFactory,
    @Autowired private val pipelineRepository: PipelineRepository,
    @Autowired private val projectRepository: ProjectRepository,
    @Autowired private val buildRepository: BuildRepository
) {
    private var logger = LoggerFactory.getLogger(this.javaClass.name)

    fun verifyPipelineConfiguration(pipeline: PipelineConfiguration) {
        logger.info("Started verification for pipeline [name: ${pipeline.name}, url: ${pipeline.url}, " +
                "type: ${pipeline.type}]")

        pipelineServiceFactory.getService(pipeline.type).verifyPipelineConfiguration(pipeline)
    }

    fun createPipeline(pipeline: PipelineConfiguration): PipelineConfiguration {
        verifyProjectExist(pipeline.projectId)
        verifyPipelineNameNotDuplicate(pipeline)
        verifyPipelineConfiguration(pipeline)
        return pipelineRepository.save(pipeline)
    }

    private fun verifyPipelineNameNotDuplicate(
        pipeline: PipelineConfiguration
    ) {
        val foundPipeline = pipelineRepository.findByNameAndProjectId(pipeline.name, pipeline.projectId)
        if (foundPipeline != null && foundPipeline.id != pipeline.id) {
            throw BadRequestException("Pipeline name already exist")
        }
    }

    fun updatePipeline(pipeline: PipelineConfiguration): PipelineConfiguration {
        verifyProjectExist(pipeline.projectId)
        verifyPipelineExist(pipeline.id, pipeline.projectId)
        verifyPipelineNameNotDuplicate(pipeline)
        verifyPipelineConfiguration(pipeline)

        return pipelineRepository.save(pipeline)
    }

    fun getPipeline(projectId: String, pipelineId: String): PipelineConfiguration {
        verifyProjectExist(projectId)
        return pipelineRepository.findByIdAndProjectId(pipelineId, projectId)
    }

    @Transactional
    fun deletePipeline(projectId: String, pipelineId: String) {
        verifyProjectExist(projectId)
        verifyPipelineExist(pipelineId, projectId)
        pipelineRepository.deleteById(pipelineId)
        buildRepository.clear(pipelineId)
    }

    fun getPipelineStages(projectId: String): List<PipelineStagesResponse> {
        verifyProjectExist(projectId)
        return pipelineRepository.findByProjectId(projectId)
            .map {
                PipelineStagesResponse(
                    it.id,
                    it.name,
                    pipelineServiceFactory.getService(it.type).getStagesSortedByName(it.id)
                )
            }
            .sortedBy { it.pipelineName.uppercase() }
    }

    private fun verifyProjectExist(projectId: String) =
        projectRepository.findById(projectId)

    private fun verifyPipelineExist(pipelineId: String, projectId: String) =
        pipelineRepository.findByIdAndProjectId(pipelineId, projectId)
}
