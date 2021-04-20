package fourkeymetrics.project.service.impl

import fourkeymetrics.MockitoHelper
import fourkeymetrics.common.model.Build
import fourkeymetrics.common.model.Commit
import fourkeymetrics.common.model.Stage
import fourkeymetrics.common.model.Status
import fourkeymetrics.exception.ApplicationException
import fourkeymetrics.project.controller.applicationservice.SyncProgress
import fourkeymetrics.project.model.Pipeline
import fourkeymetrics.project.model.PipelineType
import fourkeymetrics.project.repository.BuildRepository
import fourkeymetrics.project.repository.PipelineRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.*
import org.mockito.kotlin.mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers
import org.springframework.test.web.client.response.MockRestResponseCreators
import org.springframework.web.client.RestTemplate


@ExtendWith(SpringExtension::class)
@Import(BambooPipelineService::class, RestTemplate::class)
@RestClientTest
internal class BambooPipelineServiceTest {
    @Autowired
    private lateinit var restTemplate: RestTemplate

    @Autowired
    private lateinit var bambooPipelineService: BambooPipelineService

    @MockBean
    private lateinit var buildRepository: BuildRepository

    @MockBean
    private lateinit var pipelineRepository: PipelineRepository

    private val pipelineId = "1"
    private val credential = "fake-credential"
    private val baseUrl = "http://localhost:80"
    private val planKey = "fake-plan-key"
    private val getBuildSummariesUrl = "$baseUrl/rest/api/latest/result/${planKey}.json"
    private val getBuildDetailsUrl =
        "$baseUrl/rest/api/latest/result/${planKey}-1.json?expand=changes.change,stages.stage.results"
    private val getBuild2DetailsUrl =
        "$baseUrl/rest/api/latest/result/${planKey}-2.json?expand=changes.change,stages.stage.results"
    private lateinit var mockServer: MockRestServiceServer
    private val userInputURL = "http://localhost:80/browse/FKM-FKMS"

