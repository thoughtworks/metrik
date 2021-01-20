package fourkeymetrics.pipeline

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import fourkeymetrics.model.Build
import fourkeymetrics.model.PipelineConfiguration
import fourkeymetrics.model.Stage
import fourkeymetrics.repository.BuildRepository
import fourkeymetrics.repository.PipelineRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.`when`
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
@Import(Jenkins::class, PipelineRepository::class, BuildRepository::class, ObjectMapper::class,
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
    private lateinit var pipelineRepository: PipelineRepository

    @MockBean
    private lateinit var buildRepository: BuildRepository

    @Test
    internal fun `should return true given pipeline exits in database`() {
        val dashboardId = "dashboard ID"
        val pipelineId = "fake pipeline"

        `when`(pipelineRepository.getPipeline(dashboardId, pipelineId)).thenReturn(PipelineConfiguration())

        assertThat(jenkins.hasPipeline(dashboardId, pipelineId)).isTrue
    }

    @Test
    internal fun `should return false given pipeline not exits in database`() {
        val dashboardId = "dashboard ID"
        val pipelineId = "fake pipeline"

        `when`(pipelineRepository.getPipeline(dashboardId, pipelineId)).thenReturn(null)

        assertThat(jenkins.hasPipeline(dashboardId, pipelineId)).isFalse
    }

    @Test
    internal fun `should return true given stage exits in last build`() {
        val pipelineId = "fake pipeline"
        val targetStage = "deploy to dev"
        val lastBuild = Build(stages = listOf(Stage(name = targetStage), Stage(name = "build")))

        `when`(buildRepository.getLastBuild(pipelineId)).thenReturn(lastBuild)

        assertThat(jenkins.hasStageInLastBuild(pipelineId, targetStage)).isTrue
    }

    @Test
    internal fun `should return false given stage not exits in last build`() {
        val pipelineId = "fake pipeline"
        val targetStage = "deploy to dev"
        val lastBuild = Build(stages = listOf(Stage(name = "clone"), Stage(name = "build")))

        `when`(buildRepository.getLastBuild(pipelineId)).thenReturn(lastBuild)

        assertThat(jenkins.hasStageInLastBuild(pipelineId, targetStage)).isFalse
    }

    @Test
    internal fun `should return false given no builds exist in pipeline`() {
        val pipelineId = "fake pipeline"
        val targetStage = "deploy to dev"

        `when`(buildRepository.getLastBuild(pipelineId)).thenReturn(null)

        assertThat(jenkins.hasStageInLastBuild(pipelineId, targetStage)).isFalse
    }

    @Test
    internal fun `should return all builds from Jenkins given pipeline ID`() {
        val dashboardId = "dashboard ID"
        val pipelineId = "fake pipeline"
        val username = "fake-user"
        val token = "fake-token"
        val baseUrl = "localhost"
        val getBuildSummariesUrl = "http://$username:$token@$baseUrl/api/json?tree=allBuilds%5Bbuilding," +
            "number,result,timestamp,duration,url,changeSets%5Bitems%5BcommitId,timestamp,msg,date%5D%5D%5D"
        val getBuildDetailUrl = "http://$username:$token@$baseUrl/82/wfapi/describe"
        val mockServer = MockRestServiceServer.createServer(restTemplate)

        `when`(pipelineRepository.getPipeline(dashboardId, pipelineId)).thenReturn(PipelineConfiguration(username, token, baseUrl))
        mockServer.expect(requestTo(getBuildSummariesUrl))
            .andRespond(withSuccess(this.javaClass.getResource("/pipeline/raw-builds-1.json").readText(), MediaType.APPLICATION_JSON))
        mockServer.expect(requestTo(getBuildDetailUrl))
            .andRespond(withSuccess(this.javaClass.getResource("/pipeline/raw-build-detail-1.json").readText(), MediaType.APPLICATION_JSON))

        val expectedBuilds: List<Build> = objectMapper.readValue(this.javaClass.getResource("/pipeline/builds-1.json").readText())
        assertThat(jenkins.fetchAllBuilds(dashboardId, pipelineId)).isEqualTo(expectedBuilds)
    }
}