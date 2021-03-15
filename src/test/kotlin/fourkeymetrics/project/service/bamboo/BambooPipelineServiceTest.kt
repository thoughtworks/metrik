package fourkeymetrics.project.service.bamboo

import fourkeymetrics.common.model.Build
import fourkeymetrics.common.model.Commit
import fourkeymetrics.common.model.Stage
import fourkeymetrics.common.model.Status
import fourkeymetrics.exception.ApplicationException
import fourkeymetrics.project.model.Pipeline
import fourkeymetrics.project.model.PipelineType
import fourkeymetrics.project.repository.BuildRepository
import fourkeymetrics.project.repository.PipelineRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.*
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

    @Test
    internal fun `should throw exception when verify pipeline given response is 500`() {
        val credential = "fake-credential"
        val url = "http://localhost/deploy/viewDeploymentProjectEnvironments.action?id=4751362"
        val baseUrl = "http://localhost:80"
        val mockServer = MockRestServiceServer.bindTo(restTemplate).ignoreExpectOrder(true).build()

        mockServer.expect(MockRestRequestMatchers.requestTo("${baseUrl}/rest/api/latest/project/"))
                .andRespond(
                        MockRestResponseCreators.withServerError()
                )

        Assertions.assertThrows(ApplicationException::class.java) {
            bambooPipelineService.verifyPipelineConfiguration(Pipeline(credential = credential, url = url))
        }
    }

    @Test
    internal fun `should throw exception when verify pipeline given response is 400`() {
        val credential = "fake-credential"
        val url = "http://localhost/deploy/viewDeploymentProjectEnvironments.action?id=4751362"
        val baseUrl = "http://localhost:80"
        val mockServer = MockRestServiceServer.bindTo(restTemplate).ignoreExpectOrder(true).build()

        mockServer.expect(MockRestRequestMatchers.requestTo("${baseUrl}/rest/api/latest/project/"))
                .andRespond(
                        MockRestResponseCreators.withBadRequest()
                )

        Assertions.assertThrows(ApplicationException::class.java) {
            bambooPipelineService.verifyPipelineConfiguration(Pipeline(credential = credential, url = url))
        }
    }

    @Test
    internal fun `should save all builds when sync given there is no build in DB`() {
        val pipelineId = "1"
        val credential = "fake-credential"
        val baseUrl = "http://localhost:80"
        val planKey = "fake-plan-key"
        val getBuildSummariesUrl = "$baseUrl/rest/api/latest/result/${planKey}.json"
        val getBuildDetailsUrl = "$baseUrl/rest/api/latest/result/${planKey}-1.json?expand=changes.change,stages.stage.results"
        val mockServer = MockRestServiceServer.createServer(restTemplate)

        `when`(pipelineRepository.findById(pipelineId))
                .thenReturn(Pipeline(pipelineId, credential = credential, url = "$baseUrl/browse/$planKey", type = PipelineType.BAMBOO))

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

        val builds = listOf(Build(
                pipelineId = pipelineId, number = 1, result = Status.SUCCESS, duration = 1133, timestamp = 1593398521665,
                url = "$baseUrl/rest/api/latest/result/$planKey-1",
                stages = listOf(Stage("Stage 1", Status.SUCCESS, 1593398522566, 38, 0, 1593398522604)),
                changeSets = listOf(Commit(commitId = "7cba897038ca321dac1c7e87855879194d3d6307", date = "2020-06-29T02:41:31Z[UTC]", msg = "Create dc.txt", timestamp = 1593398491000))))

        verify(buildRepository, times(1)).save(builds)
    }

    @Test
    internal fun `should sync builds given build has no stages`() {
        val pipelineId = "1"
        val credential = "fake-credential"
        val baseUrl = "http://localhost:80"
        val planKey = "fake-plan-key"
        val getBuildSummariesUrl = "$baseUrl/rest/api/latest/result/${planKey}.json"
        val getBuildDetailsUrl = "$baseUrl/rest/api/latest/result/${planKey}-1.json?expand=changes.change,stages.stage.results"
        val mockServer = MockRestServiceServer.createServer(restTemplate)

        `when`(pipelineRepository.findById(pipelineId))
                .thenReturn(Pipeline(pipelineId, credential = credential, url = "$baseUrl/browse/$planKey", type = PipelineType.BAMBOO))

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

        val builds = listOf(Build(
                pipelineId = pipelineId, number = 1, result = Status.SUCCESS, duration = 0, timestamp = 1593398522798,
                url = "$baseUrl/rest/api/latest/result/$planKey-1",
                stages = emptyList(),
                changeSets = emptyList()))

        verify(buildRepository, times(1)).save(builds)
    }

    @Test
    internal fun `should sync all builds when stage contains job that haven't been triggered`() {
        val pipelineId = "1"
        val credential = "fake-credential"
        val baseUrl = "http://localhost:80"
        val planKey = "fake-plan-key"
        val getBuildSummariesUrl = "$baseUrl/rest/api/latest/result/${planKey}.json"
        val getBuildDetailsUrl = "$baseUrl/rest/api/latest/result/${planKey}-1.json?expand=changes.change,stages.stage.results"
        val mockServer = MockRestServiceServer.createServer(restTemplate)

        `when`(pipelineRepository.findById(pipelineId))
                .thenReturn(Pipeline(pipelineId, credential = credential, url = "$baseUrl/browse/$planKey", type = PipelineType.BAMBOO))

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

        val builds = listOf(Build(
                pipelineId = pipelineId, number = 1, result = Status.SUCCESS, duration = 4020, timestamp = 1594089286351,
                url = "$baseUrl/rest/api/latest/result/$planKey-1",
                stages = listOf(Stage("Stage 1", Status.SUCCESS, 1594089288199, 880, 0, 1594089289079),
                Stage("Deploy to SIT", Status.IN_PROGRESS, null, null, 0, null),
                Stage("Deploy to Prod", Status.IN_PROGRESS, null, null, 0, null)),
                changeSets = emptyList()))

        verify(buildRepository, times(1)).save(builds)
    }
}