package fourkeymetrics.datasource.pipeline.configuration.controller.vo

data class DashboardConfigurationRequest(
    val dashboardName: String,
    val pipeline: PipelineConfigurationRequest
)