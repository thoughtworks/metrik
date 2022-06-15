package metrik.project.infrastructure.bamboo.feign

import feign.Headers
import feign.RequestInterceptor
import feign.RequestTemplate
import metrik.project.domain.service.bamboo.BuildDetailDTO
import metrik.project.domain.service.bamboo.BuildSummaryDTO
import metrik.project.domain.service.bamboo.DeployProjectDTO
import metrik.project.domain.service.bamboo.DeploymentResultsDTO
import metrik.project.domain.service.bamboo.DeploymentVersionBuildResultDTO
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import java.net.URI

@FeignClient(
    value = "bamboo-api",
    url = "https://this-is-a-placeholder.com",
    configuration = [BambooFeignClientConfiguration::class]
)
interface BambooFeignClient {
    @GetMapping(path = ["/rest/api/latest/project/"])
    @Headers("Connection: close")
    fun verify(
        baseUrl: URI,
        @RequestHeader("credential") credential: String,
    )

    @GetMapping
    fun getMaxBuildNumber(
        baseUrl: URI,
        @RequestHeader("credential") credential: String,
    ): BuildSummaryDTO?

    @GetMapping
    fun getBuildDetails(
        baseUrl: URI,
        @RequestHeader("credential") credential: String,
    ): BuildDetailDTO?

    @GetMapping
    fun getDeploySummary(
        baseUrl: URI,
        @RequestHeader("credential") credential: String
    ): DeployProjectDTO?

    @GetMapping
    fun getDeployResults(
        baseUrl: URI,
        @RequestHeader("credential") credential: String
    ): DeploymentResultsDTO?

    @GetMapping
    fun getDeployVersionInfo(
        baseUrl: URI,
        @RequestHeader("credential") credential: String
    ): DeploymentVersionBuildResultDTO?
}

class BambooFeignClientConfiguration : RequestInterceptor {
    override fun apply(template: RequestTemplate?) {
        val token = "Bearer " + template!!.headers()["credential"]!!.first()
        template.header("Authorization", token)
        template.removeHeader("credential")
    }
}
