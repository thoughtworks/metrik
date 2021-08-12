package metrik.project.domain.service.githubactions

import metrik.exception.ApplicationException
import metrik.project.*
import metrik.project.domain.model.*
import metrik.project.domain.repository.BuildRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
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
import org.springframework.test.web.client.match.MockRestRequestMatchers
import org.springframework.test.web.client.response.MockRestResponseCreators
import org.springframework.web.client.RestTemplate

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
@ExtendWith(SpringExtension::class)
@Import(GithubActionsPipelineService::class, RestTemplate::class)
@RestClientTest
internal class GithubActionsPipelineServiceTest {

    @Autowired
    private lateinit var restTemplate: RestTemplate

    @Autowired
    private lateinit var githubActionsPipelineService: GithubActionsPipelineService

    @MockBean
    private lateinit var buildRepository: BuildRepository

    private lateinit var mockServer: MockRestServiceServer

    @BeforeEach
    fun setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate)
    }

    @Test
    fun `should throw exception when verify pipeline given response is 500`() {
        mockServer.expect(MockRestRequestMatchers.requestTo("$url/actions/runs?per_page=1"))
            .andRespond(
                MockRestResponseCreators.withServerError()
            )

        Assertions.assertThrows(ApplicationException::class.java) {
            githubActionsPipelineService.verifyPipelineConfiguration(
                Pipeline(credential = credential, url = userInputURL)
            )
        }
    }

    @Test
    fun `should throw exception when verify pipeline given response is 400`() {

        mockServer.expect(MockRestRequestMatchers.requestTo("$url/actions/runs?per_page=1"))
            .andRespond(
                MockRestResponseCreators.withBadRequest()
            )

        Assertions.assertThrows(ApplicationException::class.java) {
            githubActionsPipelineService.verifyPipelineConfiguration(
                Pipeline(credential = credential, url = userInputURL)
            )
        }
    }

    @Test
    fun `should return sorted stage name lists when getStagesSortedByName() called`() {
        `when`(buildRepository.getAllBuilds(pipelineID)).thenReturn(builds)

        val result = githubActionsPipelineService.getStagesSortedByName(pipelineID)

        Assertions.assertEquals(5, result.size)
        Assertions.assertEquals("amazing", result[0])
        Assertions.assertEquals("build", result[1])
        Assertions.assertEquals("clone", result[2])
        Assertions.assertEquals("good", result[3])
        Assertions.assertEquals("zzz", result[4])
    }

    @Test
    fun `should sync all builds given first time synchronization and builds need to sync are below maxPerPage`() {
        `when`(buildRepository.getLatestBuild(pipelineID)).thenReturn(null)
        `when`(buildRepository.getInProgressBuilds(pipelineID)).thenReturn(emptyList())

        mockServer.expect(MockRestRequestMatchers.requestTo(verifyPipelineUrl))
            .andExpect { MockRestRequestMatchers.header("Authorization", credential) }
            .andRespond(
                MockRestResponseCreators.withSuccess(
                    this.javaClass.getResource(
                        "/pipeline/githubactions/verify-pipeline/runs-verify1.json"
                    ).readText(),
                    MediaType.APPLICATION_JSON
                )
            )

        mockServer.expect(MockRestRequestMatchers.requestTo("$getRunsBaseUrl?per_page=100&page=1"))
            .andExpect { MockRestRequestMatchers.header("Authorization", credential) }
            .andRespond(
                MockRestResponseCreators.withSuccess(
                    this.javaClass.getResource(
                        "/pipeline/githubactions/runs/runs1.json"
                    ).readText(),
                    MediaType.APPLICATION_JSON
                )
            )

        mockServer.expect(MockRestRequestMatchers.requestTo("$getRunsBaseUrl?per_page=100&page=2"))
            .andExpect { MockRestRequestMatchers.header("Authorization", credential) }
            .andRespond(
                MockRestResponseCreators.withSuccess(
                    this.javaClass.getResource(
                        "/pipeline/githubactions/runs/empty-run.json"
                    ).readText(),
                    MediaType.APPLICATION_JSON
                )
            )

        githubActionsPipelineService.syncBuildsProgressively(githubActionsPipeline, mockEmitCb)

        verify(buildRepository, times(1)).save(githubActionsBuildOne)
        verify(buildRepository, times(1)).save(githubActionsBuildTwo)
    }

    private companion object {
        private const val baseUrl = "http://localhost:80/test_project/test_repo"
        private const val getRunsBaseUrl = "$baseUrl/actions/runs"
        private const val verifyPipelineUrl = "$getRunsBaseUrl?per_page=1"
    }
}
