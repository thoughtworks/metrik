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
import metrik.infrastructure.utlils.RequestUtil
import metrik.project.domain.model.*
import metrik.project.domain.model.Stage
import metrik.project.domain.repository.BuildRepository
import metrik.project.infrastructure.bamboo.feign.BambooFeignClient
import metrik.project.rest.vo.response.SyncProgress
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
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
@Import(BambooPipelineService::class, RestTemplate::class)
internal class BambooPipelineServiceTest {
    @Autowired
    private lateinit var bambooPipelineService: BambooPipelineService

    @MockkBean(relaxed = true)
    private lateinit var buildRepository: BuildRepository

    @MockkBean(relaxed = true)
    private lateinit var bambooFeignClient: BambooFeignClient

    private val mockEmitCb = mockk<(SyncProgress) -> Unit>(relaxed = true)

    private lateinit var objectMapper: ObjectMapper

    private val pipelineId = "1"
    private val credential = "fake-credential"
    private val baseUrl = "http://localhost:80"
    private val planKey = "fake-plan-key"
    private val userInputURL = "http://localhost:80/browse/FKM-FKMS"
    private val pipelineName = "TEST"

    private val dummyFeignRequest = Request.create(Request.HttpMethod.POST, "url", mapOf(), null, null, null)


    @BeforeEach
    fun setUp() {
        objectMapper = ObjectMapper().registerModule(KotlinModule())
        objectMapper.registerModule(JavaTimeModule())
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        val executions = listOf(
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
        every { buildRepository.getAllBuilds(pipelineId) } returns executions
        every { buildRepository.getBambooJenkinsBuildNumbersNeedSync(pipelineId, 1) } returns listOf(1)
    }

    @Test
    fun `should throw exception when verify pipeline given response is 500`() {
        every {
            bambooFeignClient.verify(any(), any())
        } throws FeignException.errorStatus(
            "GET",
            buildFeignResponse(500)
        )
        assertThrows(ApplicationException::class.java) {
            bambooPipelineService.verifyPipelineConfiguration(Pipeline(credential = credential, url = userInputURL))
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
        assertThrows(ApplicationException::class.java) {
            bambooPipelineService.verifyPipelineConfiguration(Pipeline(credential = credential, url = userInputURL))
        }
    }

    @Test
    fun `should return sorted stage name lists when getStagesSortedByName() called`() {
        val result = bambooPipelineService.getStagesSortedByName(pipelineId)

        assertEquals(5, result.size)
        assertEquals("amazing", result[0])
        assertEquals("build", result[1])
        assertEquals("clone", result[2])
        assertEquals("good", result[3])
        assertEquals("zzz", result[4])
    }

    @Test
    fun `should throw exception when sync pipeline given response is 500`() {
        val pipeline = Pipeline(
            id = pipelineId,
            name = pipelineName,
            credential = credential,
            url = "$baseUrl/browse/$planKey",
            type = PipelineType.BAMBOO
        )
        every {
            bambooFeignClient.getMaxBuildNumber(any(), any())
        } throws FeignException.errorStatus(
            "GET",
            buildFeignResponse(500)
        )
        assertThrows(ApplicationException::class.java) {
            bambooPipelineService.syncBuildsProgressively(pipeline, mockEmitCb)
        }
    }

    @Test
    fun `should throw exception when sync pipeline given response is 400`() {
        val pipeline = Pipeline(
            id = pipelineId,
            name = pipelineName,
            credential = credential,
            url = "$baseUrl/browse/$planKey",
            type = PipelineType.BAMBOO
        )
        every {
            bambooFeignClient.getMaxBuildNumber(any(), any())
        } throws FeignException.errorStatus(
            "GET",
            buildFeignResponse(400)
        )

        assertThrows(ApplicationException::class.java) {
            bambooPipelineService.syncBuildsProgressively(pipeline, mockEmitCb)
        }
    }

    @Test
    fun `should sync builds given build has no stages`() {
        val pipeline = Pipeline(
            id = pipelineId,
            name = pipelineName,
            credential = credential,
            url = "$baseUrl/browse/$planKey",
            type = PipelineType.BAMBOO
        )
        val buildSummaryDTO: BuildSummaryDTO = objectMapper.readValue(
            javaClass.getResource("/pipeline/bamboo/raw-build-summary-2.json")
                .readText()
        )
        val buildDetailsDTO: BuildDetailDTO = objectMapper.readValue(
            javaClass.getResource("/pipeline/bamboo/raw-build-details-2.json")
                .readText()
        )

        val urlForSummary = "${RequestUtil.getDomain(pipeline.url)}/rest/api/latest/result/$planKey.json"
        val urlForDetails =
            "${RequestUtil.getDomain(pipeline.url)}/rest/api/latest/result/$planKey-$pipelineName-1.json?" +
                    "expand=changes.change,stages.stage.results"
        every { buildRepository.getBambooJenkinsBuildNumbersNeedSync(pipelineId, 1) } returns listOf(1)
        every {
            bambooFeignClient.getMaxBuildNumber(URI(urlForSummary), credential)
        } returns buildSummaryDTO
        every {
            bambooFeignClient.getBuildDetails(URI(urlForDetails), credential)
        } returns buildDetailsDTO
        bambooPipelineService.syncBuildsProgressively(pipeline, mockEmitCb)

        val execution = Execution(
            pipelineId = pipelineId, number = 1, result = Status.SUCCESS, duration = 0, timestamp = 1593398522798,
            url = "$baseUrl/rest/api/latest/result/$planKey-1",
            stages = emptyList(),
            changeSets = emptyList()
        )

        verify(exactly = 1) { buildRepository.save(execution) }
    }

    @Test
    fun `should sync builds given stage has no jobs`() {
        val pipeline = Pipeline(
            id = pipelineId,
            name = pipelineName,
            credential = credential,
            url = "$baseUrl/browse/$planKey",
            type = PipelineType.BAMBOO
        )
        val urlForSummary = "${RequestUtil.getDomain(pipeline.url)}/rest/api/latest/result/$planKey.json"
        val urlForDetails =
            "${RequestUtil.getDomain(pipeline.url)}/rest/api/latest/result/$planKey-$pipelineName-1.json?" +
                    "expand=changes.change,stages.stage.results"

        val buildSummaryDTO: BuildSummaryDTO =
            objectMapper.readValue(javaClass.getResource("/pipeline/bamboo/raw-build-summary-6.json").readText())
        val buildDetailsDTO: BuildDetailDTO =
            objectMapper.readValue(javaClass.getResource("/pipeline/bamboo/raw-build-details-6.json").readText())

        every { buildRepository.getBambooJenkinsBuildNumbersNeedSync(pipelineId, 1) } returns listOf(1)
        every { bambooFeignClient.getMaxBuildNumber(URI(urlForSummary), credential) } returns buildSummaryDTO
        every { bambooFeignClient.getBuildDetails(URI(urlForDetails), credential) } returns buildDetailsDTO

        bambooPipelineService.syncBuildsProgressively(pipeline, mockEmitCb)

        val execution =
            Execution(
                pipelineId = pipelineId, number = 1, result = Status.SUCCESS, duration = 0, timestamp = 1615882987191,
                url = "$baseUrl/rest/api/latest/result/$planKey-1",
                stages = emptyList(),
                changeSets = emptyList()
            )

        verify(exactly = 1) { buildRepository.save(execution) }
    }

    @Test
    fun `should sync builds and emit the progress event given both build and stage status are success`() {
        val pipeline = Pipeline(
            id = pipelineId,
            name = pipelineName,
            credential = credential,
            url = "$baseUrl/browse/$planKey",
            type = PipelineType.BAMBOO
        )
        val urlForSummary = "${RequestUtil.getDomain(pipeline.url)}/rest/api/latest/result/$planKey.json"
        val urlForDetails =
            "${RequestUtil.getDomain(pipeline.url)}/rest/api/latest/result/$planKey-$pipelineName-1.json?" +
                    "expand=changes.change,stages.stage.results"


        val buildSummaryDTO: BuildSummaryDTO =
            objectMapper.readValue(javaClass.getResource("/pipeline/bamboo/raw-build-summary-1.json").readText())
        val buildDetailsDTO: BuildDetailDTO =
            objectMapper.readValue(javaClass.getResource("/pipeline/bamboo/raw-build-details-1.json").readText())

        every { buildRepository.getBambooJenkinsBuildNumbersNeedSync(pipelineId, 1) } returns listOf(1)
        every { bambooFeignClient.getMaxBuildNumber(URI(urlForSummary), credential) } returns buildSummaryDTO
        every { bambooFeignClient.getBuildDetails(URI(urlForDetails), credential) } returns buildDetailsDTO

        bambooPipelineService.syncBuildsProgressively(pipeline, mockEmitCb)

        val execution =
            Execution(
                pipelineId = pipelineId,
                number = 1,
                result = Status.SUCCESS,
                duration = 1133,
                timestamp = 1593398521665,
                url = "$baseUrl/rest/api/latest/result/$planKey-1",
                stages = listOf(Stage("Stage 1", Status.SUCCESS, 1593398522566, 38, 0, 1593398522604)),
                changeSets = listOf(
                    Commit(
                        commitId = "7cba897038ca321dac1c7e87855879194d3d6307",
                        date = "2020-06-29T02:41:31Z[UTC]",
                        timestamp = 1593398491000
                    )
                )
            )

        verify(exactly = 1) { buildRepository.save(execution) }
        val progress = SyncProgress(pipelineId, pipelineName, 1, 1)
        verify(exactly = 1) { mockEmitCb.invoke(progress) }
    }

    @Test
    fun `should sync builds given both build and stage status are failed`() {
        val pipeline = Pipeline(
            id = pipelineId,
            name = pipelineName,
            credential = credential,
            url = "$baseUrl/browse/$planKey",
            type = PipelineType.BAMBOO
        )
        val urlForSummary = "${RequestUtil.getDomain(pipeline.url)}/rest/api/latest/result/$planKey.json"
        val urlForDetails =
            "${RequestUtil.getDomain(pipeline.url)}/rest/api/latest/result/$planKey-$pipelineName-1.json?" +
                    "expand=changes.change,stages.stage.results"

        val buildSummaryDTO: BuildSummaryDTO =
            objectMapper.readValue(javaClass.getResource("/pipeline/bamboo/raw-build-summary-3.json").readText())
        val buildDetailsDTO: BuildDetailDTO =
            objectMapper.readValue(javaClass.getResource("/pipeline/bamboo/raw-build-details-3.json").readText())

        every { buildRepository.getBambooJenkinsBuildNumbersNeedSync(pipelineId, 1) } returns listOf(1)
        every { bambooFeignClient.getMaxBuildNumber(URI(urlForSummary), credential) } returns buildSummaryDTO
        every { bambooFeignClient.getBuildDetails(URI(urlForDetails), credential) } returns buildDetailsDTO

        bambooPipelineService.syncBuildsProgressively(pipeline, mockEmitCb)

        val execution =
            Execution(
                pipelineId = pipelineId, number = 1, result = Status.FAILED, duration = 1133, timestamp = 1593398521665,
                url = "$baseUrl/rest/api/latest/result/$planKey-1",
                stages = listOf(Stage("Stage 1", Status.FAILED, 1593398522566, 38, 0, 1593398522604)),
                changeSets = listOf()
            )

        verify(exactly = 1) { buildRepository.save(execution) }
    }

    @Test
    fun `should set build state to in progress when sync build given build contains stages of which status is unknown`() {
        val pipeline = Pipeline(
            id = pipelineId,
            name = pipelineName,
            credential = credential,
            url = "$baseUrl/browse/$planKey",
            type = PipelineType.BAMBOO
        )
        val urlForSummary = "${RequestUtil.getDomain(pipeline.url)}/rest/api/latest/result/$planKey.json"
        val urlForDetails =
            "${RequestUtil.getDomain(pipeline.url)}/rest/api/latest/result/$planKey-$pipelineName-1.json?" +
                    "expand=changes.change,stages.stage.results"

        val buildSummaryDTO: BuildSummaryDTO =
            objectMapper.readValue(javaClass.getResource("/pipeline/bamboo/raw-build-summary-4.json").readText())
        val buildDetailsDTO: BuildDetailDTO =
            objectMapper.readValue(javaClass.getResource("/pipeline/bamboo/raw-build-details-4.json").readText())

        every { buildRepository.getBambooJenkinsBuildNumbersNeedSync(pipelineId, 1) } returns listOf(1)
        every { bambooFeignClient.getMaxBuildNumber(URI(urlForSummary), credential) } returns buildSummaryDTO
        every { bambooFeignClient.getBuildDetails(URI(urlForDetails), credential) } returns buildDetailsDTO

        bambooPipelineService.syncBuildsProgressively(pipeline, mockEmitCb)

        val execution =
            Execution(
                pipelineId = pipelineId,
                number = 1,
                result = Status.IN_PROGRESS,
                duration = 4020,
                timestamp = 1594089286351,
                url = "$baseUrl/rest/api/latest/result/$planKey-1",
                stages = listOf(Stage("Stage 1", Status.SUCCESS, 1594089288199, 880, 0, 1594089289079)),
                changeSets = emptyList()
            )

        verify(exactly = 1) { buildRepository.save(execution) }
    }

    @Test
    fun `should sync builds given both build and stage status are non-supported status`() {
        val pipeline = Pipeline(
            id = pipelineId,
            name = pipelineName,
            credential = credential,
            url = "$baseUrl/browse/$planKey",
            type = PipelineType.BAMBOO
        )
        val urlForSummary = "${RequestUtil.getDomain(pipeline.url)}/rest/api/latest/result/$planKey.json"
        val urlForDetails =
            "${RequestUtil.getDomain(pipeline.url)}/rest/api/latest/result/$planKey-$pipelineName-1.json?" +
                    "expand=changes.change,stages.stage.results"

        val buildSummaryDTO: BuildSummaryDTO =
            objectMapper.readValue(javaClass.getResource("/pipeline/bamboo/raw-build-summary-5.json").readText())
        val buildDetailsDTO: BuildDetailDTO =
            objectMapper.readValue(javaClass.getResource("/pipeline/bamboo/raw-build-details-5.json").readText())

        every { buildRepository.getBambooJenkinsBuildNumbersNeedSync(pipelineId, 1) } returns listOf(1)
        every { bambooFeignClient.getMaxBuildNumber(URI(urlForSummary), credential) } returns buildSummaryDTO
        every { bambooFeignClient.getBuildDetails(URI(urlForDetails), credential) } returns buildDetailsDTO

        bambooPipelineService.syncBuildsProgressively(pipeline, mockEmitCb)

        val execution =
            Execution(
                pipelineId = pipelineId, number = 1, result = Status.OTHER, duration = 1133, timestamp = 1593398521665,
                url = "$baseUrl/rest/api/latest/result/$planKey-1",
                stages = listOf(Stage("Stage 1", Status.OTHER, 1593398522566, 38, 0, 1593398522604)),
                changeSets = listOf()
            )

        verify(exactly = 1) { buildRepository.save(execution) }
    }

    @Test
    fun `should sync builds given the build is marked as 'need sync' by buildRepository`() {
        val pipeline = Pipeline(
            id = pipelineId,
            name = pipelineName,
            credential = credential,
            url = "$baseUrl/browse/$planKey",
            type = PipelineType.BAMBOO
        )
        val urlForSummary = "${RequestUtil.getDomain(pipeline.url)}/rest/api/latest/result/$planKey.json"
        val urlForDetails =
            "${RequestUtil.getDomain(pipeline.url)}/rest/api/latest/result/$planKey-$pipelineName-1.json?" +
                    "expand=changes.change,stages.stage.results"

        val buildSummaryDTO: BuildSummaryDTO =
            objectMapper.readValue(javaClass.getResource("/pipeline/bamboo/raw-build-summary-7.json").readText())
        val buildDetailsDTO: BuildDetailDTO =
            objectMapper.readValue(javaClass.getResource("/pipeline/bamboo/raw-build-details-1.json").readText())

        every { buildRepository.getBambooJenkinsBuildNumbersNeedSync(pipelineId, 1) } returns listOf(1)
        every { bambooFeignClient.getMaxBuildNumber(URI(urlForSummary), credential) } returns buildSummaryDTO
        every { bambooFeignClient.getBuildDetails(URI(urlForDetails), credential) } returns buildDetailsDTO

        bambooPipelineService.syncBuildsProgressively(pipeline, mockEmitCb)

        val execution =
            Execution(
                pipelineId = pipelineId,
                number = 1,
                result = Status.SUCCESS,
                duration = 1133,
                timestamp = 1593398521665,
                url = "$baseUrl/rest/api/latest/result/$planKey-1",
                stages = listOf(Stage("Stage 1", Status.SUCCESS, 1593398522566, 38, 0, 1593398522604)),
                changeSets = listOf(
                    Commit(
                        commitId = "7cba897038ca321dac1c7e87855879194d3d6307",
                        date = "2020-06-29T02:41:31Z[UTC]",
                        timestamp = 1593398491000
                    )
                )
            )

        verify(exactly = 1) { buildRepository.save(execution) }
    }

    @Test
    fun `should be able to convert not started build which has no start time and complete time`() {
        val pipeline = Pipeline(
            id = pipelineId,
            name = pipelineName,
            credential = credential,
            url = "$baseUrl/browse/$planKey",
            type = PipelineType.BAMBOO
        )
        val urlForSummary = "${RequestUtil.getDomain(pipeline.url)}/rest/api/latest/result/$planKey.json"
        val urlForDetails =
            "${RequestUtil.getDomain(pipeline.url)}/rest/api/latest/result/$planKey-$pipelineName-1.json?" +
                    "expand=changes.change,stages.stage.results"

        val buildSummaryDTO: BuildSummaryDTO =
            objectMapper.readValue(javaClass.getResource("/pipeline/bamboo/raw-build-summary-8.json").readText())
        val buildDetailsDTO: BuildDetailDTO =
            objectMapper.readValue(javaClass.getResource("/pipeline/bamboo/raw-build-details-8.json").readText())

        every { buildRepository.getBambooJenkinsBuildNumbersNeedSync(pipelineId, 1) } returns listOf(1)
        every { bambooFeignClient.getMaxBuildNumber(URI(urlForSummary), credential) } returns buildSummaryDTO
        every { bambooFeignClient.getBuildDetails(URI(urlForDetails), credential) } returns buildDetailsDTO

        bambooPipelineService.syncBuildsProgressively(pipeline, mockEmitCb)

        verify(exactly = 0) { buildRepository.save(ofType(Execution::class)) }
    }

    private fun buildFeignResponse(statusCode: Int) =
        Response.builder().status(statusCode).request(dummyFeignRequest).build()
}
