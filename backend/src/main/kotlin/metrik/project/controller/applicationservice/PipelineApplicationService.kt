package metrik.project.controller.applicationservice

import metrik.exception.BadRequestException
import metrik.project.controller.vo.response.PipelineStagesResponse
import metrik.project.model.Pipeline
import metrik.project.repository.BuildRepository
import metrik.project.repository.PipelineRepository
import metrik.project.repository.ProjectRepository
import metrik.project.service.factory.PipelineServiceFactory
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

    fun verifyPipelineConfiguration(pipeline: Pipeline) {
        logger.info("Started verification for pipeline [$pipeline]")

        pipelineServiceFactory.getService(pipeline.type).verifyPipelineConfiguration(pipeline)
    }

    fun createPipeline(pipeline: Pipeline): Pipeline {
        verifyProjectExist(pipeline.projectId)
        verifyPipelineNameNotDuplicate(pipeline)
        verifyPipelineConfiguration(pipeline)
        return pipelineRepository.save(pipeline)

    }

    private fun verifyPipelineNameNotDuplicate(
        pipeline: Pipeline
    ) {
        val foundPipeline = pipelineRepository.findByNameAndProjectId(pipeline.name, pipeline.projectId)
        if (foundPipeline != null && foundPipeline.id != pipeline.id) {
            throw BadRequestException("Pipeline name already exist")
        }
    }

    fun updatePipeline(pipeline: Pipeline): Pipeline {
        verifyProjectExist(pipeline.projectId)
        verifyPipelineExist(pipeline.id, pipeline.projectId)
        verifyPipelineNameNotDuplicate(pipeline)
        verifyPipelineConfiguration(pipeline)

        return pipelineRepository.save(pipeline)
    }

    fun getPipeline(projectId: String, pipelineId: String): Pipeline {
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
            .sortedBy { it.pipelineName.toUpperCase() }
    }

    private fun verifyProjectExist(projectId: String) =
        projectRepository.findById(projectId)

    private fun verifyPipelineExist(pipelineId: String, projectId: String) =
        pipelineRepository.findByIdAndProjectId(pipelineId, projectId)
}
