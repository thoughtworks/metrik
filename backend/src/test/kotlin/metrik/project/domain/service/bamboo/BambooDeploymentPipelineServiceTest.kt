package metrik.project.domain.service.bamboo

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.ninjasquad.springmockk.MockkBean
import feign.FeignException
import feign.Request
import feign.Response
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import metrik.exception.ApplicationException
import metrik.project.domain.model.Commit
import metrik.project.domain.model.Execution
import metrik.project.domain.model.PipelineConfiguration
import metrik.project.domain.model.PipelineType
import metrik.project.domain.model.Stage
import metrik.project.domain.model.Status
import metrik.project.domain.repository.BuildRepository
import metrik.project.infrastructure.bamboo.feign.BambooFeignClient
import metrik.project.rest.vo.response.SyncProgress
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.web.client.RestTemplate
import java.net.URI

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
@ExtendWith(SpringExtension::class)
@Import(BambooDeploymentPipelineService::class, RestTemplate::class)
internal class BambooDeploymentPipelineServiceTest {
    @Autowired
    private lateinit var bambooDeploymentPipelineService: BambooDeploymentPipelineService

    @MockkBean(relaxed = true)
    private lateinit var buildRepository: BuildRepository

    @MockkBean(relaxed = true)
    private lateinit var bambooFeignClient: BambooFeignClient

    private val mockEmitCb = mockk<(SyncProgress) -> Unit>(relaxed = true)

    private lateinit var objectMapper: ObjectMapper

    private val pipelineId = "1"
    private val credential = "fake-credential"
    private val baseUrl = "http://localhost:8085"
    private val planKey = "SEC-DEP"
    private val userInputURL = "http://localhost:8085/deploy/viewDeploymentProjectEnvironments.action?id=1736705"
    private val deploymentProjectId = "1736705"
    private val pipelineName = "TEST"
    private val latestDeployTimestamp: Long = 1630501012686
    private val pipeline = PipelineConfiguration(
        id = pipelineId,
        name = pipelineName,
        credential = credential,
        url = userInputURL,
        type = PipelineType.BAMBOO
    )
    private val stageDefault = Stage(
        name = "Default Stage",
        status = Status.SUCCESS,
        startTimeMillis = 1631500510541,
        durationMillis = 29,
        pauseDurationMillis = 0,
        completedTimeMillis = 1631500510570
    )
    private val stageDevFailed = Stage(
        name = "dev",
        status = Status.FAILED,
        startTimeMillis = 1631501012824,
        durationMillis = 122,
        pauseDurationMillis = 0,
        completedTimeMillis = 1631501013129
    )
    private val stageDevSuccess1 = Stage(
        name = "dev",
        status = Status.SUCCESS,
        startTimeMillis = 1631501045640,
        durationMillis = 5,
        pauseDurationMillis = 0,
        completedTimeMillis = 1631501046208
    )
    private val stageStagingSuccess = Stage(
        name = "staging",
        status = Status.SUCCESS,
        startTimeMillis = 1631501066171,
        durationMillis = 7,
        pauseDurationMillis = 0,
        completedTimeMillis = 1631501066302
    )
    private val stageDevSuccess2 = Stage(
        name = "dev",
        status = Status.SUCCESS,
        startTimeMillis = 1631513979524,
        durationMillis = 43,
        pauseDurationMillis = 0,
        completedTimeMillis = 1631513980345
    )
    private val commit = Commit(
        commitId = "42c1d1e4314ea05de253548ed4b42f81e4d2cd88",
        timestamp = 1631500436000,
        date = "2021-09-13T02:33:56Z[UTC]"
    )

    private val dummyFeignRequest = Request.create(Request.HttpMethod.POST, "url", mapOf(), null, null, null)

