package metrik.project.domain.service.githubactions

import metrik.exception.ApplicationException
import metrik.project.*
import metrik.project.domain.model.*
import metrik.project.domain.repository.BuildRepository
import metrik.project.rest.vo.response.SyncProgress
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
    fun `should sync all builds given first time synchronization and builds need to sync only one page`() {
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

        verify(buildRepository, times(1)).save(githubActionsBuild)
        verify(buildRepository, times(1)).save(githubActionsBuild.copy(number = 1111111112))
    }

    @Test
    fun `should sync all builds given first time synchronization and builds need to sync more than one page`() {
        `when`(buildRepository.getLatestBuild(pipelineID)).thenReturn(null)
        `when`(buildRepository.getInProgressBuilds(pipelineID)).thenReturn(emptyList())

        mockServer.expect(MockRestRequestMatchers.requestTo(verifyPipelineUrl))
            .andExpect { MockRestRequestMatchers.header("Authorization", credential) }
            .andRespond(
                MockRestResponseCreators.withSuccess(
                    this.javaClass.getResource(
                        "/pipeline/githubactions/verify-pipeline/runs-verify2.json"
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
                        "/pipeline/githubactions/runs/runs2.json"
                    ).readText(),
                    MediaType.APPLICATION_JSON
                )
            )

        mockServer.expect(MockRestRequestMatchers.requestTo("$getRunsBaseUrl?per_page=100&page=3"))
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

        verify(buildRepository, times(1)).save(githubActionsBuild)
        verify(buildRepository, times(1)).save(githubActionsBuild.copy(number = 1111111112))
        verify(buildRepository, times(1)).save(githubActionsBuild.copy(number = 1111111113))
        verify(buildRepository, times(1)).save(githubActionsBuild.copy(number = 1111111114))
    }

    @Test
    fun `should sync and save all in-progress builds to databases`() {
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
                        "/pipeline/githubactions/runs/in-progress-runs1.json"
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

        verify(buildRepository, times(1)).save(
            githubActionsBuild.copy(
                result = Status.IN_PROGRESS,
                stages = emptyList()
            )
        )
        verify(buildRepository, times(1)).save(
            githubActionsBuild.copy(
                result = Status.IN_PROGRESS,
                stages = emptyList(),
                number = 1111111112
            )
        )
    }

    @Test
    fun `should sync and update all previous in-progress builds`() {
        val build = githubActionsBuild.copy(result = Status.IN_PROGRESS, stages = emptyList())
        `when`(buildRepository.getLatestBuild(pipelineID)).thenReturn(build)
        `when`(buildRepository.getInProgressBuilds(pipelineID)).thenReturn(
            listOf(
                build
            )
        )

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
                        "/pipeline/githubactions/runs/empty-run.json"
                    ).readText(),
                    MediaType.APPLICATION_JSON
                )
            )

        mockServer.expect(MockRestRequestMatchers.requestTo("$getRunsBaseUrl/1111111111"))
            .andExpect { MockRestRequestMatchers.header("Authorization", credential) }
            .andRespond(
                MockRestResponseCreators.withSuccess(
                    this.javaClass.getResource(
                        "/pipeline/githubactions/run/in-progress-update-runs1.json"
                    ).readText(),
                    MediaType.APPLICATION_JSON
                )
            )

        githubActionsPipelineService.syncBuildsProgressively(githubActionsPipeline, mockEmitCb)

        verify(buildRepository, times(1)).save(githubActionsBuild)
    }

    @Test
    fun `should sync new builds, update all previous in-progress builds and emit the progress event`() {
        val build = githubActionsBuild.copy(result = Status.IN_PROGRESS, stages = emptyList())
        `when`(buildRepository.getLatestBuild(pipelineID)).thenReturn(build)
        `when`(buildRepository.getInProgressBuilds(pipelineID)).thenReturn(
            listOf(
                build
            )
        )

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
                        "/pipeline/githubactions/runs/runs3.json"
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

        mockServer.expect(MockRestRequestMatchers.requestTo("$getRunsBaseUrl/1111111111"))
            .andExpect { MockRestRequestMatchers.header("Authorization", credential) }
            .andRespond(
                MockRestResponseCreators.withSuccess(
                    this.javaClass.getResource(
                        "/pipeline/githubactions/run/in-progress-update-runs1.json"
                    ).readText(),
                    MediaType.APPLICATION_JSON
                )
            )

        githubActionsPipelineService.syncBuildsProgressively(githubActionsPipeline, mockEmitCb)

        verify(buildRepository, times(1)).save(
            githubActionsBuild.copy(
                number = 1111111113,
                result = Status.FAILED,
                timestamp = 1628766661000,
                stages = listOf(
                    stage.copy(
                        status = Status.FAILED,
                        startTimeMillis = 1628766661000,
                        completedTimeMillis = 1628766677000
                    )
                )
            )
        )
        verify(buildRepository, times(1)).save(
            githubActionsBuild.copy(
                number = 1111111114,
                timestamp = 1628766661000,
                stages = listOf(
                    stage.copy(
                        startTimeMillis = 1628766661000,
                        completedTimeMillis = 1628766677000
                    )
                )
            )
        )
        verify(buildRepository, times(1)).save(githubActionsBuild)

        val progress = SyncProgress(pipelineID, name, 1, 3)
        verify(mockEmitCb).invoke(progress)
        verify(mockEmitCb).invoke(progress.copy(progress = 2))
        verify(mockEmitCb).invoke(progress.copy(progress = 3))
    }

    @Test
    fun `should sync builds given status is completed and conclusion is non-supported types`(){
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
                        "/pipeline/githubactions/runs/non-supported-runs1.json"
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

        verify(buildRepository, times(1)).save(
            githubActionsBuild.copy(
                result = Status.OTHER,
                stages = emptyList()
            )
        )
        verify(buildRepository, times(1)).save(
            githubActionsBuild.copy(
                result = Status.OTHER,
                stages = emptyList(),
                number = 1111111112
            )
        )
    }

    private companion object {
        private const val baseUrl = "http://localhost:80/test_project/test_repo"
        private const val getRunsBaseUrl = "$baseUrl/actions/runs"
        private const val verifyPipelineUrl = "$getRunsBaseUrl?per_page=1"
    }
}
