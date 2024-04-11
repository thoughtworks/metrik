package metrik.project.rest.vo.request

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import metrik.project.domain.model.PipelineConfiguration
import metrik.project.rest.validation.EnumConstraint
import javax.validation.Valid
import javax.validation.constraints.NotBlank

data class ProjectRequest(
    @field:NotBlank(message = "Project name cannot be empty")
    val projectName: String,
    @field:Valid
    val pipeline: PipelineRequest
)

data class UpdateProjectRequest(
    @field:NotBlank(message = "Project name cannot be empty")
    val projectName: String
)

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = BambooPipelineRequest::class, name = "BAMBOO"),
    JsonSubTypes.Type(value = BambooDeploymentPipelineRequest::class, name = "BAMBOO_DEPLOYMENT"),
    JsonSubTypes.Type(value = JenkinsPipelineRequest::class, name = "JENKINS"),
    JsonSubTypes.Type(value = GithubActionsPipelineRequest::class, name = "GITHUB_ACTIONS"),
    JsonSubTypes.Type(value = AzurePipelinesPipelineRequest::class, name = "AZURE_PIPELINES"),
    JsonSubTypes.Type(value = BuddyPipelineRequest::class, name = "BUDDY")
)
abstract class PipelineRequest(
    @field:NotBlank(message = "URL cannot be empty")
    val url: String,
    @field:EnumConstraint(
        acceptedValues = ["JENKINS", "BAMBOO", "BAMBOO_DEPLOYMENT", "GITHUB_ACTIONS", "AZURE_PIPELINES", "BUDDY"],
        message = "Allowed types: JENKINS, BAMBOO, BAMBOO_DEPLOYMENT, GITHUB_ACTIONS, AZURE_PIPELINES, BUDDY"
    )
    var type: String
) {
    abstract fun toPipeline(projectId: String, pipelineId: String): PipelineConfiguration
}

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = BambooVerificationRequest::class, name = "BAMBOO"),
    JsonSubTypes.Type(value = BambooDeploymentVerificationRequest::class, name = "BAMBOO_DEPLOYMENT"),
    JsonSubTypes.Type(value = JenkinsVerificationRequest::class, name = "JENKINS"),
    JsonSubTypes.Type(value = GithubActionsVerificationRequest::class, name = "GITHUB_ACTIONS"),
    JsonSubTypes.Type(value = AzurePipelinesVerificationRequest::class, name = "AZURE_PIPELINES"),
    JsonSubTypes.Type(value = BuddyVerificationRequest::class, name = "BUDDY"),
)
abstract class PipelineVerificationRequest(
    @field:NotBlank(message = "URL cannot be empty")
    val url: String,
    @field:EnumConstraint(
        acceptedValues = ["JENKINS", "BAMBOO", "BAMBOO_DEPLOYMENT", "GITHUB_ACTIONS", "AZURE_PIPELINES", "BUDDY"],
        message = "Allowed types: JENKINS, BAMBOO, BAMBOO_DEPLOYMENT, GITHUB_ACTIONS, AZURE_PIPELINES, BUDDY"
    )
    val type: String,
) {
    abstract fun toPipeline(): PipelineConfiguration
}
