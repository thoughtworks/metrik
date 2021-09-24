package metrik.project.infrastructure.bamboo.feign

import feign.Headers
import feign.RequestInterceptor
import feign.RequestTemplate
import metrik.project.domain.service.bamboo.dto.BuildDetailDTO
import metrik.project.domain.service.bamboo.dto.BuildSummaryDTO
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
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
}

class BambooFeignClientConfiguration : RequestInterceptor {
    override fun apply(template: RequestTemplate?) {
        (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes?)?.request
        val token = "Bearer " + template!!.headers()["credential"]!!.first()
        template.header("Authorization", token)
        template.removeHeader("credential")
    }
}