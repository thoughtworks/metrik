package metrik.project.infrastructure.bamboo.feign

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import metrik.project.domain.service.bamboo.BuildDetailDTO
import metrik.project.domain.service.bamboo.BuildSummaryDTO
import metrik.project.domain.service.bamboo.DeployProjectDTO
import metrik.project.domain.service.bamboo.DeploymentResultsDTO
import metrik.project.domain.service.bamboo.DeploymentVersionBuildResultDTO
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
    private lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun setUp() {
        objectMapper = ObjectMapper().registerModule(KotlinModule())
        objectMapper.registerModule(JavaTimeModule())
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
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

    @Test
    fun `should get plan key while querying for deploy project`() {
        val expectDeployProjectSummaryDTO: DeployProjectDTO =
            objectMapper.readValue(
                javaClass.getResource("/pipeline/bamboo/raw-deploy-project-summary.json")!!.readText()
            )

        mockServer.`when`(
            request()
                .withMethod("GET")
                .withPath("/test/deploy/summary")
                .withHeader("Authorization", "Bearer TestToken")
        ).respond(
            response()
                .withStatusCode(200)
                .withHeader("Content-Type", "application/json; charset=utf-8")
                .withBody(javaClass.getResource("/pipeline/bamboo/raw-deploy-project-summary.json")!!.readText())
        )

        val deployProjectSummary: DeployProjectDTO? =
            bambooFeignClient.getDeploySummary(URI("http://localhost:8787/test/deploy/summary"), "TestToken")

        assertEquals(
            expectDeployProjectSummaryDTO.planKey.key,
            deployProjectSummary!!.planKey.key
        )
        assertEquals(
            expectDeployProjectSummaryDTO.environments[0].name,
            deployProjectSummary.environments[0].name
        )
    }

    @Test
    fun `should get deploy results and serialize right`() {
        val expectedDeploymentResultsDTO: DeploymentResultsDTO =
            objectMapper.readValue(
                javaClass.getResource("/pipeline/bamboo/raw-deploy-results-1.json")!!.readText()
            )

        mockServer.`when`(
            request()
                .withMethod("GET")
                .withPath("/test/deploy/results")
                .withHeader("Authorization", "Bearer TestToken")
        ).respond(
            response()
                .withStatusCode(200)
                .withHeader("Content-Type", "application/json; charset=utf-8")
                .withBody(javaClass.getResource("/pipeline/bamboo/raw-deploy-results-1.json")!!.readText())
        )

        val deploymentResultsDTO: DeploymentResultsDTO? =
            bambooFeignClient.getDeployResults(URI("http://localhost:8787/test/deploy/results"), "TestToken")

        assertEquals(
            expectedDeploymentResultsDTO.results[0].deploymentVersionName,
            deploymentResultsDTO!!.results[0].deploymentVersionName
        )
    }

    @Test
    fun `should return deploy version and serialize right`() {
        val expectedDeploymentVersionBuildResultDTO: DeploymentVersionBuildResultDTO =
            objectMapper.readValue(
                javaClass.getResource("/pipeline/bamboo/raw-deploy-version-1.json")!!.readText()
            )

        mockServer.`when`(
            request()
                .withMethod("GET")
                .withPath("/test/deploy/version")
                .withHeader("Authorization", "Bearer TestToken")
        ).respond(
            response()
                .withStatusCode(200)
                .withHeader("Content-Type", "application/json; charset=utf-8")
                .withBody(javaClass.getResource("/pipeline/bamboo/raw-deploy-version-1.json")!!.readText())
        )

        val deploymentVersionBuildResult: DeploymentVersionBuildResultDTO? =
            bambooFeignClient.getDeployVersionInfo(URI("http://localhost:8787/test/deploy/version"), "TestToken")

        assertEquals(
            expectedDeploymentVersionBuildResultDTO.planResultKey.key,
            deploymentVersionBuildResult!!.planResultKey.key
        )
    }

    @AfterEach
    fun tearDown() {
        mockServer.close()
    }
}
