package fourkeymetrics.dashboard.controller.vo.request

import fourkeymetrics.common.validation.EnumConstraint
import fourkeymetrics.dashboard.model.Pipeline
import fourkeymetrics.dashboard.model.PipelineType
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

data class PipelineVerificationRequest(
    @field:NotEmpty(message = "name cannot be empty")
    val url: String,
    @field:NotEmpty(message = "username cannot be empty")
    val username: String,
    @field:NotEmpty(message = "credential cannot be empty")
    val credential: String,
    @field:NotNull(message = "type cannot be null")
    @field:EnumConstraint(acceptedValues = ["BAMBOO", "JENKINS"], message = "type only allow BAMBOO and JENKINS")
    val type: String,
) {
    fun toPipeline(): Pipeline {
        return with(this) {
            Pipeline(
                username = username,
                credential = credential,
                url = url,
                type = PipelineType.valueOf(type)
            )
        }
    }
}