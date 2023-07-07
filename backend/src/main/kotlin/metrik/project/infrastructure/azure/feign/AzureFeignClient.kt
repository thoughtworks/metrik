package metrik.project.infrastructure.azure.feign

import feign.Headers
import feign.RequestInterceptor
import feign.RequestTemplate
import metrik.project.infrastructure.azure.feign.response.MultiplePipelineResponse
import metrik.project.infrastructure.github.feign.response.BranchResponse
import metrik.project.infrastructure.github.feign.response.CommitResponse
import metrik.project.infrastructure.github.feign.response.MultipleRunResponse
import metrik.project.infrastructure.github.feign.response.SingleRunResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import java.util.Base64

@FeignClient(
    value = "azure-api",
    url = "https://dev.azure.com/",
    configuration = [AzureFeignClientConfiguration::class]
)
interface AzureFeignClient {
    @GetMapping("{organization}/{project}/_apis/pipelines?api-version=6.1-preview.1")
    @Headers("Content-Type: application/json")
    fun retrieveMultiplePipelines(
        @RequestHeader("credential") credential: String,
        @PathVariable("organization") organization: String,
        @PathVariable("project") project: String,
    ): MultiplePipelineResponse?
}

class AzureFeignClientConfiguration : RequestInterceptor {
    override fun apply(template: RequestTemplate?) {
        val pat = template!!.headers()["credential"]!!.first()
        val encode = Base64.getEncoder().encodeToString("dora:$pat".encodeToByteArray())
        template.header("Authorization", "Basic $encode")
        template.removeHeader("credential")
    }
}
