package metrik.project.domain.service.githubactions

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import metrik.configuration.RestTemplateConfiguration
import metrik.exception.ApplicationException
import metrik.project.builds
import metrik.project.credential
import metrik.project.domain.model.Pipeline
import metrik.project.domain.model.Status
import metrik.project.domain.repository.BuildRepository
import metrik.project.githubActionsBuild
import metrik.project.githubActionsPipeline
import metrik.project.mockEmitCb
import metrik.project.name
import metrik.project.pipelineID
import metrik.project.rest.vo.response.SyncProgress
import metrik.project.stage
import metrik.project.userInputURL
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers
import org.springframework.test.web.client.response.MockRestResponseCreators
import org.springframework.web.client.RestTemplate

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
@ExtendWith(MockKExtension::class)
internal class GithubActionsPipelineServiceTest {

    @SpyK
    private var restTemplate: RestTemplate = RestTemplateConfiguration().restTemplate()

    @MockK(relaxed = true)
    private lateinit var buildRepository: BuildRepository

    @InjectMockKs
    private lateinit var githubActionsPipelineService: GithubActionsPipelineService

    private lateinit var mockServer: MockRestServiceServer

    @BeforeEach
    fun setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate)
    }

    @Test
    fun `should throw exception when verify pipeline given response is 500`() {
        mockServer.expect(MockRestRequestMatchers.requestTo("$userInputURL/actions/runs?per_page=1"))
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

        mockServer.expect(MockRestRequestMatchers.requestTo("$userInputURL/actions/runs?per_page=1"))
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
        every { buildRepository.getAllBuilds(pipelineID) } returns (builds)

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
        every { buildRepository.getLatestBuild(pipelineID) } returns (null)
        every { buildRepository.getInProgressBuilds(pipelineID) } returns (emptyList())

        mockServer.expect(MockRestRequestMatchers.requestTo(getRunsFirstPagePipelineUrl))
            .andExpect { MockRestRequestMatchers.header("Authorization", credential) }
            .andRespond(
                MockRestResponseCreators.withSuccess(
                    javaClass.getResource(
                        "/pipeline/githubactions/verify-pipeline/runs-verify1.json"
                    ).readText(),
                    MediaType.APPLICATION_JSON
                )
            )

        mockServer.expect(MockRestRequestMatchers.requestTo("$getRunsBaseUrl?per_page=100&page=1"))
            .andExpect { MockRestRequestMatchers.header("Authorization", credential) }
            .andRespond(
                MockRestResponseCreators.withSuccess(
                    javaClass.getResource(
                        "/pipeline/githubactions/runs/runs1.json"
                    ).readText(),
                    MediaType.APPLICATION_JSON
                )
            )

        mockServer.expect(MockRestRequestMatchers.requestTo("$getRunsBaseUrl?per_page=100&page=2"))
            .andExpect { MockRestRequestMatchers.header("Authorization", credential) }
            .andRespond(
                MockRestResponseCreators.withSuccess(
                    javaClass.getResource(
                        "/pipeline/githubactions/runs/empty-run.json"
                    ).readText(),
                    MediaType.APPLICATION_JSON
                )
            )

        githubActionsPipelineService.syncBuildsProgressively(githubActionsPipeline, mockEmitCb)

        verify {
            buildRepository.save(githubActionsBuild)
            buildRepository.save(githubActionsBuild.copy(number = 1111111112))
        }
    }

    @Test
    fun `should sync all builds given first time synchronization and builds need to sync more than one page`() {
        every { buildRepository.getLatestBuild(pipelineID) } returns (null)
        every { buildRepository.getInProgressBuilds(pipelineID) } returns (emptyList())

        mockServer.expect(MockRestRequestMatchers.requestTo(getRunsFirstPagePipelineUrl))
            .andExpect { MockRestRequestMatchers.header("Authorization", credential) }
            .andRespond(
                MockRestResponseCreators.withSuccess(
                    javaClass.getResource(
                        "/pipeline/githubactions/verify-pipeline/runs-verify2.json"
                    ).readText(),
                    MediaType.APPLICATION_JSON
                )
            )

        mockServer.expect(MockRestRequestMatchers.requestTo("$getRunsBaseUrl?per_page=100&page=1"))
            .andExpect { MockRestRequestMatchers.header("Authorization", credential) }
            .andRespond(
                MockRestResponseCreators.withSuccess(
                    javaClass.getResource(
                        "/pipeline/githubactions/runs/runs1.json"
                    ).readText(),
                    MediaType.APPLICATION_JSON
                )
            )

        mockServer.expect(MockRestRequestMatchers.requestTo("$getRunsBaseUrl?per_page=100&page=2"))
            .andExpect { MockRestRequestMatchers.header("Authorization", credential) }
            .andRespond(
                MockRestResponseCreators.withSuccess(
                    javaClass.getResource(
                        "/pipeline/githubactions/runs/runs2.json"
                    ).readText(),
                    MediaType.APPLICATION_JSON
                )
            )

        mockServer.expect(MockRestRequestMatchers.requestTo("$getRunsBaseUrl?per_page=100&page=3"))
            .andExpect { MockRestRequestMatchers.header("Authorization", credential) }
            .andRespond(
                MockRestResponseCreators.withSuccess(
                    javaClass.getResource(
                        "/pipeline/githubactions/runs/empty-run.json"
                    ).readText(),
                    MediaType.APPLICATION_JSON
                )
            )

        githubActionsPipelineService.syncBuildsProgressively(githubActionsPipeline, mockEmitCb)

        verify {
            buildRepository.save(githubActionsBuild)
            buildRepository.save(githubActionsBuild.copy(number = 1111111112))
            buildRepository.save(githubActionsBuild.copy(number = 1111111113))
            buildRepository.save(githubActionsBuild.copy(number = 1111111114))
        }
    }

    @Test
    fun `should sync and save all in-progress builds to databases`() {
        every { buildRepository.getLatestBuild(pipelineID) } returns (null)
        every { buildRepository.getInProgressBuilds(pipelineID) } returns (emptyList())

        mockServer.expect(MockRestRequestMatchers.requestTo(getRunsFirstPagePipelineUrl))
            .andExpect { MockRestRequestMatchers.header("Authorization", credential) }
            .andRespond(
                MockRestResponseCreators.withSuccess(
                    javaClass.getResource(
                        "/pipeline/githubactions/verify-pipeline/runs-verify1.json"
                    ).readText(),
                    MediaType.APPLICATION_JSON
                )
            )

        mockServer.expect(MockRestRequestMatchers.requestTo("$getRunsBaseUrl?per_page=100&page=1"))
            .andExpect { MockRestRequestMatchers.header("Authorization", credential) }
            .andRespond(
                MockRestResponseCreators.withSuccess(
                    javaClass.getResource(
                        "/pipeline/githubactions/runs/in-progress-runs1.json"
                    ).readText(),
                    MediaType.APPLICATION_JSON
                )
            )

        mockServer.expect(MockRestRequestMatchers.requestTo("$getRunsBaseUrl?per_page=100&page=2"))
            .andExpect { MockRestRequestMatchers.header("Authorization", credential) }
            .andRespond(
                MockRestResponseCreators.withSuccess(
                    javaClass.getResource(
                        "/pipeline/githubactions/runs/empty-run.json"
                    ).readText(),
                    MediaType.APPLICATION_JSON
                )
            )

        githubActionsPipelineService.syncBuildsProgressively(githubActionsPipeline, mockEmitCb)

        verify {
            buildRepository.save(
                githubActionsBuild.copy(
                    result = Status.IN_PROGRESS,
                    stages = emptyList()
                )
            )
            buildRepository.save(
                githubActionsBuild.copy(
                    result = Status.IN_PROGRESS,
                    stages = emptyList(),
                    number = 1111111112
                )
            )
        }
    }

    @Test
    fun `should sync and update all previous in-progress builds`() {
        val build = githubActionsBuild.copy(result = Status.IN_PROGRESS, stages = emptyList())
        every { buildRepository.getLatestBuild(pipelineID) } returns (build)
        every { buildRepository.getInProgressBuilds(pipelineID) } returns (listOf(build))

        mockServer.expect(MockRestRequestMatchers.requestTo(getRunsFirstPagePipelineUrl))
            .andExpect { MockRestRequestMatchers.header("Authorization", credential) }
            .andRespond(
                MockRestResponseCreators.withSuccess(
                    javaClass.getResource(
                        "/pipeline/githubactions/verify-pipeline/runs-verify1.json"
                    ).readText(),
                    MediaType.APPLICATION_JSON
                )
            )

        mockServer.expect(MockRestRequestMatchers.requestTo("$getRunsBaseUrl?per_page=100&page=1"))
            .andExpect { MockRestRequestMatchers.header("Authorization", credential) }
            .andRespond(
                MockRestResponseCreators.withSuccess(
                    javaClass.getResource(
                        "/pipeline/githubactions/runs/empty-run.json"
                    ).readText(),
                    MediaType.APPLICATION_JSON
                )
            )

        mockServer.expect(MockRestRequestMatchers.requestTo("$getRunsBaseUrl/1111111111"))
            .andExpect { MockRestRequestMatchers.header("Authorization", credential) }
            .andRespond(
                MockRestResponseCreators.withSuccess(
                    javaClass.getResource(
                        "/pipeline/githubactions/run/in-progress-update-runs1.json"
                    ).readText(),
                    MediaType.APPLICATION_JSON
                )
            )

        githubActionsPipelineService.syncBuildsProgressively(githubActionsPipeline, mockEmitCb)

        verify {
            buildRepository.save(githubActionsBuild)
        }
    }

    @Test
    fun `should sync new builds, update all previous in-progress builds and emit the progress event`() {
        val build = githubActionsBuild.copy(result = Status.IN_PROGRESS, stages = emptyList())
        every { buildRepository.getLatestBuild(pipelineID) } returns (build)
        every { buildRepository.getInProgressBuilds(pipelineID) } returns (listOf(build))

        mockServer.expect(MockRestRequestMatchers.requestTo(getRunsFirstPagePipelineUrl))
            .andExpect { MockRestRequestMatchers.header("Authorization", credential) }
            .andRespond(
                MockRestResponseCreators.withSuccess(
                    javaClass.getResource(
                        "/pipeline/githubactions/verify-pipeline/runs-verify1.json"
                    ).readText(),
                    MediaType.APPLICATION_JSON
                )
            )

        mockServer.expect(MockRestRequestMatchers.requestTo("$getRunsBaseUrl?per_page=100&page=1"))
            .andExpect { MockRestRequestMatchers.header("Authorization", credential) }
            .andRespond(
                MockRestResponseCreators.withSuccess(
                    javaClass.getResource(
                        "/pipeline/githubactions/runs/runs3.json"
                    ).readText(),
                    MediaType.APPLICATION_JSON
                )
            )

        mockServer.expect(MockRestRequestMatchers.requestTo("$getRunsBaseUrl?per_page=100&page=2"))
            .andExpect { MockRestRequestMatchers.header("Authorization", credential) }
            .andRespond(
                MockRestResponseCreators.withSuccess(
                    javaClass.getResource(
                        "/pipeline/githubactions/runs/empty-run.json"
                    ).readText(),
                    MediaType.APPLICATION_JSON
                )
            )

        mockServer.expect(MockRestRequestMatchers.requestTo("$getRunsBaseUrl/1111111111"))
            .andExpect { MockRestRequestMatchers.header("Authorization", credential) }
            .andRespond(
                MockRestResponseCreators.withSuccess(
                    javaClass.getResource(
                        "/pipeline/githubactions/run/in-progress-update-runs1.json"
                    ).readText(),
                    MediaType.APPLICATION_JSON
                )
            )

        githubActionsPipelineService.syncBuildsProgressively(githubActionsPipeline, mockEmitCb)

        val progress = SyncProgress(pipelineID, name, 1, 3)

        verify {
            buildRepository.save(
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
            buildRepository.save(
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
            buildRepository.save(githubActionsBuild)
            mockEmitCb.invoke(progress)
            mockEmitCb.invoke(progress.copy(progress = 2))
            mockEmitCb.invoke(progress.copy(progress = 3))
        }
    }

    @Test
    fun `should sync builds given status is completed and conclusion is non-supported types`() {
        every { buildRepository.getLatestBuild(pipelineID) } returns (null)
        every { buildRepository.getInProgressBuilds(pipelineID) } returns (emptyList())

        mockServer.expect(MockRestRequestMatchers.requestTo(getRunsFirstPagePipelineUrl))
            .andExpect { MockRestRequestMatchers.header("Authorization", credential) }
            .andRespond(
                MockRestResponseCreators.withSuccess(
                    javaClass.getResource(
                        "/pipeline/githubactions/verify-pipeline/runs-verify1.json"
                    ).readText(),
                    MediaType.APPLICATION_JSON
                )
            )

        mockServer.expect(MockRestRequestMatchers.requestTo("$getRunsBaseUrl?per_page=100&page=1"))
            .andExpect { MockRestRequestMatchers.header("Authorization", credential) }
            .andRespond(
                MockRestResponseCreators.withSuccess(
                    javaClass.getResource(
                        "/pipeline/githubactions/runs/non-supported-runs1.json"
                    ).readText(),
                    MediaType.APPLICATION_JSON
                )
            )

        mockServer.expect(MockRestRequestMatchers.requestTo("$getRunsBaseUrl?per_page=100&page=2"))
            .andExpect { MockRestRequestMatchers.header("Authorization", credential) }
            .andRespond(
                MockRestResponseCreators.withSuccess(
                    javaClass.getResource(
                        "/pipeline/githubactions/runs/empty-run.json"
                    ).readText(),
                    MediaType.APPLICATION_JSON
                )
            )

        githubActionsPipelineService.syncBuildsProgressively(githubActionsPipeline, mockEmitCb)

        verify {
            buildRepository.save(
                githubActionsBuild.copy(
                    result = Status.OTHER,
                    stages = emptyList()
                )
            )
            buildRepository.save(
                githubActionsBuild.copy(
                    result = Status.OTHER,
                    stages = emptyList(),
                    number = 1111111112
                )
            )
        }
    }

    @Test
    fun `should ignore not found in-progress builds and continue with next in-progress build`() {
        val build = githubActionsBuild.copy(result = Status.IN_PROGRESS, stages = emptyList())

        every { buildRepository.getLatestBuild(pipelineID) } returns (null)
        every { buildRepository.getInProgressBuilds(pipelineID) } returns
            listOf(
                build,
                build.copy(
                    number = 1111111112,
                    url = "http://localhost:80/test_project/test_repo/actions/runs/1111111112"
                )
            )

        mockServer.expect(MockRestRequestMatchers.requestTo(getRunsFirstPagePipelineUrl))
            .andExpect { MockRestRequestMatchers.header("Authorization", credential) }
            .andRespond(
                MockRestResponseCreators.withSuccess(
                    javaClass.getResource(
                        "/pipeline/githubactions/verify-pipeline/runs-verify1.json"
                    ).readText(),
                    MediaType.APPLICATION_JSON
                )
            )

        mockServer.expect(MockRestRequestMatchers.requestTo("$getRunsBaseUrl?per_page=100&page=1"))
            .andExpect { MockRestRequestMatchers.header("Authorization", credential) }
            .andRespond(
                MockRestResponseCreators.withSuccess(
                    javaClass.getResource(
                        "/pipeline/githubactions/runs/empty-run.json"
                    ).readText(),
                    MediaType.APPLICATION_JSON
                )
            )

        mockServer.expect(MockRestRequestMatchers.requestTo("$getRunsBaseUrl/1111111111"))
            .andExpect { MockRestRequestMatchers.header("Authorization", credential) }
            .andRespond(
                MockRestResponseCreators.withStatus(HttpStatus.NOT_FOUND)
            )

        mockServer.expect(MockRestRequestMatchers.requestTo("$getRunsBaseUrl/1111111112"))
            .andExpect { MockRestRequestMatchers.header("Authorization", credential) }
            .andRespond(
                MockRestResponseCreators.withSuccess(
                    javaClass.getResource(
                        "/pipeline/githubactions/run/in-progress-update-runs2.json"
                    ).readText(),
                    MediaType.APPLICATION_JSON
                )
            )

        githubActionsPipelineService.syncBuildsProgressively(githubActionsPipeline, mockEmitCb)

        verify(exactly = 1) {
            buildRepository.save(
                githubActionsBuild.copy(
                    number = 1111111112,
                    url = "http://localhost:80/test_project/test_repo/actions/runs/1111111112"
                )
            )
        }
    }

    @Test
    fun `should stop calling next page api when the current api call throw not found exception and sync builds before that exception is thrown`() {
        every { buildRepository.getLatestBuild(pipelineID) } returns (null)
        every { buildRepository.getInProgressBuilds(pipelineID) } returns (emptyList())

        mockServer.expect(MockRestRequestMatchers.requestTo(getRunsFirstPagePipelineUrl))
            .andExpect { MockRestRequestMatchers.header("Authorization", credential) }
            .andRespond(
                MockRestResponseCreators.withSuccess(
                    javaClass.getResource(
                        "/pipeline/githubactions/verify-pipeline/runs-verify2.json"
                    ).readText(),
                    MediaType.APPLICATION_JSON
                )
            )

        mockServer.expect(MockRestRequestMatchers.requestTo("$getRunsBaseUrl?per_page=100&page=1"))
            .andExpect { MockRestRequestMatchers.header("Authorization", credential) }
            .andRespond(
                MockRestResponseCreators.withSuccess(
                    javaClass.getResource(
                        "/pipeline/githubactions/runs/runs1.json"
                    ).readText(),
                    MediaType.APPLICATION_JSON
                )
            )

        mockServer.expect(MockRestRequestMatchers.requestTo("$getRunsBaseUrl?per_page=100&page=2"))
            .andExpect { MockRestRequestMatchers.header("Authorization", credential) }
            .andRespond(
                MockRestResponseCreators.withStatus(HttpStatus.NOT_FOUND)
            )

        githubActionsPipelineService.syncBuildsProgressively(githubActionsPipeline, mockEmitCb)

        verify {
            buildRepository.save(githubActionsBuild)
            buildRepository.save(githubActionsBuild.copy(number = 1111111112))
        }
    }

    @Test
    fun `should throw exception when the server responds 500 at any time`() {
        every { buildRepository.getLatestBuild(pipelineID) } returns (null)
        every { buildRepository.getInProgressBuilds(pipelineID) } returns (emptyList())

        mockServer.expect(MockRestRequestMatchers.requestTo(getRunsFirstPagePipelineUrl))
            .andExpect { MockRestRequestMatchers.header("Authorization", credential) }
            .andRespond(
                MockRestResponseCreators.withSuccess(
                    javaClass.getResource(
                        "/pipeline/githubactions/verify-pipeline/runs-verify2.json"
                    ).readText(),
                    MediaType.APPLICATION_JSON
                )
            )

        mockServer.expect(MockRestRequestMatchers.requestTo("$getRunsBaseUrl?per_page=100&page=1"))
            .andExpect { MockRestRequestMatchers.header("Authorization", credential) }
            .andRespond(
                MockRestResponseCreators.withSuccess(
                    javaClass.getResource(
                        "/pipeline/githubactions/runs/runs1.json"
                    ).readText(),
                    MediaType.APPLICATION_JSON
                )
            )

        mockServer.expect(MockRestRequestMatchers.requestTo("$getRunsBaseUrl?per_page=100&page=2"))
            .andExpect { MockRestRequestMatchers.header("Authorization", credential) }
            .andRespond(
                MockRestResponseCreators.withServerError()
            )

        Assertions.assertThrows(ApplicationException::class.java) {
            githubActionsPipelineService.syncBuildsProgressively(githubActionsPipeline, mockEmitCb)
        }
    }

    private companion object {
        private const val baseUrl = "http://localhost:80/test_project/test_repo"
        private const val getRunsBaseUrl = "$baseUrl/actions/runs"
        private const val getRunsFirstPagePipelineUrl = "$getRunsBaseUrl?per_page=1"
    }
}
