package fourkeymetrics.pipeline

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import fourkeymetrics.model.Build
import fourkeymetrics.model.PipelineConfiguration
import fourkeymetrics.model.Stage
import fourkeymetrics.repository.BuildRepository
import fourkeymetrics.repository.DashboardRepository
import org.assertj.core.api.Assertions.assertThat
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
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import org.springframework.web.client.RestTemplate


@ExtendWith(SpringExtension::class)
@Import(Jenkins::class, DashboardRepository::class, BuildRepository::class, ObjectMapper::class,
    RestTemplate::class)
@RestClientTest
internal class JenkinsTest {
    @Autowired
    private lateinit var jenkins: Jenkins

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var restTemplate: RestTemplate

    @MockBean
    private lateinit var dashboardRepository: DashboardRepository

    @MockBean
    private lateinit var buildRepository: BuildRepository

    @Test
    internal fun `should return true given pipeline exits in database`() {
        val dashboardId = "dashboard ID"
        val pipelineId = "fake pipeline"

        `when`(dashboardRepository.getPipelineConfiguration(dashboardId, pipelineId)).thenReturn(PipelineConfiguration())

        assertThat(jenkins.hasPipeline(dashboardId, pipelineId)).isTrue
    }

    @Test
    internal fun `should return false given pipeline not exits in database`() {
        val dashboardId = "dashboard ID"
        val pipelineId = "fake pipeline"

        `when`(dashboardRepository.getPipelineConfiguration(dashboardId, pipelineId)).thenReturn(null)

        assertThat(jenkins.hasPipeline(dashboardId, pipelineId)).isFalse
    }

    @Test
    internal fun `should return all builds from Jenkins given pipeline ID`() {
        val dashboardId = "dashboard ID"
        val pipelineId = "fake pipeline"
        val username = "fake-user"
        val token = "fake-token"
        val baseUrl = "localhost"
        val getBuildSummariesUrl = "http://$baseUrl/api/json?tree=allBuilds%5Bbuilding," +
            "number,result,timestamp,duration,url,changeSets%5Bitems%5BcommitId,timestamp,msg,date%5D%5D%5D"
        val getBuildDetailUrl = "http://$baseUrl/82/wfapi/describe"
        val mockServer = MockRestServiceServer.createServer(restTemplate)

        `when`(dashboardRepository.getPipelineConfiguration(dashboardId, pipelineId)).thenReturn(PipelineConfiguration(username = username, token = token, url = baseUrl))
        mockServer.expect(requestTo(getBuildSummariesUrl))
            .andRespond(withSuccess(this.javaClass.getResource("/pipeline/raw-builds-1.json").readText(), MediaType.APPLICATION_JSON))
        mockServer.expect(requestTo(getBuildDetailUrl))
            .andRespond(withSuccess(this.javaClass.getResource("/pipeline/raw-build-detail-1.json").readText(), MediaType.APPLICATION_JSON))

        val expectedBuilds: List<Build> = objectMapper.readValue(this.javaClass.getResource("/pipeline/builds-1.json").readText())
        val allBuilds = jenkins.fetchAllBuilds(dashboardId, pipelineId)
        assertThat(allBuilds.get(0).pipelineId).isEqualTo(expectedBuilds.get(0).pipelineId)
        verify(buildRepository, only()).save(allBuilds)
    }

    @Test
    internal fun `should return true given stage exists in builds filtered by time range`() {
        val pipelineId = "fake pipeline"
        val targetStage = "deploy to dev"
        val startTimestamp = 100000L
        val endTimestamp = 200000L

        val lastBuild = Build(stages = listOf(Stage(name = targetStage), Stage(name = "build")))

        `when`(buildRepository.getBuildsByTimePeriod(pipelineId, startTimestamp, endTimestamp)).thenReturn(listOf(lastBuild))

        assertThat(jenkins.hasStageInTimeRange(pipelineId, targetStage, startTimestamp, endTimestamp)).isTrue
    }

    @Test
    internal fun `should return false given stage not exists in builds filtered by time range`() {
        val pipelineId = "fake pipeline"
        val targetStage = "deploy to dev"
        val startTimestamp = 100000L
        val endTimestamp = 200000L

        val lastBuild = Build(stages = listOf(Stage(name = "clone"), Stage(name = "build")))

        `when`(buildRepository.getBuildsByTimePeriod(pipelineId, startTimestamp, endTimestamp)).thenReturn(listOf(lastBuild))

        assertThat(jenkins.hasStageInTimeRange(pipelineId, targetStage, startTimestamp, endTimestamp)).isFalse
    }
}