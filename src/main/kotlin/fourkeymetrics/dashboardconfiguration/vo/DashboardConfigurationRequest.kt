package fourkeymetrics.dashboardconfiguration.vo

data class DashboardConfigurationRequest(
    val dashboardName: String,
    val pipeline: PipelineConfigurationRequest
)