    @BeforeEach
    fun setUp() {
        objectMapper = ObjectMapper().registerModule(KotlinModule())
        objectMapper.registerModule(JavaTimeModule())
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        val builds = listOf(
            Execution(
                stages = listOf(
                    Stage(name = "clone"), Stage(name = "build"),
                    Stage(name = "zzz"), Stage(name = "amazing")
                )
            ),
            Execution(
                stages = listOf(
                    Stage(name = "build"), Stage("good")
                )
            )
        )
        val buildSummaryDTO: BuildSummaryDTO = objectMapper.readValue(
            javaClass.getResource("/pipeline/bamboo/raw-deploy-build-summary.json")
                .readText()
        )
        val buildDetailsDTO1: BuildDetailDTO = objectMapper.readValue(
            javaClass.getResource("/pipeline/bamboo/raw-deploy-build-details-1.json")
                .readText()
        )
        val buildDetailsDTO2: BuildDetailDTO = objectMapper.readValue(
            javaClass.getResource("/pipeline/bamboo/raw-deploy-build-details-2.json")
                .readText()
        )
        val buildDetailsDTO3: BuildDetailDTO = objectMapper.readValue(
            javaClass.getResource("/pipeline/bamboo/raw-deploy-build-details-3.json")
                .readText()
        )
        val buildDetailsDTO4: BuildDetailDTO = objectMapper.readValue(
            javaClass.getResource("/pipeline/bamboo/raw-deploy-build-details-4.json")
                .readText()
        )
        val buildDetailsDTO5: BuildDetailDTO = objectMapper.readValue(
            javaClass.getResource("/pipeline/bamboo/raw-deploy-build-details-5.json")
                .readText()
        )
        val deploymentProjectDTO: DeployProjectDTO =
            objectMapper.readValue(
                javaClass.getResource("/pipeline/bamboo/raw-deploy-project-summary.json")!!.readText()
            )
        val deployResults1: DeploymentResultsDTO =
            objectMapper.readValue(
                javaClass.getResource("/pipeline/bamboo/raw-deploy-results-1.json")!!.readText()
            )
        val deployResults2: DeploymentResultsDTO =
            objectMapper.readValue(
                javaClass.getResource("/pipeline/bamboo/raw-deploy-results-2.json")!!.readText()
            )
        val deployVersion1: DeploymentVersionBuildResultDTO =
            objectMapper.readValue(
                javaClass.getResource("/pipeline/bamboo/raw-deploy-version-1.json")!!.readText()
            )
        val deployVersion2: DeploymentVersionBuildResultDTO =
            objectMapper.readValue(
                javaClass.getResource("/pipeline/bamboo/raw-deploy-version-2.json")!!.readText()
            )
        every { buildRepository.getAllBuilds(pipelineId) } returns builds
        every { buildRepository.getLatestDeployTimestamp(pipelineId) } returns latestDeployTimestamp
        every { buildRepository.getBambooJenkinsBuildNumbersNeedSync(pipelineId, 1) } returns listOf(1)
        every {
            bambooFeignClient.getDeploySummary(
                URI("$baseUrl/rest/api/latest/deploy/project/$deploymentProjectId"),
                credential
            )
        } returns deploymentProjectDTO
        every {
            bambooFeignClient.getDeployResults(
                URI("$baseUrl/rest/api/latest/deploy/environment/1802241/results?expand"),
                credential
            )
        } returns deployResults1
        every {
            bambooFeignClient.getDeployResults(
                URI("$baseUrl/rest/api/latest/deploy/environment/1802242/results?expand"),
                credential
            )
        } returns deployResults2
        every {
            bambooFeignClient.getDeployVersionInfo(
                URI("$baseUrl/rest/api/latest/deploy/version/1835009/build-result"),
                credential
            )
        } returns deployVersion1
        every {
            bambooFeignClient.getDeployVersionInfo(
                URI("$baseUrl/rest/api/latest/deploy/version/2064385/build-result"),
                credential
            )
        } returns deployVersion2
        every {
            bambooFeignClient.getMaxBuildNumber(
                URI("$baseUrl/rest/api/latest/result/$planKey.json"),
                credential
            )
        } returns buildSummaryDTO
        every {
            bambooFeignClient.getBuildDetails(
                URI("$baseUrl/rest/api/latest/result/$planKey-1.json?expand=changes.change,stages.stage.results"),
                credential
            )
        } returns buildDetailsDTO1
        every {
            bambooFeignClient.getBuildDetails(
                URI("$baseUrl/rest/api/latest/result/$planKey-2.json?expand=changes.change,stages.stage.results"),
                credential
            )
        } returns buildDetailsDTO2
        every {
            bambooFeignClient.getBuildDetails(
                URI("$baseUrl/rest/api/latest/result/$planKey-3.json?expand=changes.change,stages.stage.results"),
                credential
            )
        } returns buildDetailsDTO3
        every {
            bambooFeignClient.getBuildDetails(
                URI("$baseUrl/rest/api/latest/result/$planKey-4.json?expand=changes.change,stages.stage.results"),
                credential
            )
        } returns buildDetailsDTO4
        every {
            bambooFeignClient.getBuildDetails(
                URI("$baseUrl/rest/api/latest/result/$planKey-5.json?expand=changes.change,stages.stage.results"),
                credential
            )
        } returns buildDetailsDTO5
    }

