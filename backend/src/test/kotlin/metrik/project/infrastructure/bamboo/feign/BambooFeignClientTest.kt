package metrik.project.infrastructure.bamboo.feign

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import metrik.project.domain.service.bamboo.dto.BuildDetailDTO
import metrik.project.domain.service.bamboo.dto.BuildSummaryDTO
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockserver.integration.ClientAndServer
import org.mockserver.integration.ClientAndServer.startClientAndServer
import org.mockserver.junit.jupiter.MockServerExtension
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse.response
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.net.URI

@ExtendWith(MockServerExtension::class)
@SpringBootTest
internal class BambooFeignClientTest(
    @Autowired
    private val bambooFeignClient: BambooFeignClient
) {

    private lateinit var mockServer: ClientAndServer
    private val objectMapper = jacksonObjectMapper()

    @BeforeEach
    fun setUp() {
        objectMapper.registerModule(JavaTimeModule())
        mockServer = startClientAndServer(8787)
    }

    @Test
    fun `should add Bearer token to request while verifying`() {
        mockServer.`when`(
            request()
                .withMethod("GET")
                .withPath("/test/build/rest/api/latest/project/")
                .withHeader("Authorization", "Bearer TestToken")
        ).respond(
            response()
                .withStatusCode(200)
                .withHeader("Content-Type", "application/json; charset=utf-8")
        )

        bambooFeignClient.verify(URI("http://localhost:8787/test/build"), "TestToken")
    }

    @Test
    fun `should add Bearer token and serialize right while querying for build summary`() {
        val expectBuildSummaryDTO: BuildSummaryDTO =
            objectMapper.readValue(javaClass.getResource("/pipeline/bamboo/raw-build-summary-1.json")!!.readText())

        mockServer.`when`(
            request()
                .withMethod("GET")
                .withPath("/test/build")
                .withHeader("Authorization", "Bearer TestToken")
        ).respond(
            response()
                .withStatusCode(200)
                .withHeader("Content-Type", "application/json; charset=utf-8")
                .withBody(javaClass.getResource("/pipeline/bamboo/raw-build-summary-1.json")!!.readText())
        )
        val buildSummary = bambooFeignClient.getMaxBuildNumber(URI("http://localhost:8787/test/build"), "TestToken")
        assertEquals(
            expectBuildSummaryDTO,
            buildSummary
        )
    }

    @Test
    fun `should add Bearer token and serialize right while querying for build details`() {
        val expectBuildDetailsDTO: BuildDetailDTO =
            objectMapper.readValue(javaClass.getResource("/pipeline/bamboo/raw-build-details-1.json")!!.readText())

        mockServer.`when`(
            request()
                .withMethod("GET")
                .withPath("/test/build/detail")
                .withHeader("Authorization", "Bearer TestToken")
        ).respond(
            response()
                .withStatusCode(200)
                .withHeader("Content-Type", "application/json; charset=utf-8")
                .withBody(javaClass.getResource("/pipeline/bamboo/raw-build-details-1.json")!!.readText())
        )

        val buildDetail = bambooFeignClient.getBuildDetails(URI("http://localhost:8787/test/build/detail"), "TestToken")
        assertEquals(
            expectBuildDetailsDTO,
            buildDetail
        )
    }

    @AfterEach
    fun tearDown() {
        mockServer.close()
    }
}