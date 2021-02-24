package fourkeymetrics.dashboard.controller.applicationservice

import fourkeymetrics.dashboard.controller.vo.response.PipelineStagesResponse
import fourkeymetrics.dashboard.model.Pipeline
import fourkeymetrics.dashboard.model.PipelineType
import fourkeymetrics.dashboard.repository.BuildRepository
import fourkeymetrics.dashboard.repository.DashboardRepository
import fourkeymetrics.dashboard.repository.PipelineRepository
import fourkeymetrics.dashboard.service.jenkins.JenkinsPipelineService
import fourkeymetrics.exception.BadRequestException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PipelineApplicationService {
    @Autowired
    private lateinit var jenkinsPipelineService: JenkinsPipelineService

    @Autowired
    private lateinit var pipelineRepository: PipelineRepository

    @Autowired
    private lateinit var dashboardRepository: DashboardRepository

    @Autowired
    private lateinit var buildRepository: BuildRepository

    fun verifyPipelineConfiguration(pipeline: Pipeline) {
        if (PipelineType.JENKINS == pipeline.type) {
            jenkinsPipelineService.verifyPipelineConfiguration(
                pipeline.url, pipeline.username, pipeline.credential
            )
        } else {
            throw BadRequestException("Pipeline type not support")
        }
    }

    fun createPipeline(pipeline: Pipeline): Pipeline {
        verifyDashboardExist(pipeline.dashboardId)
        verifyPipelineNameNotDuplicate(pipeline)
        verifyPipelineConfiguration(pipeline)
        return pipelineRepository.save(pipeline)

    }

    private fun verifyPipelineNameNotDuplicate(
        pipeline: Pipeline
    ) {
        if (pipelineRepository.pipelineExistWithNameAndDashboardId(pipeline.name, pipeline.dashboardId)) {
            throw BadRequestException("Pipeline name already exist")
        }
    }

    fun updatePipeline(pipeline: Pipeline): Pipeline {
        verifyDashboardExist(pipeline.dashboardId)
        verifyPipelineExist(pipeline.id, pipeline.dashboardId)
        verifyPipelineNameNotDuplicate(pipeline)
        verifyPipelineConfiguration(pipeline)

        return pipelineRepository.save(pipeline)
    }

    fun getPipeline(dashboardId: String, pipelineId: String): Pipeline {
        verifyDashboardExist(dashboardId)
        return pipelineRepository.findByIdAndDashboardId(pipelineId, dashboardId)
    }

    @Transactional
    fun deletePipeline(dashboardId: String, pipelineId: String) {
        verifyDashboardExist(dashboardId)
        verifyPipelineExist(pipelineId, dashboardId)
        pipelineRepository.deleteById(pipelineId)
        buildRepository.clear(pipelineId)
    }
    fun getPipelineStages(dashboardId: String): List<PipelineStagesResponse> {
        verifyDashboardExist(dashboardId)
        return pipelineRepository.findByDashboardId(dashboardId)
            .map {
                PipelineStagesResponse(it.id, it.name, jenkinsPipelineService.getStagesSortedByName(it.id))
            }
            .sortedBy { it.pipelineName.toUpperCase() }
    }

    private fun verifyDashboardExist(dashboardId: String) =
        dashboardRepository.findById(dashboardId)

    private fun verifyPipelineExist(pipelineId: String, dashboardId: String) =
        pipelineRepository.findByIdAndDashboardId(pipelineId, dashboardId)
}