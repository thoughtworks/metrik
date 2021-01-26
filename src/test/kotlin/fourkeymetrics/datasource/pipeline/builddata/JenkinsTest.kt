package fourkeymetrics.datasource.pipeline.builddata

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import fourkeymetrics.datasource.pipeline.configuration.repository.DashboardRepository
import fourkeymetrics.datasource.pipeline.configuration.model.PipelineConfiguration
import fourkeymetrics.metrics.model.Build
import fourkeymetrics.metrics.model.Stage
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
@Import(
    Jenkins::class, DashboardRepository::class, BuildRepository::class, ObjectMapper::class,
    RestTemplate::class
)
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

        `when`(
            dashboardRepository.getPipelineConfiguration(
                dashboardId,
                pipelineId
            )
        ).thenReturn(PipelineConfiguration())

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
        val credential = "fake-credential"
        val baseUrl = "http://localhost"
        val getBuildSummariesUrl = "$baseUrl/api/json?tree=allBuilds%5Bbuilding," +
                "number,result,timestamp,duration,url,changeSets%5Bitems%5BcommitId,timestamp,msg,date%5D%5D%5D"
        val getBuildDetailUrl = "$baseUrl/82/wfapi/describe"
        val mockServer = MockRestServiceServer.createServer(restTemplate)

        `when`(dashboardRepository.getPipelineConfiguration(dashboardId, pipelineId)).thenReturn(
            PipelineConfiguration(
                username = username,
                credential = credential,
                url = baseUrl
            )
        )
        mockServer.expect(requestTo(getBuildSummariesUrl))
            .andRespond(
                withSuccess(
                    this.javaClass.getResource("/pipeline/raw-builds-1.json").readText(),
                    MediaType.APPLICATION_JSON
                )
            )
        mockServer.expect(requestTo(getBuildDetailUrl))
            .andRespond(
                withSuccess(
                    this.javaClass.getResource("/pipeline/raw-build-detail-1.json").readText(),
                    MediaType.APPLICATION_JSON
                )
            )

        val expectedBuilds: List<Build> =
            objectMapper.readValue(this.javaClass.getResource("/pipeline/builds-for-jenkins-1.json").readText())
        val allBuilds = jenkins.fetchAllBuilds(dashboardId, pipelineId)
        assertThat(allBuilds[0].pipelineId).isEqualTo(expectedBuilds[0].pipelineId)
        verify(buildRepository, times(1)).clear()
        verify(buildRepository, times(1)).save(allBuilds)
    }

    @Test
    internal fun `should return true given stage exists in builds filtered by time range`() {
        val pipelineId = "fake pipeline"
        val targetStage = "deploy to dev"
        val startTimestamp = 100000L
        val endTimestamp = 200000L

        val lastBuild = Build(
            stages = listOf(
                Stage(name = targetStage, startTimeMillis = 100000L),
                Stage(name = "another stage", startTimeMillis = 100000L)
            )
        )

        `when`(buildRepository.getAllBuilds(pipelineId)).thenReturn(listOf(lastBuild))

        assertThat(jenkins.hasStageInTimeRange(pipelineId, targetStage, startTimestamp, endTimestamp)).isTrue
    }

    @Test
    internal fun `should return false given stage not exists in builds filtered by time range`() {
        val pipelineId = "fake pipeline"
        val targetStage = "deploy to dev"
        val startTimestamp = 100000L
        val endTimestamp = 200000L

        val lastBuild = Build(
            stages = listOf(
                Stage(name = "clone"), Stage(name = "build"),
                Stage(
                    name = targetStage,
                    startTimeMillis = 150000L,
                    durationMillis = 30000L,
                    pauseDurationMillis = 50000L
                )
            )
        )

        `when`(buildRepository.getAllBuilds(pipelineId)).thenReturn(listOf(lastBuild))

        assertThat(jenkins.hasStageInTimeRange(pipelineId, targetStage, startTimestamp, endTimestamp)).isFalse
    }
}