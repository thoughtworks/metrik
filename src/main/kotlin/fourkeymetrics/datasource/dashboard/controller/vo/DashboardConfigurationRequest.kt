package fourkeymetrics.datasource.dashboard.controller.vo

data class DashboardConfigurationRequest(
    val dashboardName: String,
    val pipeline: PipelineConfigurationRequest
)