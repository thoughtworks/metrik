package fourkeymetrics.dashboard.controller.vo.request

import javax.validation.Valid
import javax.validation.constraints.NotBlank

data class DashboardRequest(
    @field:NotBlank(message = "dashboardName cannot be empty")
    val dashboardName: String,
    @field:Valid
    val pipeline: PipelineRequest
)
