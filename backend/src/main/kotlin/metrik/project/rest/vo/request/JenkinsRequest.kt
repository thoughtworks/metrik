package metrik.project.rest.vo.request

import metrik.project.domain.model.Pipeline
import metrik.project.domain.model.PipelineType
import javax.validation.constraints.NotBlank

class JenkinsPipelineRequest(
    @field:NotBlank(message = "Name cannot be empty")
    val name: String,
    @field:NotBlank(message = "Username cannot be empty")
    val username: String,
    @field:NotBlank(message = "Credential cannot be empty")
    val credential: String,
    url: String
) : PipelineRequest(url, PipelineType.JENKINS.toString()) {
    override fun toPipeline(projectId: String, pipelineId: String): Pipeline {
        return with(this) {
            Pipeline(
                id = pipelineId,
                projectId = projectId,
                name = name,
                username = username,
                credential = credential,
                url = url,
                type = PipelineType.valueOf(type)
            )
        }
    }
}


class JenkinsVerificationRequest(
    @field:NotBlank(message = "Username cannot be empty")
    val username: String,
    @field:NotBlank(message = "Credential cannot be null or empty")
    val credential: String,
    url: String
) : PipelineVerificationRequest(url, PipelineType.JENKINS.toString()) {
    override fun toPipeline() = with(this) {
        Pipeline(
            username = username,
            credential = credential,
            url = url,
            type = PipelineType.valueOf(type)
        )
    }
}