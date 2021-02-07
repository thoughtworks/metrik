package fourkeymetrics.dashboard.controller.vo.request

import fourkeymetrics.common.validation.EnumConstraint
import fourkeymetrics.dashboard.model.Pipeline
import fourkeymetrics.dashboard.model.PipelineType
import org.apache.logging.log4j.util.Strings
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

data class PipelineRequest(
    @field:NotEmpty(message = "name cannot be empty")
    val name: String = Strings.EMPTY,
    @field:NotEmpty(message = "username cannot be empty")
    val username: String = Strings.EMPTY,
    @field:NotEmpty(message = "credential cannot be empty")
    val credential: String = Strings.EMPTY,
    @field:NotEmpty(message = "url cannot be empty")
    val url: String = Strings.EMPTY,
    @field:NotNull(message = "type cannot be null")
    @field:EnumConstraint(acceptedValues = ["BAMBOO", "JENKINS"], message = "type only allow BAMBOO and JENKINS")
    var type: String = Strings.EMPTY
) {
    fun toPipeline(dashboardId: String, pipelineId: String): Pipeline {
        return with(this) {
            Pipeline(
                pipelineId,
                dashboardId,
                name,
                username,
                credential,
                url,
                PipelineType.valueOf(type)
            )
        }
    }
}