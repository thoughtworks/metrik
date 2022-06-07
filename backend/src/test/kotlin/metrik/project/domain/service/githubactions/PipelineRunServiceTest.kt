package metrik.project.domain.service.githubactions

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import metrik.project.githubActionsPipeline
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

import java.time.ZonedDateTime

@ExtendWith(MockKExtension::class)
internal class PipelineRunServiceTest {

    @InjectMockKs
    private lateinit var pipelineRunService: PipelineRunService

    @MockK
    private lateinit var githubBranchService: GithubBranchService

    @MockK
    private lateinit var runService: RunService

    @Test
    fun `should get valid new runs`() {
        every {
            runService.syncRunsByPage(any(), any(), any())
        } returns mutableListOf(
            GithubActionsRun(
                id = 1111111111,
                name = "CI",
                branch = "master",
                status = "in_progress",
                conclusion = null,
                url = "http://localhost:80/test_project/test_repo/actions/runs/1111111111",
                commitTimeStamp = ZonedDateTime.parse("2021-08-11T01:46:31Z"),
                createdTimestamp = ZonedDateTime.parse("2021-08-11T11:11:01Z"),
                updatedTimestamp = ZonedDateTime.parse("2021-08-11T11:11:17Z")
            ),
            GithubActionsRun(
                id = 1111111112,
                name = "CI",
                branch = "feature/CI pipeline",
                status = "queued",
                conclusion = null,
                url = "http://localhost:80/test_project/test_repo/actions/runs/1111111111",
                commitTimeStamp = ZonedDateTime.parse("2021-08-11T01:46:31Z"),
                createdTimestamp = ZonedDateTime.parse("2021-08-11T11:11:01Z"),
                updatedTimestamp = ZonedDateTime.parse("2021-08-11T11:11:17Z")
            )
        )
        every {
            githubBranchService.retrieveBranches(any())
        } returns listOf(
            "master",
            "feature/CD pipeline"
        )

        val validNewRuns = pipelineRunService.getNewRuns(githubActionsPipeline)

        Assertions.assertThat(validNewRuns.size).isEqualTo(1)
        Assertions.assertThat(validNewRuns[0]).isEqualTo("master")


    }


    @Test
    fun `should get in progress runs`() {

    }
}