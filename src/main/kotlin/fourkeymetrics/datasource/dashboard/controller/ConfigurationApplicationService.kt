package fourkeymetrics.datasource.dashboard.controller

import fourkeymetrics.datasource.dashboard.model.Dashboard
import fourkeymetrics.datasource.dashboard.model.PipelineConfiguration
import fourkeymetrics.datasource.dashboard.model.PipelineType
import fourkeymetrics.datasource.dashboard.repository.DashboardRepository
import fourkeymetrics.datasource.dashboard.controller.vo.DashboardConfigurationRequest
import fourkeymetrics.datasource.dashboard.controller.vo.PipelineConfigurationRequest
import fourkeymetrics.exception.ApplicationException
import fourkeymetrics.datasource.dashboard.facade.jenkins.JenkinsFacade
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class ConfigurationApplicationService {

    @Autowired
    private lateinit var jenkinsFacade: JenkinsFacade

    @Autowired
    private lateinit var dashboardRepository: DashboardRepository

    fun verifyPipeline(url: String, username: String, credential: String, type: String) {
        if (type == PipelineType.JENKINS.name) {
            jenkinsFacade.verifyPipelineConfiguration(url, username, credential)
        } else {
            throw  ApplicationException(HttpStatus.BAD_REQUEST, "Pipeline type not support")
        }
    }

    fun save(config: DashboardConfigurationRequest): Dashboard {
        verifyPipeline(config.pipeline.url, config.pipeline.username, config.pipeline.token, config.pipeline.type)
        val dashboards = dashboardRepository.getDashBoardDetailByName(config.dashboardName)
        val dashboard: Dashboard
        val pipeline = buildNewPipelineConfiguration(config)
        if (dashboards.isEmpty()) {
            dashboard = Dashboard(
                name = config.dashboardName,
                pipelineConfigurations = listOf(
                    pipeline
                )
            )
        } else {
            dashboard = dashboards[0]
            val pipelines = dashboard.pipelineConfigurations.toMutableList()
            pipelines.add(
                pipeline
            )
            dashboard.pipelineConfigurations = pipelines
        }
        return dashboardRepository.save(dashboard)
    }

    private fun buildNewPipelineConfiguration(config: DashboardConfigurationRequest): PipelineConfiguration {
        return updatePipelineConfig(ObjectId().toString(), config.pipeline)
    }

    fun update(config: PipelineConfigurationRequest, dashboardId: String, pipelineId: String): PipelineConfiguration {
        verifyPipeline(config.url, config.username, config.token, config.type)
        val pipeline = updatePipelineConfig(pipelineId, config)
        return dashboardRepository.updatePipeline(dashboardId, pipelineId, pipeline)

    }

    private fun updatePipelineConfig(
        pipelineId: String,
        config: PipelineConfigurationRequest
    ): PipelineConfiguration {
        val pipeline = PipelineConfiguration(
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