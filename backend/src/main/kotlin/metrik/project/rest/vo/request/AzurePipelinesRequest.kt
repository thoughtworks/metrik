package metrik.project.rest.vo.request

import metrik.project.domain.model.PipelineConfiguration
import metrik.project.domain.model.PipelineType
import java.net.URL
import javax.validation.constraints.NotBlank

class AzurePipelinesPipelineRequest(
    @field:NotBlank(message = "Name cannot be empty") val name: String,
    @field:NotBlank(message = "Credential cannot be empty") val credential: String,
    url: String
) : PipelineRequest(url, PipelineType.AZURE_PIPELINES.toString()) {
    override fun toPipeline(projectId: String, pipelineId: String) = PipelineConfiguration(
        id = pipelineId,
        projectId = projectId,
        name = name,
        username = null,
        credential = credential,
        url = url,
        type = PipelineType.valueOf(type)
    )
}

class AzurePipelinesVerificationRequest(
    @field:NotBlank(message = "Credential cannot be null or empty") val credential: String,
    url: String
) : PipelineVerificationRequest(url, PipelineType.AZURE_PIPELINES.toString()) {
    override fun toPipeline() = PipelineConfiguration(
        credential = credential,
        url = url,
        type = PipelineType.valueOf(type)
    )
}
