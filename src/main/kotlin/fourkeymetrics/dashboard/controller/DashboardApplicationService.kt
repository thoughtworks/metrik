package fourkeymetrics.dashboard.controller

import fourkeymetrics.dashboard.model.Dashboard
import fourkeymetrics.dashboard.model.Pipeline
import fourkeymetrics.dashboard.model.PipelineType
import fourkeymetrics.dashboard.repository.DashboardRepository
import fourkeymetrics.dashboard.controller.vo.DashboardRequest
import fourkeymetrics.dashboard.controller.vo.PipelineRequest
import fourkeymetrics.exception.ApplicationException
import fourkeymetrics.dashboard.service.jenkins.JenkinsPipelinService
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class DashboardApplicationService {

    @Autowired
    private lateinit var jenkinsPipelineService: JenkinsPipelinService

    @Autowired
    private lateinit var dashboardRepository: DashboardRepository

    fun verifyPipeline(url: String, username: String, credential: String, type: String) {
        if (type == PipelineType.JENKINS.name) {
            jenkinsPipelineService.verifyPipelineConfiguration(url, username, credential)
        } else {
            throw  ApplicationException(HttpStatus.BAD_REQUEST, "Pipeline type not support")
        }
    }

    fun save(config: DashboardRequest): Dashboard {
        verifyPipeline(config.pipeline.url, config.pipeline.username, config.pipeline.token, config.pipeline.type)
        val dashboards = dashboardRepository.getDashBoardDetailByName(config.dashboardName)
        val dashboard: Dashboard
        val pipeline = buildNewPipelineConfiguration(config)
        if (dashboards.isEmpty()) {
            dashboard = Dashboard(
                    name = config.dashboardName,
                    pipelines = listOf(
                            pipeline
                    )
            )
        } else {
            dashboard = dashboards[0]
            val pipelines = dashboard.pipelines.toMutableList()
            pipelines.add(
                pipeline
            )
            dashboard.pipelines = pipelines
        }
        return dashboardRepository.save(dashboard)
    }

    private fun buildNewPipelineConfiguration(config: DashboardRequest): Pipeline {
        return updatePipelineConfig(ObjectId().toString(), config.pipeline)
    }

    fun update(config: PipelineRequest, dashboardId: String, pipelineId: String): Pipeline {
        verifyPipeline(config.url, config.username, config.token, config.type)
        val pipeline = updatePipelineConfig(pipelineId, config)
        return dashboardRepository.updatePipeline(dashboardId, pipelineId, pipeline)

    }

    private fun updatePipelineConfig(
        pipelineId: String,
        config: PipelineRequest
    ): Pipeline {
        val pipeline = Pipeline(
                id = pipelineId,
                name = config.name,
                url = config.url,
                username = config.username,
                credential = config.token,
                type = PipelineType.valueOf(config.type)
        )
        return pipeline
    }
}