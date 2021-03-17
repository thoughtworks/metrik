package fourkeymetrics.project.controller.applicationservice

import fourkeymetrics.exception.BadRequestException
import fourkeymetrics.project.controller.vo.response.PipelineStagesResponse
import fourkeymetrics.project.model.Pipeline
import fourkeymetrics.project.model.PipelineType
import fourkeymetrics.project.repository.BuildRepository
import fourkeymetrics.project.repository.PipelineRepository
import fourkeymetrics.project.repository.ProjectRepository
import fourkeymetrics.project.service.bamboo.BambooPipelineService
import fourkeymetrics.project.service.jenkins.JenkinsPipelineService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PipelineApplicationService {
    @Autowired
    @Qualifier("jenkinsPipelineService")
    private lateinit var jenkinsPipelineService: JenkinsPipelineService

    @Autowired
    @Qualifier("bambooPipelineService")
    private lateinit var bambooPipelineService: BambooPipelineService

    @Autowired
    private lateinit var pipelineRepository: PipelineRepository

    @Autowired
    private lateinit var projectRepository: ProjectRepository

    @Autowired
    private lateinit var buildRepository: BuildRepository

    fun verifyPipelineConfiguration(pipeline: Pipeline) {
        when (pipeline.type) {
            PipelineType.JENKINS -> {
                jenkinsPipelineService.verifyPipelineConfiguration(pipeline)
            }
            PipelineType.BAMBOO -> {
                bambooPipelineService.verifyPipelineConfiguration(pipeline)
            }
            else -> {
                throw BadRequestException("Pipeline type not support")
            }
        }
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
                PipelineStagesResponse(it.id, it.name, jenkinsPipelineService.getStagesSortedByName(it.id))
            }
            .sortedBy { it.pipelineName.toUpperCase() }
    }

    private fun verifyProjectExist(projectId: String) =
        projectRepository.findById(projectId)

    private fun verifyPipelineExist(pipelineId: String, projectId: String) =
        pipelineRepository.findByIdAndProjectId(pipelineId, projectId)
}
