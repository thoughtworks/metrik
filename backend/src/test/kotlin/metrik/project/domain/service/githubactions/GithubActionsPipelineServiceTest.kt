package metrik.project.domain.service.githubactions

import feign.FeignException
import feign.Request
import feign.Response
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import metrik.exception.ApplicationException
import metrik.project.*
import metrik.project.domain.model.PipelineConfiguration
import metrik.project.domain.model.Status
import metrik.project.domain.repository.BuildRepository
import metrik.project.infrastructure.github.feign.GithubFeignClient
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "LargeClass")
@ExtendWith(MockKExtension::class)
internal class GithubActionsPipelineServiceTest {
    @MockK(relaxed = true)
    private lateinit var buildRepository: BuildRepository

    @InjectMockKs
    private lateinit var pipelineService: GithubActionsPipelineService

    @MockK(relaxed = true)
    private lateinit var commitService: CommitService

    @MockK(relaxed = true)
    private lateinit var runService: RunService

    @MockK(relaxed = true)
    private lateinit var githubFeignClient: GithubFeignClient

    @MockK(relaxed = true)
    private lateinit var pipelineCommitService: PipelineCommitService

    @MockK(relaxed = true)
    private lateinit var pipelineRunService: PipelineRunService


    @SpyK
    private var executionConverter: ExecutionConverter = ExecutionConverter()

    private val dummyFeignRequest = Request.create(Request.HttpMethod.POST, "url", mapOf(), null, null, null)

    @Test
    fun `should successfully verify a pipeline given response is 200`() {
        pipelineService.verifyPipelineConfiguration(githubActionsPipeline)

        verify {
            githubFeignClient.retrieveMultipleRuns(any(), any(), any())
        }
    }

    @Test
    fun `should throw exception when verify pipeline given response is 500`() {
        every {
            githubFeignClient.retrieveMultipleRuns(any(), any(), any())
        } throws FeignException.errorStatus(
            "GET",
            buildFeignResponse(500)
        )

        assertThrows(ApplicationException::class.java) {
            pipelineService.verifyPipelineConfiguration(
                PipelineConfiguration(credential = credential, url = userInputURL)
            )
        }
    }

    @Test
    fun `should throw exception when verify pipeline given response is 404`() {
        every {
            githubFeignClient.retrieveMultipleRuns(any(), any(), any())
        } throws FeignException.errorStatus(
            "GET",
            buildFeignResponse(404)
        )

        assertThrows(ApplicationException::class.java) {
            pipelineService.verifyPipelineConfiguration(
                PipelineConfiguration(credential = credential, url = userInputURL)
            )
        }
    }

    @Test
    fun `should return sorted stage name lists when getStagesSortedByName() called`() {
        every { buildRepository.getAllBuilds(pipelineId) } returns (executions)

        val result = pipelineService.getStagesSortedByName(pipelineId)

        assertEquals(5, result.size)
        assertEquals("amazing", result[0])
        assertEquals("build", result[1])
        assertEquals("clone", result[2])
        assertEquals("good", result[3])
        assertEquals("zzz", result[4])
    }

    @Test
    fun `should sync and save all in-progress and completed-status builds to databases`() {
        every { pipelineRunService.getNewRuns(githubActionsPipeline, mockEmitCb ) } returns(mutableListOf( githubActionsRun1, githubActionsRun2))
        every { pipelineRunService.getInProgressRuns(githubActionsPipeline, mockEmitCb) } returns(listOf(githubActionsRun1, githubActionsRun2))
        every { pipelineCommitService.mapCommitToRun(githubActionsPipeline,any(), mockEmitCb) } returns mapOf(
            "master" to mapOf(githubActionsRun1 to listOf(commit)),
            "feature/CI pipeline" to mapOf(githubActionsRun2 to listOf(commit)
        ))

        pipelineService.syncBuildsProgressively(githubActionsPipeline, mockEmitCb)

        verify {
            buildRepository.save(
                githubActionsExecution.copy(
                    number = 123,
                    result = Status.OTHER,
                    stages = emptyList(),
                    duration = 0,
                    timestamp = 1629203005000,
                    url="12345",
                )
            )
            buildRepository.save(
                githubActionsExecution.copy(
                    result = Status.IN_PROGRESS,
                    branch = "feature/CI pipeline",
                    stages = emptyList(),
                )
            )
            buildRepository.save(
                githubActionsExecution.copy(
                    number = 123,
                    result = Status.OTHER,
                    stages = emptyList(),
                    duration = 0,
                    timestamp=1629203005000,
                    url="12345"
                )
            )
            buildRepository.save(
                githubActionsExecution.copy(
                    result = Status.IN_PROGRESS,
                    branch = "feature/CI pipeline",
                    stages = emptyList(),
                )
            )
        }
    }

    private fun buildFeignResponse(statusCode: Int) =
        Response.builder().status(statusCode).request(dummyFeignRequest).build()
}
