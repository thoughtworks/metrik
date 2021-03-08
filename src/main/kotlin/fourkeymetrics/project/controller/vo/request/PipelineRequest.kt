package fourkeymetrics.project.controller.vo.request

import fourkeymetrics.common.validation.EnumConstraint
import fourkeymetrics.project.model.Pipeline
import fourkeymetrics.project.model.PipelineType
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class PipelineRequest(
    @field:NotBlank(message = "name cannot be null or empty")
    val name: String?,
    val username: String?,
    @field:NotBlank(message = "credential cannot be null or empty")
    val credential: String?,
    @field:NotBlank(message = "url cannot be null or empty")
    val url: String?,
    @field:NotNull(message = "type cannot be null")
    @field:EnumConstraint(acceptedValues = ["JENKINS", "BAMBOO"], message = "type only allow JENKINS and BAMBOO")
    var type: String?
) {
    fun toPipeline(projectId: String, pipelineId: String): Pipeline {
        return with(this) {
            Pipeline(
                pipelineId,
                projectId,
                name!!,
                username,
                credential!!,
                url!!,
                PipelineType.valueOf(type!!)
            )
        }
    }
}