    @BeforeEach
    internal fun setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate)
    }

    @Test
    internal fun `should throw exception when verify pipeline given response is 500`() {
        mockServer.expect(MockRestRequestMatchers.requestTo("${baseUrl}/rest/api/latest/project/"))
            .andRespond(
                MockRestResponseCreators.withServerError()
            )

        Assertions.assertThrows(ApplicationException::class.java) {
            bambooPipelineService.verifyPipelineConfiguration(Pipeline(credential = credential, url = userInputURL))
        }
    }

    @Test
    internal fun `should throw exception when verify pipeline given response is 400`() {

        mockServer.expect(MockRestRequestMatchers.requestTo("${baseUrl}/rest/api/latest/project/"))
            .andRespond(
                MockRestResponseCreators.withBadRequest()
            )

        Assertions.assertThrows(ApplicationException::class.java) {
            bambooPipelineService.verifyPipelineConfiguration(Pipeline(credential = credential, url = userInputURL))
        }
    }

    @Test
    internal fun `should throw exception when sync pipeline given response is 500`() {
        `when`(pipelineRepository.findById(pipelineId))
            .thenReturn(
                Pipeline(
                    pipelineId,
                    credential = credential,
                    url = "$baseUrl/browse/$planKey",
                    type = PipelineType.BAMBOO
                )
            )

        mockServer.expect(MockRestRequestMatchers.requestTo("${baseUrl}/rest/api/latest/result/${planKey}.json"))
            .andRespond(
                MockRestResponseCreators.withServerError()
            )

        Assertions.assertThrows(ApplicationException::class.java) {
            bambooPipelineService.syncBuilds(pipelineId)
        }
    }

    @Test
    internal fun `should throw exception when sync pipeline given response is 400`() {
        `when`(pipelineRepository.findById(pipelineId))
            .thenReturn(
                Pipeline(
                    pipelineId,
                    credential = credential,
                    url = "$baseUrl/browse/$planKey",
                    type = PipelineType.BAMBOO
                )
            )

        mockServer.expect(MockRestRequestMatchers.requestTo("${baseUrl}/rest/api/latest/result/${planKey}.json"))
            .andRespond(
                MockRestResponseCreators.withBadRequest()
            )

        Assertions.assertThrows(ApplicationException::class.java) {
            bambooPipelineService.syncBuilds(pipelineId)
        }
    }

    @Test
    internal fun `should sync builds given build has no stages`() {
        val mockServer = MockRestServiceServer.createServer(restTemplate)

        `when`(pipelineRepository.findById(pipelineId))
            .thenReturn(
                Pipeline(
                    pipelineId,
                    credential = credential,
                    url = "$baseUrl/browse/$planKey",
                    type = PipelineType.BAMBOO
                )
            )

        mockServer.expect(MockRestRequestMatchers.requestTo(getBuildSummariesUrl))
            .andExpect { MockRestRequestMatchers.header("Authorization", credential) }
            .andRespond(
                MockRestResponseCreators.withSuccess(
                    this.javaClass.getResource("/pipeline/bamboo/raw-build-summary-2.json").readText(),
                    MediaType.APPLICATION_JSON
                )
            )

        mockServer.expect(MockRestRequestMatchers.requestTo(getBuildDetailsUrl))
            .andExpect { MockRestRequestMatchers.header("Authorization", credential) }
            .andRespond(
                MockRestResponseCreators.withSuccess(
                    this.javaClass.getResource("/pipeline/bamboo/raw-build-details-2.json").readText(),
                    MediaType.APPLICATION_JSON
                )
            )

        bambooPipelineService.syncBuilds(pipelineId)

        val build = Build(
            pipelineId = pipelineId, number = 1, result = Status.SUCCESS, duration = 0, timestamp = 1593398522798,
            url = "$baseUrl/rest/api/latest/result/$planKey-1",
            stages = emptyList(),
            changeSets = emptyList()
        )

        verify(buildRepository, times(1)).save(build)
    }

    @Test
    internal fun `should sync builds given stage has no jobs`() {
        `when`(pipelineRepository.findById(pipelineId))
            .thenReturn(
                Pipeline(
                    pipelineId,
                    credential = credential,
                    url = "$baseUrl/browse/$planKey",
                    type = PipelineType.BAMBOO
                )
            )

        mockServer.expect(MockRestRequestMatchers.requestTo(getBuildSummariesUrl))
            .andExpect { MockRestRequestMatchers.header("Authorization", credential) }
            .andRespond(
                MockRestResponseCreators.withSuccess(
                    this.javaClass.getResource("/pipeline/bamboo/raw-build-summary-6.json").readText(),
                    MediaType.APPLICATION_JSON
                )
            )

        mockServer.expect(MockRestRequestMatchers.requestTo(getBuildDetailsUrl))
            .andExpect { MockRestRequestMatchers.header("Authorization", credential) }
            .andRespond(
                MockRestResponseCreators.withSuccess(
                    this.javaClass.getResource("/pipeline/bamboo/raw-build-details-6.json").readText(),
                    MediaType.APPLICATION_JSON
                )
            )

        bambooPipelineService.syncBuilds(pipelineId)

        val build =
            Build(
                pipelineId = pipelineId, number = 1, result = Status.SUCCESS, duration = 0, timestamp = 1615882987191,
                url = "$baseUrl/rest/api/latest/result/$planKey-1",
                stages = emptyList(),
                changeSets = emptyList()
            )

        verify(buildRepository, times(1)).save(build)
    }

    @Test
    internal fun `should sync builds given both build and stage status are success`() {
        `when`(pipelineRepository.findById(pipelineId))
            .thenReturn(
                Pipeline(
                    pipelineId,
                    credential = credential,
                    url = "$baseUrl/browse/$planKey",
                    type = PipelineType.BAMBOO
                )
            )

        mockServer.expect(MockRestRequestMatchers.requestTo(getBuildSummariesUrl))
            .andExpect { MockRestRequestMatchers.header("Authorization", credential) }
            .andRespond(
                MockRestResponseCreators.withSuccess(
                    this.javaClass.getResource("/pipeline/bamboo/raw-build-summary-1.json").readText(),
                    MediaType.APPLICATION_JSON
                )
            )

        mockServer.expect(MockRestRequestMatchers.requestTo(getBuildDetailsUrl))
            .andExpect { MockRestRequestMatchers.header("Authorization", credential) }
            .andRespond(
                MockRestResponseCreators.withSuccess(
                    this.javaClass.getResource("/pipeline/bamboo/raw-build-details-1.json").readText(),
                    MediaType.APPLICATION_JSON
                )
            )

        bambooPipelineService.syncBuilds(pipelineId)

        val build =
            Build(
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
                        msg = "Create dc.txt",
                        timestamp = 1593398491000
                    )
                )
            )

        verify(buildRepository, times(1)).save(build)
    }

    @Test
    internal fun `should sync builds and update progress via the emit callback at the same time`() {
        `when`(pipelineRepository.findById(pipelineId))
            .thenReturn(
                Pipeline(
                    pipelineId,
                    credential = credential,
                    url = "$baseUrl/browse/$planKey",
                    type = PipelineType.BAMBOO
                )
            )
        mockServer.expect(MockRestRequestMatchers.requestTo(getBuildSummariesUrl))
            .andExpect { MockRestRequestMatchers.header("Authorization", credential) }
            .andRespond(
                MockRestResponseCreators.withSuccess(
                    this.javaClass.getResource("/pipeline/bamboo/raw-build-summary-1.json").readText(),
                    MediaType.APPLICATION_JSON
                )
            )
        mockServer.expect(MockRestRequestMatchers.requestTo(getBuildDetailsUrl))
            .andExpect { MockRestRequestMatchers.header("Authorization", credential) }
            .andRespond(
                MockRestResponseCreators.withSuccess(
                    this.javaClass.getResource("/pipeline/bamboo/raw-build-details-1.json").readText(),
                    MediaType.APPLICATION_JSON
                )
            )
        val mockEmitCb = mock<(SyncProgress) -> Unit>()

        bambooPipelineService.syncBuildsProgressively(pipelineId, mockEmitCb)

        val build =
            Build(
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
                        msg = "Create dc.txt",
                        timestamp = 1593398491000
                    )
                )
            )
        val progress = SyncProgress(pipelineId, 1, 1)

        verify(buildRepository, times(1)).save(build)
        verify(mockEmitCb).invoke(progress)
    }

    @Test
    internal fun `should sync builds given both build and stage status are failed`() {
        `when`(pipelineRepository.findById(pipelineId))
            .thenReturn(
                Pipeline(
                    pipelineId,
                    credential = credential,
                    url = "$baseUrl/browse/$planKey",
                    type = PipelineType.BAMBOO
                )
            )

        mockServer.expect(MockRestRequestMatchers.requestTo(getBuildSummariesUrl))
            .andExpect { MockRestRequestMatchers.header("Authorization", credential) }
            .andRespond(
                MockRestResponseCreators.withSuccess(
                    this.javaClass.getResource("/pipeline/bamboo/raw-build-summary-3.json").readText(),
                    MediaType.APPLICATION_JSON
                )
            )

        mockServer.expect(MockRestRequestMatchers.requestTo(getBuildDetailsUrl))
            .andExpect { MockRestRequestMatchers.header("Authorization", credential) }
            .andRespond(
                MockRestResponseCreators.withSuccess(
                    this.javaClass.getResource("/pipeline/bamboo/raw-build-details-3.json").readText(),
                    MediaType.APPLICATION_JSON
                )
            )

        bambooPipelineService.syncBuilds(pipelineId)

        val build =
            Build(
                pipelineId = pipelineId, number = 1, result = Status.FAILED, duration = 1133, timestamp = 1593398521665,
                url = "$baseUrl/rest/api/latest/result/$planKey-1",
                stages = listOf(Stage("Stage 1", Status.FAILED, 1593398522566, 38, 0, 1593398522604)),
                changeSets = listOf()
            )

        verify(buildRepository, times(1)).save(build)
    }

    @Test
    internal fun `should set build state to in progress when sync build given build contains stages of which status is unknown`() {
        `when`(pipelineRepository.findById(pipelineId))
            .thenReturn(
                Pipeline(
                    pipelineId,
                    credential = credential,
                    url = "$baseUrl/browse/$planKey",
                    type = PipelineType.BAMBOO
                )
            )

        mockServer.expect(MockRestRequestMatchers.requestTo(getBuildSummariesUrl))
            .andExpect { MockRestRequestMatchers.header("Authorization", credential) }
            .andRespond(
                MockRestResponseCreators.withSuccess(
                    this.javaClass.getResource("/pipeline/bamboo/raw-build-summary-4.json").readText(),
                    MediaType.APPLICATION_JSON
                )
            )

        mockServer.expect(MockRestRequestMatchers.requestTo(getBuildDetailsUrl))
            .andExpect { MockRestRequestMatchers.header("Authorization", credential) }
            .andRespond(
                MockRestResponseCreators.withSuccess(
                    this.javaClass.getResource("/pipeline/bamboo/raw-build-details-4.json").readText(),
                    MediaType.APPLICATION_JSON
                )
            )

        bambooPipelineService.syncBuilds(pipelineId)

        val build =
            Build(
                pipelineId = pipelineId,
                number = 1,
                result = Status.IN_PROGRESS,
                duration = 4020,
                timestamp = 1594089286351,
                url = "$baseUrl/rest/api/latest/result/$planKey-1",
                stages = listOf(Stage("Stage 1", Status.SUCCESS, 1594089288199, 880, 0, 1594089289079)),
                changeSets = emptyList()
            )

        verify(buildRepository, times(1)).save(build)
    }

    @Test
    internal fun `should sync builds given both build and stage status are non-supported status`() {
        `when`(pipelineRepository.findById(pipelineId))
            .thenReturn(
                Pipeline(
                    pipelineId,
                    credential = credential,
                    url = "$baseUrl/browse/$planKey",
                    type = PipelineType.BAMBOO
                )
            )

        mockServer.expect(MockRestRequestMatchers.requestTo(getBuildSummariesUrl))
            .andExpect { MockRestRequestMatchers.header("Authorization", credential) }
            .andRespond(
                MockRestResponseCreators.withSuccess(
                    this.javaClass.getResource("/pipeline/bamboo/raw-build-summary-5.json").readText(),
                    MediaType.APPLICATION_JSON
                )
            )

        mockServer.expect(MockRestRequestMatchers.requestTo(getBuildDetailsUrl))
            .andExpect { MockRestRequestMatchers.header("Authorization", credential) }
            .andRespond(
                MockRestResponseCreators.withSuccess(
                    this.javaClass.getResource("/pipeline/bamboo/raw-build-details-5.json").readText(),
                    MediaType.APPLICATION_JSON
                )
            )

        bambooPipelineService.syncBuilds(pipelineId)

        val build =
            Build(
                pipelineId = pipelineId, number = 1, result = Status.OTHER, duration = 1133, timestamp = 1593398521665,
                url = "$baseUrl/rest/api/latest/result/$planKey-1",
                stages = listOf(Stage("Stage 1", Status.OTHER, 1593398522566, 38, 0, 1593398522604)),
                changeSets = listOf()
            )

        verify(buildRepository, times(1)).save(build)
    }

    @Test
    internal fun `should sync builds given build state is in progress`() {
        `when`(pipelineRepository.findById(pipelineId))
            .thenReturn(
                Pipeline(
                    pipelineId,
                    credential = credential,
                    url = "$baseUrl/browse/$planKey",
                    type = PipelineType.BAMBOO
                )
            )

        `when`(buildRepository.findByBuildNumber(pipelineId, 1)).thenReturn(
            Build(pipelineId = pipelineId, number = 1, result = Status.IN_PROGRESS)
        )

        mockServer.expect(MockRestRequestMatchers.requestTo(getBuildSummariesUrl))
            .andExpect { MockRestRequestMatchers.header("Authorization", credential) }
            .andRespond(
                MockRestResponseCreators.withSuccess(
                    this.javaClass.getResource("/pipeline/bamboo/raw-build-summary-7.json").readText(),
                    MediaType.APPLICATION_JSON
                )
            )

        mockServer.expect(MockRestRequestMatchers.requestTo(getBuildDetailsUrl))
            .andExpect { MockRestRequestMatchers.header("Authorization", credential) }
            .andRespond(
                MockRestResponseCreators.withSuccess(
                    this.javaClass.getResource("/pipeline/bamboo/raw-build-details-1.json").readText(),
                    MediaType.APPLICATION_JSON
                )
            )

        bambooPipelineService.syncBuilds(pipelineId)

        val build =
            Build(
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
                        msg = "Create dc.txt",
                        timestamp = 1593398491000
                    )
                )
            )

        verify(buildRepository, times(1)).save(build)
    }

    @Test
    internal fun `should sync builds given build state is not exists in DB`() {
        `when`(pipelineRepository.findById(pipelineId))
            .thenReturn(
                Pipeline(
                    pipelineId,
                    credential = credential,
                    url = "$baseUrl/browse/$planKey",
                    type = PipelineType.BAMBOO
                )
            )

        `when`(buildRepository.findByBuildNumber(pipelineId, 1)).thenReturn(null)

        mockServer.expect(MockRestRequestMatchers.requestTo(getBuildSummariesUrl))
            .andExpect { MockRestRequestMatchers.header("Authorization", credential) }
            .andRespond(
                MockRestResponseCreators.withSuccess(
                    this.javaClass.getResource("/pipeline/bamboo/raw-build-summary-7.json").readText(),
                    MediaType.APPLICATION_JSON
                )
            )

        mockServer.expect(MockRestRequestMatchers.requestTo(getBuildDetailsUrl))
            .andExpect { MockRestRequestMatchers.header("Authorization", credential) }
            .andRespond(
                MockRestResponseCreators.withSuccess(
                    this.javaClass.getResource("/pipeline/bamboo/raw-build-details-1.json").readText(),
                    MediaType.APPLICATION_JSON
                )
            )

        bambooPipelineService.syncBuilds(pipelineId)

        val build =
            Build(
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
                        msg = "Create dc.txt",
                        timestamp = 1593398491000
                    )
                )
            )

        verify(buildRepository, times(1)).save(build)
    }

    @Test
    internal fun `should be able to convert not started build which has no start time and complete time`() {
        `when`(pipelineRepository.findById(pipelineId))
            .thenReturn(
                Pipeline(
                    pipelineId,
                    credential = credential,
                    url = "$baseUrl/browse/$planKey",
                    type = PipelineType.BAMBOO
                )
            )

        mockServer.expect(MockRestRequestMatchers.requestTo(getBuildSummariesUrl))
            .andExpect { MockRestRequestMatchers.header("Authorization", credential) }
            .andRespond(
                MockRestResponseCreators.withSuccess(
                    this.javaClass.getResource("/pipeline/bamboo/raw-build-summary-8.json").readText(),
                    MediaType.APPLICATION_JSON
                )
            )

        mockServer.expect(MockRestRequestMatchers.requestTo(getBuildDetailsUrl))
            .andExpect { MockRestRequestMatchers.header("Authorization", credential) }
            .andRespond(
                MockRestResponseCreators.withSuccess(
                    this.javaClass.getResource("/pipeline/bamboo/raw-build-details-8.json").readText(),
                    MediaType.APPLICATION_JSON
                )
            )

        bambooPipelineService.syncBuilds(pipelineId)

        verify(buildRepository, never()).save(MockitoHelper.anyObject<Build>())
    }
}