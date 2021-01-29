package fourkeymetrics.dashboard.controller.vo

data class DashboardRequest(
    val dashboardName: String,
    val pipelineRequest: PipelineRequest
)