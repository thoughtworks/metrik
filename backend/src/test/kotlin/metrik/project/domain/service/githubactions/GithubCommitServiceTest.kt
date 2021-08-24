package metrik.project.domain.service.githubactions

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.mockk.junit5.MockKExtension
import metrik.project.commit
import metrik.project.credential
import metrik.project.domain.model.Commit
import metrik.project.githubActionsPipeline
import metrik.project.infrastructure.github.GithubClient
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpServerErrorException
import java.time.ZonedDateTime

@ExtendWith(MockKExtension::class)
internal class GithubCommitServiceTest {

    @InjectMockKs
    private lateinit var githubActionsCommitService: GithubActionsCommitService

    @MockK(relaxed = true)
    private lateinit var githubClient: GithubClient

    @SpyK
    private var githubBuildConverter: GithubBuildConverter = GithubBuildConverter()

    @MockK(relaxed = true)
    private lateinit var githubUtil: GithubUtil

    @BeforeEach
    fun setUp() {
        every {
            githubUtil.getToken(githubActionsPipeline.credential)
        } returns "Bearer credential"

        every {
            githubUtil.getOwnerRepoFromUrl(githubActionsPipeline.url)
        } returns Pair("test_repo", "test_owner")
    }

    @Test
    fun `should retrieve commits between builds successfully`() {
        every {
            githubClient.retrieveCommits(
                any(),
                any(),
                any(),
                sinceTimeStamp,
                untilTimeStamp,
                null,
                any(),
                any()
            )
        } returns listOf(
            GithubCommit(
                "3986a82cf9f852e9938f7e7984d1e95742854baa",
                ZonedDateTime.parse("2021-08-11T01:46:31Z[UTC]")
            )
        )

        val commits = githubActionsCommitService.getCommitsBetweenBuilds(
            sinceTimeStampZonedFormat,
            untilTimeStampZonedFormat,
            pipeline = githubActionsPipeline
        )

        assertEquals(listOf(commit), commits)
    }

    @Test
    fun `should get all commits given since timestamp is not provided`() {
        every {
            githubClient.retrieveCommits(
                any(),
                any(),
                any(),
                null,
                untilTimeStamp,
                null,
                any(),
                any()
            )
        } returns listOf(
            GithubCommit(
                "3986a82cf9f852e9938f7e7984d1e95742854baa",
                ZonedDateTime.parse("2021-08-11T01:46:31Z[UTC]")
            )
        )

        val commits = githubActionsCommitService.getCommitsBetweenBuilds(
            untilTimeStamp = untilTimeStampZonedFormat,
            pipeline = githubActionsPipeline
        )

        assertEquals(listOf(commit), commits)
    }

    @Test
    fun `should get all commits in assigned branch`() {
        every {
            githubClient.retrieveCommits(
                any(),
                any(),
                any(),
                null,
                untilTimeStamp,
                "feature",
                any(),
                any()
            )
        } returns listOf(
            GithubCommit(
                "3986a82cf9f852e9938f7e7984d1e95742854baa",
                ZonedDateTime.parse("2021-08-11T01:46:31Z[UTC]")
            )
        )

        val commits = githubActionsCommitService.getCommitsBetweenBuilds(
            untilTimeStamp = untilTimeStampZonedFormat,
            branch = "feature",
            pipeline = githubActionsPipeline
        )

        assertEquals(listOf(commit), commits)
    }

    @Test
    fun `should stop calling next page api when the current api call throw not found exception`() {
        every {
            githubClient.retrieveCommits(
                any(),
                any(),
                any(),
                sinceTimeStamp,
                untilTimeStamp,
                null,
                any(),
                any()
            )
        } returns null

        val commits = githubActionsCommitService.getCommitsBetweenBuilds(
            sinceTimeStampZonedFormat,
            untilTimeStampZonedFormat,
            pipeline = githubActionsPipeline
        )

        assertEquals(emptyList<Commit>(), commits)
    }

    @Test
    fun `should throw exception when the server responds 500 at any time`() {
        every {
            githubClient.retrieveCommits(
                any(),
                any(),
                any(),
                sinceTimeStamp,
                untilTimeStamp,
                null,
                any(),
                any()
            )
        } throws HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR)

        assertThrows(HttpServerErrorException::class.java) {
            githubActionsCommitService.getCommitsBetweenBuilds(
                sinceTimeStampZonedFormat,
                untilTimeStampZonedFormat,
                pipeline = githubActionsPipeline
            )
        }
    }

    private companion object {
        const val sinceTimeStamp = "2021-08-10T01:46:31Z"
        const val untilTimeStamp = "2021-08-11T01:46:31Z"
        val sinceTimeStampZonedFormat: ZonedDateTime = ZonedDateTime.parse(sinceTimeStamp)!!
        val untilTimeStampZonedFormat: ZonedDateTime = ZonedDateTime.parse(untilTimeStamp)!!
    }
}
