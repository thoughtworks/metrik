package metrik.project.infrastructure.buddy.feign

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import feign.RequestInterceptor
import feign.codec.Decoder
import metrik.project.domain.service.buddy.ChangeSetDTO
import metrik.project.domain.service.buddy.ExecutionDetailsDTO
import metrik.project.domain.service.buddy.ExecutionPageDTO
import metrik.project.domain.service.buddy.PipelineDTO
import org.springframework.boot.autoconfigure.http.HttpMessageConverters
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder
import org.springframework.cloud.openfeign.support.SpringDecoder
import org.springframework.context.annotation.Bean
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import java.net.URI

@FeignClient(
    value = "buddy-api",
    url = "https://this-is-a-placeholder.com",
    configuration = [BuddyFeignClientConfiguration::class]
)
interface BuddyFeignClient {

    @GetMapping
    fun getPipeline(
        pipelineUrl: URI,
        @RequestHeader("credential") credential: String
    ): PipelineDTO

    @GetMapping("/executions")
    fun getExecutionPage(
        pipelineUrl: URI,
        @RequestHeader("credential") credential: String,
        @RequestParam("page") page: Int
    ): ExecutionPageDTO

    @GetMapping("/executions/{id}")
    fun getExecution(
        pipelineUrl: URI,
        @RequestHeader("credential") credential: String,
        @PathVariable("id") executionId: Long
    ): ExecutionDetailsDTO

    @GetMapping("/repository/comparison/{base}...{head}")
    fun getComparison(
        projectUrl: URI,
        @RequestHeader("credential") credential: String,
        @PathVariable("base") baseRevision: String,
        @PathVariable("head") headRevision: String
    ): ChangeSetDTO
}

class BuddyFeignClientConfiguration {

    @Bean
    fun requestInterceptor() = RequestInterceptor { template ->
        val token = "Bearer " + template!!.headers()["credential"]!!.first()
        template.header("Authorization", token)
        template.removeHeader("credential")
    }

    @Bean
    fun decoder(): Decoder = ResponseEntityDecoder(
        SpringDecoder {
            HttpMessageConverters(MappingJackson2HttpMessageConverter(objectMapper()))
        }
    )

    fun objectMapper() = ObjectMapper().apply {
        registerModule(KotlinModule())
        registerModule(JavaTimeModule())
        disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        propertyNamingStrategy = PropertyNamingStrategy.SNAKE_CASE
    }
}
