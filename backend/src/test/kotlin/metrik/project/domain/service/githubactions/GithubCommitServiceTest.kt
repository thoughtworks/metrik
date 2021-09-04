package metrik.project.domain.service.githubactions

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import metrik.infrastructure.utlils.toTimestamp
import metrik.project.domain.model.Commit
import metrik.project.domain.repository.CommitRepository
import metrik.project.githubActionsPipeline
import metrik.project.infrastructure.github.feign.GithubFeignClient
import metrik.project.infrastructure.github.feign.response.CommitResponse
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.ZonedDateTime

@ExtendWith(MockKExtension::class)
internal class GithubCommitServiceTest {

    @InjectMockKs
    private lateinit var githubCommitService: GithubCommitService

    @MockK(relaxed = true)
    private lateinit var githubFeignClient: GithubFeignClient

    @MockK(relaxed = true)
    private lateinit var commitRepository: CommitRepository

    @Test
    internal fun `should retrieve commit from GitHub given no records in DB already`() {
        every {
            commitRepository.findByTimePeriod(
                githubActionsPipeline.id,
                startTimeStamp,
                endTimeStamp
            )
        } returns emptyList()

        every {
            githubFeignClient.retrieveCommits(
                owner = any(),
                repo = any(),
                since = startTimeStamp.toString(),
                until = endTimeStamp.toString(),
                branch = any(),
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
            githubCommitService.getCommitsBetweenTimePeriod(startTimeStamp, endTimeStamp, null, githubActionsPipeline)

        assertThat(commitsBetweenTimePeriod.size).isEqualTo(1)
        assertThat(commitsBetweenTimePeriod[0].pipelineId).isEqualTo(githubActionsPipeline.id)
        assertThat(commitsBetweenTimePeriod[0].commitId).isEqualTo("test sha")
    }

    @Test
    internal fun `should return records in DB without calling GitHub API given commits in DB already`() {
        every { commitRepository.getTheLatestCommit(githubActionsPipeline.id) } returns Commit(timestamp = endTimeStamp + 1)
        every {
            commitRepository.findByTimePeriod(
                githubActionsPipeline.id,
                startTimeStamp,
                endTimeStamp
            )
        } returns listOf(Commit())

        val commitsBetweenTimePeriod =
            githubCommitService.getCommitsBetweenTimePeriod(startTimeStamp, endTimeStamp, null, githubActionsPipeline)

        verify(exactly = 0) {
            githubFeignClient.retrieveCommits(
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any()
            )
        }
        assertThat(commitsBetweenTimePeriod.size).isEqualTo(1)
    }

    @Test
    fun `should get all commits given since timestamp is not provided`() {
        every {
            commitRepository.findByTimePeriod(
                githubActionsPipeline.id,
                startTimeStamp,
                endTimeStamp
            )
        } returns emptyList()

        every {
            githubFeignClient.retrieveCommits(
                owner = any(), repo = any(), since = null, until = endTimeStamp.toString(),
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
            githubCommitService.getCommitsBetweenTimePeriod(0, endTimeStamp, null, githubActionsPipeline)

        assertThat(commitsBetweenTimePeriod.size).isEqualTo(1)
        assertThat(commitsBetweenTimePeriod[0].pipelineId).isEqualTo(githubActionsPipeline.id)
        assertThat(commitsBetweenTimePeriod[0].commitId).isEqualTo("test sha")
    }

    @Test
    fun `should get all commits in assigned branch`() {
        every {
            commitRepository.findByTimePeriod(
                githubActionsPipeline.id,
                startTimeStamp,
                endTimeStamp
            )
        } returns emptyList()

        every {
            githubFeignClient.retrieveCommits(
                owner = any(),
                repo = any(),
                since = any(),
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
            githubCommitService.getCommitsBetweenTimePeriod(
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
        val startTimeStamp = ZonedDateTime.parse(startTime)!!.toTimestamp()
        val endTimeStamp = ZonedDateTime.parse(endTime)!!.toTimestamp()
    }
}
