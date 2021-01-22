package fourkeymetrics.resource.configuration

data class DashboardConfigurationRequest(
    val dashboardName: String,
    val pipeline: PipelineConfigurationRequest
)