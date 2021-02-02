package fourkeymetrics.dashboard.service.jenkins

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import fourkeymetrics.common.model.Build
import fourkeymetrics.common.model.Stage
import fourkeymetrics.dashboard.controller.vo.PipelineStagesResponse
import fourkeymetrics.dashboard.model.Pipeline
import fourkeymetrics.dashboard.repository.BuildRepository
import fourkeymetrics.dashboard.repository.DashboardRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
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
    JenkinsPipelineService::class, DashboardRepository::class, BuildRepository::class, ObjectMapper::class,
    RestTemplate::class
)
@RestClientTest
internal class JenkinsPipelineServiceTest {
    @Autowired
    private lateinit var jenkinsPipelineService: JenkinsPipelineService

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
        ).thenReturn(Pipeline())

        assertThat(jenkinsPipelineService.hasPipeline(dashboardId, pipelineId)).isTrue
    }

    @Test
    internal fun `should return false given pipeline not exits in database`() {
        val dashboardId = "dashboard ID"
        val pipelineId = "fake pipeline"

        `when`(dashboardRepository.getPipelineConfiguration(dashboardId, pipelineId)).thenReturn(null)

        assertThat(jenkinsPipelineService.hasPipeline(dashboardId, pipelineId)).isFalse
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
            Pipeline(
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
        val allBuilds = jenkinsPipelineService.syncBuilds(dashboardId, pipelineId)
        assertThat(allBuilds[0].pipelineId).isEqualTo(expectedBuilds[0].pipelineId)
        verify(buildRepository, times(1)).save(allBuilds)
    }


    @Test
    internal fun `should return builds with previous status is building null or not exits in DB from Jenkins given pipeline ID`() {
        val dashboardId = "dashboard ID"
        val pipelineId = "fake pipeline"
        val username = "fake-user"
        val credential = "fake-credential"
        val baseUrl = "http://localhost"
        val getBuildSummariesUrl = "$baseUrl/api/json?tree=allBuilds%5Bbuilding," +
                "number,result,timestamp,duration,url,changeSets%5Bitems%5BcommitId,timestamp,msg,date%5D%5D%5D"
        val getBuild1DetailUrl = "$baseUrl/1/wfapi/describe"
        val getBuild2DetailUrl = "$baseUrl/2/wfapi/describe"
        val mockServer = MockRestServiceServer.bindTo(restTemplate).ignoreExpectOrder(true).build()

        `when`(dashboardRepository.getPipelineConfiguration(dashboardId, pipelineId)).thenReturn(
            Pipeline(
                username = username,
                credential = credential,
                url = baseUrl
            )
        )

        `when`(buildRepository.findByBuildNumber(pipelineId, 1)).thenReturn(
            Build(pipelineId = pipelineId, number = 1, result = null)
        )

        mockServer.expect(requestTo(getBuildSummariesUrl))
            .andRespond(
                withSuccess(
                    this.javaClass.getResource("/pipeline/raw-builds-2.json").readText(),
                    MediaType.APPLICATION_JSON
                )
            )
        mockServer.expect(requestTo(getBuild1DetailUrl))
        mockServer.expect(requestTo(getBuild2DetailUrl))
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

        assertThat(
            jenkinsPipelineService.hasStageInTimeRange(
                pipelineId,
                targetStage,
                startTimestamp,
                endTimestamp
            )
        ).isTrue
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

        assertThat(
            jenkinsPipelineService.hasStageInTimeRange(
                pipelineId,
                targetStage,
                startTimestamp,
                endTimestamp
            )
        ).isFalse
        assertThat(jenkinsPipelineService.hasStageInTimeRange(pipelineId, targetStage, startTimestamp, endTimestamp)).isFalse
        assertThat(jenkinsPipelineService.hasStageInTimeRange(pipelineId, targetStage, startTimestamp, endTimestamp)).isFalse
    }

//    @Test
//    internal fun `should return all pipeline stages ordered by ASCII when there are two pipelines in one dashboard`() {
//        val dashboardId = "1"
//        val pipelines: List<Pipeline> = listOf(
//            Pipeline("2", "5km"),
//            Pipeline("1", "4km")
//        )
//        `when`(dashboardRepository.getPipelinesByDashboardId(dashboardId)).thenReturn(pipelines)
//
//        val buildsPipeline1: List<Build> = ObjectMapper().readValue(
//            this.javaClass.getResource("/repository/builds-for-pipeline-id-1.json").readText()
//        )
//        `when`(buildRepository.getAllBuilds("1")).thenReturn(buildsPipeline1)
//        val buildsForPipeline2: List<Build> = ObjectMapper().readValue(
//            this.javaClass.getResource("/repository/builds-for-pipeline-id-2.json").readText()
//        )
//        `when`(buildRepository.getAllBuilds("2")).thenReturn(buildsForPipeline2)
//
//        val actualPipelineStages = jenkinsPipelineService.getPipelineStages(dashboardId)
//
//        val expectedPipelineStages = listOf(
//            PipelineStagesResponse("4km", listOf("4km-DEV", "4km-PROD", "4km-UAT")),
//            PipelineStagesResponse("5km", listOf("5km-BUILD", "5km-DEV", "5km-PROD", "5km-UAT"))
//        )
//        assertEquals(expectedPipelineStages, actualPipelineStages)
//    }
}