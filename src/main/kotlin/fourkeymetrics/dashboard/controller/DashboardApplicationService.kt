package fourkeymetrics.dashboard.controller

import fourkeymetrics.dashboard.controller.vo.DashboardRequest
import fourkeymetrics.dashboard.controller.vo.PipelineRequest
import fourkeymetrics.dashboard.controller.vo.PipelineStagesResponse
import fourkeymetrics.dashboard.controller.vo.PipelineVerificationRequest
import fourkeymetrics.dashboard.exception.DashboardNotFoundException
import fourkeymetrics.dashboard.exception.PipelineNotFoundException
import fourkeymetrics.dashboard.model.Dashboard
import fourkeymetrics.dashboard.model.Pipeline
import fourkeymetrics.dashboard.model.PipelineType
import fourkeymetrics.dashboard.repository.DashboardRepository
import fourkeymetrics.dashboard.service.jenkins.JenkinsPipelineService
import fourkeymetrics.exception.ApplicationException
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import kotlin.streams.toList

@SuppressWarnings("TooManyFunctions")
@Service
class DashboardApplicationService {
    @Autowired
    private lateinit var jenkinsPipelineService: JenkinsPipelineService

    @Autowired
    private lateinit var dashboardRepository: DashboardRepository

    fun createDashboard(dashboardRequest: DashboardRequest): Dashboard {
        val dashboardName = dashboardRequest.dashboardName
        val dashboards = dashboardRepository.getDashBoardDetailByName(dashboardName)
        return if (dashboards.isEmpty()) {
            val pipelines = with(dashboardRequest.pipelineRequest) {
                listOf(Pipeline(ObjectId().toString(), name, username, credential, url, type))
            }
            val dashboard = Dashboard(name = dashboardName, id = ObjectId().toString(), pipelines = pipelines)
            dashboardRepository.save(dashboard)
        } else throw ApplicationException(HttpStatus.FORBIDDEN, "Dashboard [name=$dashboardName] already existing")
    }

    fun updateDashboardName(dashboardId: String, dashboardName: String): Dashboard {
        val dashboard = getDashboard(dashboardId)
        dashboard.name = dashboardName
        return dashboardRepository.save(dashboard)
    }

    fun getDashboards(): List<Dashboard> {
        return dashboardRepository.getDashboards()
    }

    fun deleteDashboard(dashboardId: String) {
        dashboardRepository.deleteDashboard(dashboardId)
    }

    fun verifyPipeline(pipelineVerificationRequest: PipelineVerificationRequest) {
        with(pipelineVerificationRequest) {
            verifyPipeline(url, username, credential, type)
        }
    }

    fun createPipeline(dashboardId: String, pipelineRequest: PipelineRequest): Dashboard {
        val dashboard = getDashboard(dashboardId)
        val updatedPipelines = dashboard.pipelines.toMutableList()
        val pipeline =
            with(pipelineRequest) { Pipeline(ObjectId.get().toString(), name, username, credential, url, type) }
        updatedPipelines.add(pipeline)
        dashboard.pipelines = updatedPipelines
        return dashboardRepository.save(dashboard)
    }

    fun updatePipeline(dashboardId: String, pipelineId: String, pipelineRequest: PipelineRequest): Pipeline {
        with(pipelineRequest) { verifyPipeline(url, username, credential, type) }
        val pipeline =
            with(pipelineRequest) { Pipeline(pipelineId, name, username, credential, url, type) }

        return dashboardRepository.updatePipeline(dashboardId, pipelineId, pipeline)
    }

    fun getPipeline(dashboardId: String, pipelineId: String): Pipeline =
        dashboardRepository
            .getPipelineConfiguration(dashboardId, pipelineId)
            ?: throw PipelineNotFoundException(
                HttpStatus.NOT_FOUND,
                "Pipeline [id=$pipelineId] of dashboard [id=$dashboardId] is not existing"
            )

    fun deletePipeline(dashboardId: String, pipelineId: String) {
        dashboardRepository.deletePipeline(dashboardId, pipelineId)
    }

    fun getPipelineStages(dashboardId: String): List<PipelineStagesResponse> {
        val dashboard = getDashboard(dashboardId)

        return dashboardRepository.getPipelinesByDashboardId(dashboard.id).parallelStream().map {
            PipelineStagesResponse(it.name, jenkinsPipelineService.getStagesSortedByName(it.id))
        }.toList().sortedBy { it.pipelineName }.sortedBy { it.pipelineName.toUpperCase() }
    }

    private fun verifyPipeline(url: String, username: String, credential: String, type: PipelineType) {
        if (PipelineType.JENKINS == type) {
            jenkinsPipelineService.verifyPipelineConfiguration(url, username, credential)
        } else {
            throw ApplicationException(HttpStatus.BAD_REQUEST, "Pipeline type not support")
        }
    }

    private fun getDashboard(dashboardId: String): Dashboard =
        dashboardRepository.getDashBoardDetailById(dashboardId) ?: throw DashboardNotFoundException(
            HttpStatus.NOT_FOUND,
            "Dashboard [id=$dashboardId] is not existing"
        )
}