    @Test
    fun `should throw exception when verify pipeline given response is 500`() {
        every {
            bambooFeignClient.verify(any(), any())
        } throws FeignException.errorStatus(
            "GET",
            buildFeignResponse(500)
        )
        Assertions.assertThrows(ApplicationException::class.java) {
            bambooDeploymentPipelineService.verifyPipelineConfiguration(
                PipelineConfiguration(
                    credential = credential,
                    url = userInputURL
                )
            )
        }
    }

    @Test
    fun `should throw exception when verify pipeline given response is 400`() {
        every {
            bambooFeignClient.verify(any(), any())
        } throws FeignException.errorStatus(
            "GET",
            buildFeignResponse(400)
        )
        Assertions.assertThrows(ApplicationException::class.java) {
            bambooDeploymentPipelineService.verifyPipelineConfiguration(
                PipelineConfiguration(
                    credential = credential,
                    url = userInputURL
                )
            )
        }
    }

    @Test
    fun `should return sorted stage name lists when getStagesSortedByName() called`() {
        val result = bambooDeploymentPipelineService.getStagesSortedByName(pipelineId)

        assertEquals(5, result.size)
        assertEquals("amazing", result[0])
        assertEquals("build", result[1])
        assertEquals("clone", result[2])
        assertEquals("good", result[3])
        assertEquals("zzz", result[4])
    }

    @Test
    fun `should throw exception when sync pipeline given response is 500`() {
        every {
            bambooFeignClient.getDeploySummary(any(), any())
        } throws FeignException.errorStatus(
            "GET",
            buildFeignResponse(500)
        )

        Assertions.assertThrows(ApplicationException::class.java) {
            bambooDeploymentPipelineService.syncBuildsProgressively(pipeline, mockEmitCb)
        }
    }

    @Test
    fun `should throw exception when sync pipeline given response is 400`() {
        every {
            bambooFeignClient.getDeploySummary(any(), any())
        } throws FeignException.errorStatus(
            "GET",
            buildFeignResponse(400)
        )

        Assertions.assertThrows(ApplicationException::class.java) {
            bambooDeploymentPipelineService.syncBuildsProgressively(pipeline, mockEmitCb)
        }
    }

    @Test
    fun `should throw exception when querying for build detail giving response other than 404`() {
        every {
            bambooFeignClient.getDeploySummary(any(), any())
        } throws FeignException.errorStatus(
            "GET",
            buildFeignResponse(400)
        )

        Assertions.assertThrows(ApplicationException::class.java) {
            bambooDeploymentPipelineService.syncBuildsProgressively(
                PipelineConfiguration(
                    credential = credential,
                    url = userInputURL
                ),
                mockEmitCb
            )
        }
    }

    @Test
    fun `should not save when build detail return null`() {
        every { bambooFeignClient.getBuildDetails(any(), any()) } throws FeignException.errorStatus(
            "GET",
            buildFeignResponse(404)
        )

        val result = bambooDeploymentPipelineService.syncBuildsProgressively(pipeline, mockEmitCb)

        assertEquals(emptyList<Execution>(), result)
    }

    @Test
    fun `should successfully sync a build deploy for many times`() {

        bambooDeploymentPipelineService.syncBuildsProgressively(pipeline, mockEmitCb)

        val build =
            Execution(
                pipelineId = pipelineId,
                number = 3,
                result = Status.SUCCESS,
                duration = 1305,
                timestamp = 1631500509343,
                url = "$baseUrl/rest/api/latest/result/$planKey-3",
                stages = listOf(stageDefault, stageDevFailed, stageDevSuccess1, stageStagingSuccess, stageDevSuccess2),
                changeSets = listOf(commit)
            )
        verify(exactly = 1) { buildRepository.save(build) }
    }

    private fun buildFeignResponse(statusCode: Int) =
        Response.builder().status(statusCode).request(dummyFeignRequest).build()
}
