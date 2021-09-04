package metrik.project.domain.service.githubactions

import io.mockk.impl.annotations.InjectMockKs
import io.mockk.junit5.MockKExtension
import metrik.project.domain.model.Build
import metrik.project.domain.model.Commit
import metrik.project.domain.model.Stage
import metrik.project.domain.model.Status
import metrik.project.pipelineId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.ZonedDateTime

@ExtendWith(MockKExtension::class)
internal class GithubBuildConverterTest {

    @InjectMockKs
    private lateinit var githubBuildConverter: GithubBuildConverter

    @Test
    fun `should successfully convert run to build`() {
        val commits = listOf(
            commit
        )

        val build = Build(
            pipelineId = pipelineId,
            number = 123,
            result = Status.SUCCESS,
            duration = 0,
            timestamp = 1629203005000,
            url = "12345",
            branch = "master",
            stages = listOf(
                Stage(
                    "CI-Pipeline",
                    Status.SUCCESS,
                    1629203005000,
                    0,
                    0,
                    1629203005000
                )
            ),
            changeSets = commits
        )

        assertEquals(build, githubBuildConverter.convertToBuild(run, pipelineId, commits))
    }

    private companion object {
        val run = GithubActionsRun(
            id = 123,
            name = "CI-Pipeline",
            status = "completed",
            conclusion = "success",
            url = "12345",
            branch = "master",
            commitTimeStamp = ZonedDateTime.parse("2021-08-17T12:23:25Z"),
            createdTimestamp = ZonedDateTime.parse("2021-08-17T12:23:25Z"),
            updatedTimestamp = ZonedDateTime.parse("2021-08-17T12:23:25Z")
        )
        val commit = Commit(
            "12345",
            1629203005000,
            "2021-08-17T12:23:25Z",
            "",
        )
    }
}
