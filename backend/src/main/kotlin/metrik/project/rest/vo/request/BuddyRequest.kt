package metrik.project.rest.vo.request

import metrik.project.domain.model.PipelineConfiguration
import metrik.project.domain.model.PipelineType
import javax.validation.constraints.NotBlank

class BuddyPipelineRequest(
    @field:NotBlank(message = "Name cannot be empty")
    val name: String,
    @field:NotBlank(message = "Credential cannot be null or empty") val credential: String,
    url: String
) : PipelineRequest(url, PipelineType.BUDDY.toString()) {
    override fun toPipeline(projectId: String, pipelineId: String) = PipelineConfiguration(
        id = pipelineId,
        projectId = projectId,
        name = name,
        credential = credential,
        url = url,
        type = PipelineType.valueOf(type)
    )
}

class BuddyVerificationRequest(
    @field:NotBlank(message = "Credential cannot be null or empty") val credential: String,
    url: String
) : PipelineVerificationRequest(url, PipelineType.BUDDY.toString()) {
    override fun toPipeline() = PipelineConfiguration(
        credential = credential,
        url = url,
        type = PipelineType.valueOf(type)
    )
}
