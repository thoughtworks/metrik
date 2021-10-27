package metrik.project.rest.vo.request

import metrik.project.domain.model.PipelineConfiguration
import metrik.project.domain.model.PipelineType
import java.net.URL
import javax.validation.constraints.NotBlank

class GithubActionsPipelineRequest(
    @field:NotBlank(message = "Name cannot be empty") val name: String,
    @field:NotBlank(message = "Credential cannot be empty") val credential: String,
    url: String
) : PipelineRequest(url, PipelineType.GITHUB_ACTIONS.toString()) {
    override fun toPipeline(projectId: String, pipelineId: String) = PipelineConfiguration(
        id = pipelineId,
        projectId = projectId,
        name = name,
        username = null,
        credential = credential,
        url = toGithubActionsUrl(url),
        type = PipelineType.valueOf(type)
    )

    private fun toGithubActionsUrl(url: String) =
        URL(url).path.split("/").let { "$apiRepo/${it[it.size - ownerIndex]}/${it.last()}" }

    private companion object {
        const val ownerIndex = 2
        const val apiRepo = "https://api.github.com/repos"
    }
}

class GithubActionsVerificationRequest(
    @field:NotBlank(message = "Credential cannot be null or empty") val credential: String,
    url: String
) : PipelineVerificationRequest(url, PipelineType.GITHUB_ACTIONS.toString()) {
    override fun toPipeline() = PipelineConfiguration(
        credential = credential,
        url = url,
        type = PipelineType.valueOf(type)
    )
}
