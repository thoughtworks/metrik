package fourkeymetrics.project.controller.vo.request

import javax.validation.Valid
import javax.validation.constraints.NotBlank

data class ProjectRequest(
    @field:NotBlank(message = "projectName cannot be null or empty")
    val projectName: String,
    @field:Valid
    val pipeline: PipelineRequest
)
