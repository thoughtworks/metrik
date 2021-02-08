package fourkeymetrics.dashboard.controller.vo.request

import fourkeymetrics.common.validation.EnumConstraint
import fourkeymetrics.dashboard.model.Pipeline
import fourkeymetrics.dashboard.model.PipelineType
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class PipelineVerificationRequest(
    @field:NotBlank(message = "url cannot be null or empty")
    val url: String?,
    @field:NotBlank(message = "username cannot be null or empty")
    val username: String?,
    @field:NotBlank(message = "credential cannot be null or empty")
    val credential: String?,
    @field:NotNull(message = "type cannot be null")
    @field:EnumConstraint(acceptedValues = ["JENKINS"], message = "type only allow JENKINS")
    val type: String?,
) {
    fun toPipeline(): Pipeline {
        return with(this) {
            Pipeline(
                username = username!!,
                credential = credential!!,
                url = url!!,
                type = PipelineType.valueOf(type!!)
            )
        }
    }
}