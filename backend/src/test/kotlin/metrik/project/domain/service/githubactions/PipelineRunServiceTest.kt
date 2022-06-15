package metrik.project.domain.service.githubactions

import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import metrik.project.TestFixture.githubActionsExecution
import metrik.project.TestFixture.githubActionsPipeline
import metrik.project.TestFixture.githubActionsRun1
import metrik.project.TestFixture.githubActionsRun2
import metrik.project.TestFixture.mockEmitCb
import metrik.project.TestFixture.pipelineId
import metrik.project.domain.model.Status
import metrik.project.domain.repository.BuildRepository
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class PipelineRunServiceTest {

    @InjectMockKs
    private lateinit var pipelineRunService: PipelineRunService

    @MockK(relaxed = true)
    private lateinit var buildRepository: BuildRepository

    @MockK
    private lateinit var runService: RunService

    @MockK
    private lateinit var branchService: BranchService

    @AfterEach
    fun clear() {
        clearAllMocks()
    }
    @Test
    fun `should get valid new runs`() {
        every {
            runService.syncRunsByPage(any(), any(), any())
        } returns mutableListOf(
            githubActionsRun1,
            githubActionsRun2
        )
        every {
            branchService.retrieveBranches(any())
        } returns listOf(
            "master",
            "feature/CD pipeline"
        )
        val validNewRuns = pipelineRunService.getNewRuns(githubActionsPipeline, mockEmitCb)

        Assertions.assertThat(validNewRuns.size).isEqualTo(1)
        Assertions.assertThat(validNewRuns[0]).isEqualTo(githubActionsRun1)
    }

    @Test
    fun `should get in progress runs`() {
        val build = githubActionsExecution.copy(result = Status.IN_PROGRESS, stages = emptyList())
        every { buildRepository.getInProgressBuilds(pipelineId) } returns(listOf(build))
        every { runService.syncSingleRun(any(), any()) } returns(githubActionsRun2)

        val inProgressRuns = pipelineRunService.getInProgressRuns(githubActionsPipeline, mockEmitCb)

        Assertions.assertThat(inProgressRuns.size).isEqualTo(1)
        Assertions.assertThat(inProgressRuns).isEqualTo(listOf(githubActionsRun2))
        verify(exactly = 1) { mockEmitCb(any()) }
    }
}
