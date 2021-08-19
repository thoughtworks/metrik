package metrik.project.rest.vo.request

import metrik.project.domain.model.Pipeline
import metrik.project.domain.model.PipelineType
import javax.validation.constraints.NotBlank

class GithubActionsPipelineRequest(
    @field:NotBlank(message = "Name cannot be empty") val name: String,
    @field:NotBlank(message = "Credential cannot be empty") val credential: String,
    url: String
) : PipelineRequest(url, PipelineType.GITHUB_ACTIONS.toString()) {
    override fun toPipeline(projectId: String, pipelineId: String) = Pipeline(
        id = pipelineId,
        projectId = projectId,
        name = name,
        username = null,
        credential = credential,
        url = url,
        type = PipelineType.valueOf(type)
    )
}

class GithubActionsVerificationRequest(
    @field:NotBlank(message = "Credential cannot be null or empty") val credential: String,
    url: String
) : PipelineVerificationRequest(url, PipelineType.GITHUB_ACTIONS.toString()) {
    override fun toPipeline() = Pipeline(
        credential = credential,
        url = url,
        type = PipelineType.valueOf(type)
    )
}
