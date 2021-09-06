package metrik.project.domain.service.jenkins

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.ninjasquad.springmockk.MockkBean
import feign.FeignException
import feign.Request
import feign.Response
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import metrik.exception.ApplicationException
import metrik.project.domain.model.Build
import metrik.project.domain.model.Pipeline
import metrik.project.domain.model.Stage
import metrik.project.domain.model.Status
import metrik.project.domain.repository.BuildRepository
import metrik.project.domain.service.jenkins.dto.BuildDetailsDTO
import metrik.project.domain.service.jenkins.dto.BuildSummaryCollectionDTO
import metrik.project.infrastructure.jenkins.feign.JenkinsFeignClient
import metrik.project.rest.vo.response.SyncProgress
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.web.client.RestTemplate

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
@ExtendWith(SpringExtension::class, MockKExtension::class)
@Import(JenkinsPipelineService::class, RestTemplate::class)
@RestClientTest
internal class JenkinsPipelineServiceTest {
    @Autowired
    private lateinit var jenkinsPipelineService: JenkinsPipelineService

    @MockkBean(relaxed = true)
    private lateinit var jenkinsFeignClient: JenkinsFeignClient

    @MockkBean(relaxed = true)
    private lateinit var buildRepository: BuildRepository

    private val mockEmitCb = mockk<(SyncProgress) -> Unit>(relaxed = true)

    private val objectMapper = jacksonObjectMapper()

    private val pipelineId = "fake pipeline"

    private val dummyFeignRequest = Request.create(Request.HttpMethod.POST, "url", mapOf(), null, null, null)

    @BeforeEach
    fun setUp() {
        every { buildRepository.getAllBuilds("fake pipeline") } returns listOf(
            Build(
                stages = listOf(
                    Stage(name = "clone"), Stage(name = "build"),
                    Stage(name = "zzz"), Stage(name = "amazing")
                )
            ),
            Build(
                stages = listOf(
                    Stage(name = "build"), Stage("good")
                )
            )
        )
        every { buildRepository.getByBuildNumber(any(), any()) } returns null
    }

    @Test
    fun `should throw exception when verify pipeline given response is 400`() {
        val username = "fake-user"
        val credential = "fake-credential"
        val baseUrl = "http://localhost"
        every {
            jenkinsFeignClient.verifyJenkinsUrl(any(), any())
        } throws FeignException.errorStatus(
            "GET",
            buildFeignResponse(400)
        )
        assertThrows(ApplicationException::class.java) {
            jenkinsPipelineService.verifyPipelineConfiguration(
                Pipeline(
                    id = "fake pipeline",
                    username = username,
                    credential = credential,
                    url = baseUrl
                )
            )
        }
    }

    @Test
    fun `should throw exception when verify pipeline given response is 500`() {
        val username = "fake-user"
        val credential = "fake-credential"
        val baseUrl = "http://localhost"
        every {
            jenkinsFeignClient.verifyJenkinsUrl(any(), any())
        } throws FeignException.errorStatus(
            "GET",
            buildFeignResponse(500)
        )
        assertThrows(ApplicationException::class.java) {
            jenkinsPipelineService.verifyPipelineConfiguration(
                Pipeline(
                    id = "fake pipeline",
                    username = username,
                    credential = credential,
                    url = baseUrl
                )
            )
        }
    }

    @Test
    fun `should return sorted stage name lists when getStagesSortedByName() called`() {
        val result = jenkinsPipelineService.getStagesSortedByName("fake pipeline")

        assertEquals(5, result.size)
        assertEquals("amazing", result[0])
        assertEquals("build", result[1])
        assertEquals("clone", result[2])
        assertEquals("good", result[3])
        assertEquals("zzz", result[4])
    }

