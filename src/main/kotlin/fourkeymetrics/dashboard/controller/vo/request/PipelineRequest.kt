package fourkeymetrics.dashboard.controller.vo.request

import fourkeymetrics.common.validation.EnumConstraint
import fourkeymetrics.dashboard.model.Pipeline
import fourkeymetrics.dashboard.model.PipelineType
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class PipelineRequest(
    @field:NotBlank(message = "name cannot be null or empty")
    val name: String?,
    @field:NotBlank(message = "username cannot be null or empty")
    val username: String?,
    @field:NotBlank(message = "credential cannot be null or empty")
    val credential: String?,
    @field:NotBlank(message = "url cannot be null or empty")
    val url: String?,
    @field:NotNull(message = "type cannot be null")
    @field:EnumConstraint(acceptedValues = ["JENKINS"], message = "type only allow JENKINS")
    var type: String?
) {
    fun toPipeline(dashboardId: String, pipelineId: String): Pipeline {
        return with(this) {
            Pipeline(
                pipelineId,
                dashboardId,
                name!!,
                username!!,
                credential!!,
                url!!,
                PipelineType.valueOf(type!!)
            )
        }
    }
}