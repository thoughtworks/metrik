package fourkeymetrics.project.controller.vo.request

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import fourkeymetrics.common.validation.EnumConstraint
import fourkeymetrics.project.model.Pipeline
import javax.validation.Valid
import javax.validation.constraints.NotBlank

data class ProjectRequest(
    @field:NotBlank(message = "Project name cannot be empty")
    val projectName: String,
    @field:Valid
    val pipeline: PipelineRequest
)


@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = BambooPipelineRequest::class, name = "BAMBOO"),
    JsonSubTypes.Type(value = JenkinsPipelineRequest::class, name = "JENKINS")
)
abstract class PipelineRequest(
    @field:NotBlank(message = "URL cannot be empty")
    val url: String,
    @field:EnumConstraint(acceptedValues = ["JENKINS", "BAMBOO"], message = "type only allow JENKINS and BAMBOO")
    var type: String
) {
    abstract fun toPipeline(projectId: String, pipelineId: String): Pipeline
}


@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = BambooVerificationRequest::class, name = "BAMBOO"),
    JsonSubTypes.Type(value = JenkinsVerificationRequest::class, name = "JENKINS")
)
abstract class PipelineVerificationRequest(
    @field:NotBlank(message = "URL cannot be empty")
    val url: String,
    @field:EnumConstraint(acceptedValues = ["JENKINS", "BAMBOO"], message = "type only allow JENKINS")
    val type: String,
) {
    abstract fun toPipeline(): Pipeline
}