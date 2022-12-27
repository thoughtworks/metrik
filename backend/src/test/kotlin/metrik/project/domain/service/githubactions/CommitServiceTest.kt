package metrik.project.domain.service.githubactions

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import metrik.project.TestFixture.githubActionsPipeline
import metrik.project.infrastructure.github.feign.GithubFeignClient
import metrik.project.infrastructure.github.feign.response.CommitResponse
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.ZonedDateTime
import java.util.*

@ExtendWith(MockKExtension::class)
internal class CommitServiceTest {

    @InjectMockKs
    private lateinit var commitService: CommitService

    @MockK(relaxed = true)
    private lateinit var githubFeignClient: GithubFeignClient

    @Test
    fun `should get all commits given since timestamp is not provided`() {
        every {
            githubFeignClient.retrieveCommits(
                credential = any(), owner = any(), repo = any(), since = null, until = endTimeStamp.toString(),
                branch = any(), perPage = any(), pageIndex = 1
            )
        } returns listOf(
            CommitResponse(
                "test sha",
                CommitResponse.CommitInfo(
                    CommitResponse.CommitInfo.Committer(
                        ZonedDateTime.now()
                    )
                )
            )
        )

        val commitsBetweenTimePeriod =
            commitService.getCommitsBetweenTimePeriod(null, endTimeStamp, null, githubActionsPipeline)

        assertThat(commitsBetweenTimePeriod.size).isEqualTo(1)
        assertThat(commitsBetweenTimePeriod[0].pipelineId).isEqualTo(githubActionsPipeline.id)
        assertThat(commitsBetweenTimePeriod[0].commitId).isEqualTo("test sha")
    }

    @Test
    fun `should get all commits in assigned branch`() {
        every {
            githubFeignClient.retrieveCommits(
                credential = any(),
                owner = any(),
                repo = any(),
                since = startTimeStamp.toString(),
                until = endTimeStamp.toString(),
                branch = "master",
                perPage = any(),
                pageIndex = 1
            )
        } returns listOf(
            CommitResponse(
                "test sha",
                CommitResponse.CommitInfo(
                    CommitResponse.CommitInfo.Committer(
                        ZonedDateTime.now()
                    )
                )
            )
        )

        val commitsBetweenTimePeriod =
            commitService.getCommitsBetweenTimePeriod(
                startTimeStamp,
                endTimeStamp,
                "master",
                githubActionsPipeline
            )

        assertThat(commitsBetweenTimePeriod.size).isEqualTo(1)
        assertThat(commitsBetweenTimePeriod[0].pipelineId).isEqualTo(githubActionsPipeline.id)
        assertThat(commitsBetweenTimePeriod[0].commitId).isEqualTo("test sha")
    }

    private companion object {
        const val startTime = "2021-08-10T01:46:31Z"
        const val endTime = "2021-08-11T01:46:31Z"
        val startTimeStamp = ZonedDateTime.parse(startTime)!!
        val endTimeStamp = ZonedDateTime.parse(endTime)!!
    }
}
