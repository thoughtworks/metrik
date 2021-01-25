package fourkeymetrics.datasource.pipeline.configuration.vo

data class DashboardConfigurationRequest(
    val dashboardName: String,
    val pipeline: PipelineConfigurationRequest
)