    @Test
    fun `should return all builds and emit the progress event given syncBuildsProgressively() given pipelineID`() {
        val pipelineName = "pipeline name"
        val pipelineId = "fake pipeline"
        val username = "fake-user"
        val credential = "fake-credential"
        val baseUrl = "http://localhost"
        val pipeline = Pipeline(
            id = pipelineId,
            username = username,
            credential = credential,
            url = baseUrl,
            name = pipelineName
        )

        val buildSummaryCollectionDTO: BuildSummaryCollectionDTO =
            objectMapper.readValue(
                javaClass.getResource("/pipeline/jenkins/raw-build-summary-1.json")
                    .readText()
            )
        val buildDetailsDTO: BuildDetailsDTO = objectMapper.readValue(
            javaClass.getResource("/pipeline/jenkins/raw-build-detail-1.json")
                .readText()
        )

        every {
            jenkinsFeignClient.retrieveBuildSummariesFromJenkins(any(), any())
        } returns buildSummaryCollectionDTO

        every {
            jenkinsFeignClient.retrieveBuildDetailsFromJenkins(any(), any(), any())
        } returns buildDetailsDTO


        val expectedBuilds: List<Build> =
            objectMapper.readValue(
                javaClass.getResource("/pipeline/jenkins/expected/builds-for-jenkins-1.json")
                    .readText()
            )

        val allBuilds = jenkinsPipelineService.syncBuildsProgressively(pipeline, mockEmitCb)

        assertThat(allBuilds[0].pipelineId).isEqualTo(expectedBuilds[0].pipelineId)
        verify(exactly = 1) { buildRepository.save(allBuilds) }
        val progress = SyncProgress(pipelineId, pipelineName, 1, 1)
        verify(exactly = 1) { mockEmitCb.invoke(progress) }
    }

    @Test
    fun `should sync builds from Jenkins when syncBuilds() given build status is failure`() {
        val username = "fake-user"
        val credential = "fake-credential"
        val baseUrl = "http://localhost"
        val pipeline = Pipeline(
            id = "fake pipeline",
            username = username,
            credential = credential,
            url = baseUrl
        )
        val buildSummaryCollectionDTO: BuildSummaryCollectionDTO =
            objectMapper.readValue(
                javaClass.getResource("/pipeline/jenkins/raw-build-summary-3.json")
                    .readText()
            )
        val buildDetailsDTO: BuildDetailsDTO = objectMapper.readValue(
            javaClass.getResource("/pipeline/jenkins/raw-build-detail-3.json")
                .readText()
        )

        every {
            jenkinsFeignClient.retrieveBuildSummariesFromJenkins(any(), any())
        } returns buildSummaryCollectionDTO

        every {
            jenkinsFeignClient.retrieveBuildDetailsFromJenkins(any(), any(), any())
        } returns buildDetailsDTO

        val expectedBuilds: List<Build> =
            objectMapper.readValue(
                javaClass.getResource("/pipeline/jenkins/expected/builds-for-jenkins-3.json")
                    .readText()
            )
        val allBuilds = jenkinsPipelineService.syncBuildsProgressively(pipeline, mockEmitCb)
        assertThat(allBuilds[0].pipelineId).isEqualTo(expectedBuilds[0].pipelineId)
        verify(exactly = 1) { buildRepository.save(allBuilds) }
    }

    @Test
    fun `should sync builds from Jenkins when syncBuilds() given build status is null`() {
        val username = "fake-user"
        val credential = "fake-credential"
        val baseUrl = "http://localhost"
        val pipeline = Pipeline(
            id = "fake pipeline",
            username = username,
            credential = credential,
            url = baseUrl
        )
        val buildSummaryCollectionDTO: BuildSummaryCollectionDTO =
            objectMapper.readValue(
                javaClass.getResource("/pipeline/jenkins/raw-build-summary-4.json")
                    .readText()
            )
        val buildDetailsDTO: BuildDetailsDTO = objectMapper.readValue(
            javaClass.getResource("/pipeline/jenkins/raw-build-detail-4.json")
                .readText()
        )

        every {
            jenkinsFeignClient.retrieveBuildSummariesFromJenkins(any(), any())
        } returns buildSummaryCollectionDTO

        every {
            jenkinsFeignClient.retrieveBuildDetailsFromJenkins(any(), any(), any())
        } returns buildDetailsDTO

        val expectedBuilds: List<Build> =
            objectMapper.readValue(
                javaClass.getResource("/pipeline/jenkins/expected/builds-for-jenkins-4.json")
                    .readText()
            )

        val allBuilds = jenkinsPipelineService.syncBuildsProgressively(pipeline, mockEmitCb)
        assertThat(allBuilds[0].pipelineId).isEqualTo(expectedBuilds[0].pipelineId)
        verify(exactly = 1) { buildRepository.save(allBuilds) }
    }

