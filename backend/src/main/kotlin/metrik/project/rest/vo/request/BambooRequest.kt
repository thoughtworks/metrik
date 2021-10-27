package metrik.project.rest.vo.request

import metrik.project.domain.model.PipelineConfiguration
import metrik.project.domain.model.PipelineType
import javax.validation.constraints.NotBlank

class BambooPipelineRequest(
    @field:NotBlank(message = "Name cannot be empty") val name: String,
    @field:NotBlank(message = "Credential cannot be empty") val credential: String,
    url: String
) : PipelineRequest(url, PipelineType.BAMBOO.toString()) {
    override fun toPipeline(projectId: String, pipelineId: String): PipelineConfiguration {
        return with(this) {
            PipelineConfiguration(
                id = pipelineId,
                projectId = projectId,
                name = name,
                username = null,
                credential = credential,
                url = url,
                type = PipelineType.valueOf(type)
            )
        }
    }
}

class BambooVerificationRequest(
    @field:NotBlank(message = "Credential cannot be null or empty") val credential: String,
    url: String
) : PipelineVerificationRequest(url, PipelineType.BAMBOO.toString()) {
    override fun toPipeline() = with(this) {
        PipelineConfiguration(
            username = null,
            credential = credential,
            url = url,
            type = PipelineType.valueOf(type)
        )
    }
}
