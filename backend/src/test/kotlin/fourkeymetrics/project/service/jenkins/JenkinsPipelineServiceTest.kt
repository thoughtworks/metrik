package fourkeymetrics.project.service.jenkins

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import fourkeymetrics.common.model.Build
import fourkeymetrics.common.model.Status
import fourkeymetrics.exception.ApplicationException
import fourkeymetrics.project.model.Pipeline
import fourkeymetrics.project.repository.BuildRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
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
import org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest
import org.springframework.test.web.client.response.MockRestResponseCreators.withServerError
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import org.springframework.web.client.RestTemplate


@ExtendWith(SpringExtension::class)
@Import(JenkinsPipelineService::class, BuildRepository::class, ObjectMapper::class, RestTemplate::class)
@RestClientTest
internal class JenkinsPipelineServiceTest {
    @Autowired
    private lateinit var jenkinsPipelineService: JenkinsPipelineService

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var restTemplate: RestTemplate

    @MockBean
    private lateinit var buildRepository: BuildRepository


    @Test
    internal fun `should return all builds from Jenkins when syncBuilds() given pipeline ID`() {
        val username = "fake-user"
        val credential = "fake-credential"
        val baseUrl = "http://localhost"
        val getBuildSummariesUrl = "$baseUrl/api/json?tree=allBuilds%5Bbuilding," +
                "number,result,timestamp,duration,url,changeSets%5Bitems%5BcommitId,timestamp,msg,date%5D%5D%5D"
        val getBuildDetailUrl = "$baseUrl/82/wfapi/describe"
        val mockServer = MockRestServiceServer.createServer(restTemplate)
        val pipeline = Pipeline(
            id = "fake pipeline",
            username = username,
            credential = credential,
            url = baseUrl
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
        val allBuilds = jenkinsPipelineService.syncBuilds(pipeline)
        assertThat(allBuilds[0].pipelineId).isEqualTo(expectedBuilds[0].pipelineId)
        verify(buildRepository, times(1)).save(allBuilds)
    }

    @Test
    internal fun `should sync builds from Jenkins when syncBuilds() given build status is failure`() {
        val username = "fake-user"
        val credential = "fake-credential"
        val baseUrl = "http://localhost"
        val getBuildSummariesUrl = "$baseUrl/api/json?tree=allBuilds%5Bbuilding," +
                "number,result,timestamp,duration,url,changeSets%5Bitems%5BcommitId,timestamp,msg,date%5D%5D%5D"
        val getBuildDetailUrl = "$baseUrl/82/wfapi/describe"
        val mockServer = MockRestServiceServer.createServer(restTemplate)
        val pipeline = Pipeline(
            id = "fake pipeline",
            username = username,
            credential = credential,
            url = baseUrl
        )
        mockServer.expect(requestTo(getBuildSummariesUrl))
            .andRespond(
                withSuccess(
                    this.javaClass.getResource("/pipeline/raw-builds-3.json").readText(),
                    MediaType.APPLICATION_JSON
                )
            )
        mockServer.expect(requestTo(getBuildDetailUrl))
            .andRespond(
                withSuccess(
                    this.javaClass.getResource("/pipeline/raw-build-detail-3.json").readText(),
                    MediaType.APPLICATION_JSON
                )
            )

        val expectedBuilds: List<Build> =
            objectMapper.readValue(this.javaClass.getResource("/pipeline/builds-for-jenkins-3.json").readText())
        val allBuilds = jenkinsPipelineService.syncBuilds(pipeline)
        assertThat(allBuilds[0].pipelineId).isEqualTo(expectedBuilds[0].pipelineId)
        verify(buildRepository, times(1)).save(allBuilds)
    }

    @Test
    internal fun `should sync builds from Jenkins when syncBuilds() given build status is null`() {
        val username = "fake-user"
        val credential = "fake-credential"
        val baseUrl = "http://localhost"
        val getBuildSummariesUrl = "$baseUrl/api/json?tree=allBuilds%5Bbuilding," +
                "number,result,timestamp,duration,url,changeSets%5Bitems%5BcommitId,timestamp,msg,date%5D%5D%5D"
        val getBuildDetailUrl = "$baseUrl/82/wfapi/describe"
        val mockServer = MockRestServiceServer.createServer(restTemplate)
        val pipeline = Pipeline(
            id = "fake pipeline",
            username = username,
            credential = credential,
            url = baseUrl
        )
        mockServer.expect(requestTo(getBuildSummariesUrl))
            .andRespond(
                withSuccess(
                    this.javaClass.getResource("/pipeline/raw-builds-4.json").readText(),
                    MediaType.APPLICATION_JSON
                )
            )
        mockServer.expect(requestTo(getBuildDetailUrl))
            .andRespond(
                withSuccess(
                    this.javaClass.getResource("/pipeline/raw-build-detail-4.json").readText(),
                    MediaType.APPLICATION_JSON
                )
            )

        val expectedBuilds: List<Build> =
            objectMapper.readValue(this.javaClass.getResource("/pipeline/builds-for-jenkins-4.json").readText())
        val allBuilds = jenkinsPipelineService.syncBuilds(pipeline)
        assertThat(allBuilds[0].pipelineId).isEqualTo(expectedBuilds[0].pipelineId)
        verify(buildRepository, times(1)).save(allBuilds)
    }

    @Test
    internal fun `should sync builds from Jenkins when syncBuilds() given build status is success`() {
        val username = "fake-user"
        val credential = "fake-credential"
        val baseUrl = "http://localhost"
        val getBuildSummariesUrl = "$baseUrl/api/json?tree=allBuilds%5Bbuilding," +
                "number,result,timestamp,duration,url,changeSets%5Bitems%5BcommitId,timestamp,msg,date%5D%5D%5D"
        val getBuildDetailUrl = "$baseUrl/82/wfapi/describe"
        val mockServer = MockRestServiceServer.createServer(restTemplate)
        val pipeline = Pipeline(
            id = "fake pipeline",
            username = username,
            credential = credential,
            url = baseUrl
        )
        mockServer.expect(requestTo(getBuildSummariesUrl))
            .andRespond(
                withSuccess(
                    this.javaClass.getResource("/pipeline/raw-builds-5.json").readText(),
                    MediaType.APPLICATION_JSON
                )
            )
        mockServer.expect(requestTo(getBuildDetailUrl))
            .andRespond(
                withSuccess(
                    this.javaClass.getResource("/pipeline/raw-build-detail-5.json").readText(),
                    MediaType.APPLICATION_JSON
                )
            )

        val expectedBuilds: List<Build> =
            objectMapper.readValue(this.javaClass.getResource("/pipeline/builds-for-jenkins-5.json").readText())
        val allBuilds = jenkinsPipelineService.syncBuilds(pipeline)
        assertThat(allBuilds[0].pipelineId).isEqualTo(expectedBuilds[0].pipelineId)
        verify(buildRepository, times(1)).save(allBuilds)
    }


    @Test
    internal fun `should return builds with previous status is building null or not exits in DB from Jenkins when syncBuilds() given pipeline ID`() {
        val pipelineId = "fake pipeline"
        val username = "fake-user"
        val credential = "fake-credential"
        val baseUrl = "http://localhost"
        val getBuildSummariesUrl = "$baseUrl/api/json?tree=allBuilds%5Bbuilding," +
                "number,result,timestamp,duration,url,changeSets%5Bitems%5BcommitId,timestamp,msg,date%5D%5D%5D"
        val getBuild1DetailUrl = "$baseUrl/1/wfapi/describe"
        val getBuild2DetailUrl = "$baseUrl/2/wfapi/describe"
        val mockServer = MockRestServiceServer.bindTo(restTemplate).ignoreExpectOrder(true).build()
        val pipeline = Pipeline(
            id = "fake pipeline",
            username = username,
            credential = credential,
            url = baseUrl
        )

        `when`(buildRepository.getByBuildNumber(pipelineId, 1)).thenReturn(
            Build(pipelineId = pipelineId, number = 1, result = Status.IN_PROGRESS)
        )

        mockServer.expect(requestTo(getBuildSummariesUrl))
            .andRespond(
                withSuccess(
                    this.javaClass.getResource("/pipeline/raw-builds-2.json").readText(),
                    MediaType.APPLICATION_JSON
                )
            )
        mockServer.expect(requestTo(getBuild1DetailUrl))
            .andRespond(
                withSuccess(
                    this.javaClass.getResource("/pipeline/raw-build-detail-2.json").readText(),
                    MediaType.APPLICATION_JSON
                )
            )
        mockServer.expect(requestTo(getBuild2DetailUrl))
            .andRespond(
                withSuccess(
                    this.javaClass.getResource("/pipeline/raw-build-detail-2.json").readText(),
                    MediaType.APPLICATION_JSON
                )
            )

        jenkinsPipelineService.syncBuilds(pipeline)
    }

    @Test
    internal fun `should throw exception when verify pipeline given response is 500`() {
        val username = "fake-user"
        val credential = "fake-credential"
        val baseUrl = "http://localhost"
        val mockServer = MockRestServiceServer.bindTo(restTemplate).ignoreExpectOrder(true).build()

        mockServer.expect(requestTo("${baseUrl}/wfapi/"))
            .andRespond(
                withServerError()
            )

        Assertions.assertThrows(ApplicationException::class.java) {
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
    internal fun `should throw exception when verify pipeline given response is 400`() {
        val username = "fake-user"
        val credential = "fake-credential"
        val baseUrl = "http://localhost"
        val mockServer = MockRestServiceServer.bindTo(restTemplate).ignoreExpectOrder(true).build()

        mockServer.expect(requestTo("${baseUrl}/wfapi/"))
            .andRespond(
                withBadRequest()
            )

        Assertions.assertThrows(ApplicationException::class.java) {
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
}