    @Test
    fun `should sync builds from Jenkins when syncBuilds() given build status is success`() {
        val username = "fake-user"
        val credential = "fake-credential"
        val baseUrl = "http://localhost"
        val pipeline = Pipeline(
            id = "fake pipeline",
            username = username,
            credential = credential,
            url = baseUrl
        )
        val buildSummaryCollectionDTO: BuildSummaryCollectionDTO =
            objectMapper.readValue(
                javaClass.getResource("/pipeline/jenkins/raw-build-summary-5.json")
                    .readText()
            )
        val buildDetailsDTO: BuildDetailsDTO = objectMapper.readValue(
            javaClass.getResource("/pipeline/jenkins/raw-build-detail-5.json")
                .readText()
        )

        every {
            jenkinsFeignClient.retrieveBuildSummariesFromJenkins(any(), any())
        } returns buildSummaryCollectionDTO

        every {
            jenkinsFeignClient.retrieveBuildDetailsFromJenkins(any(), any(), any())
        } returns buildDetailsDTO

        val expectedBuilds: List<Build> =
            objectMapper.readValue(
                javaClass.getResource("/pipeline/jenkins/expected/builds-for-jenkins-5.json")
                    .readText()
            )
        val allBuilds = jenkinsPipelineService.syncBuildsProgressively(pipeline, mockEmitCb)
        assertThat(allBuilds[0].pipelineId).isEqualTo(expectedBuilds[0].pipelineId)
        verify(exactly = 1) { buildRepository.save(allBuilds) }
    }

    @Test
    fun `should sync builds and update progress via the emit callback at the same time`() {
        val username = "fake-user"
        val credential = "fake-credential"
        val baseUrl = "http://localhost"
        val pipeline = Pipeline(
            id = "fake pipeline",
            username = username,
            credential = credential,
            url = baseUrl,
            name = "pipeline name"
        )
        val buildSummaryCollectionDTO: BuildSummaryCollectionDTO =
            objectMapper.readValue(
                javaClass.getResource("/pipeline/jenkins/raw-build-summary-1.json")
                    .readText()
            )
        val buildDetailsDTO: BuildDetailsDTO = objectMapper.readValue(
            javaClass.getResource("/pipeline/jenkins/raw-build-detail-1.json")
                .readText()
        )

        every {
            jenkinsFeignClient.retrieveBuildSummariesFromJenkins(any(), any())
        } returns buildSummaryCollectionDTO

        every {
            jenkinsFeignClient.retrieveBuildDetailsFromJenkins(any(), any(), any())
        } returns buildDetailsDTO

        val expectedBuilds: List<Build> =
            objectMapper.readValue(
                javaClass.getResource("/pipeline/jenkins/expected/builds-for-jenkins-1.json")
                    .readText()
            )

        val allBuilds = jenkinsPipelineService.syncBuildsProgressively(pipeline, mockEmitCb)

        assertThat(allBuilds[0].pipelineId).isEqualTo(expectedBuilds[0].pipelineId)
        verify(exactly = 1) { buildRepository.save(allBuilds) }
        val progress = SyncProgress("fake pipeline", "pipeline name", 1, 1)
        verify(exactly = 1) { mockEmitCb.invoke(progress) }
    }

    @Test
    fun `should return builds with previous status is building null or not exits in DB from Jenkins when syncBuilds() given pipeline ID`() {
        val username = "fake-user"
        val credential = "fake-credential"
        val baseUrl = "http://localhost"
        val pipeline = Pipeline(
            id = "fake pipeline",
            username = username,
            credential = credential,
            url = baseUrl
        )
        every { buildRepository.getByBuildNumber(pipelineId, 1) } returns Build(
            pipelineId = pipelineId,
            number = 1,
            result = Status.IN_PROGRESS
        )
        val buildSummaryCollectionDTO: BuildSummaryCollectionDTO =
            objectMapper.readValue(
                javaClass.getResource("/pipeline/jenkins/raw-build-summary-2.json")
                    .readText()
            )
        val buildDetailsDTO: BuildDetailsDTO = objectMapper.readValue(
            javaClass.getResource("/pipeline/jenkins/raw-build-detail-2.json")
                .readText()
        )

        every {
            jenkinsFeignClient.retrieveBuildSummariesFromJenkins(any(), any())
        } returns buildSummaryCollectionDTO

        every {
            jenkinsFeignClient.retrieveBuildDetailsFromJenkins(any(), any(), any())
        } returns buildDetailsDTO

        jenkinsPipelineService.syncBuildsProgressively(pipeline, mockEmitCb)
        verify(exactly = 2) {
            jenkinsFeignClient.retrieveBuildDetailsFromJenkins(any(), any(), any())
        }
    }

    private fun buildFeignResponse(statusCode: Int) =
        Response.builder().status(statusCode).request(dummyFeignRequest).build()
}
