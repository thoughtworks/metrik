package metrik.project.infrastructure.github

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import metrik.project.domain.service.githubactions.GithubActionsRun
import metrik.project.domain.service.githubactions.GithubCommit
import metrik.project.infrastructure.github.feign.GithubFeignClient
import metrik.project.infrastructure.github.feign.response.CommitResponse
import metrik.project.infrastructure.github.feign.response.CommitResponse.CommitInfo
import metrik.project.infrastructure.github.feign.response.CommitResponse.CommitInfo.Committer
import metrik.project.infrastructure.github.feign.response.MultipleRunResponse
import metrik.project.infrastructure.github.feign.response.SingleRunResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.ZonedDateTime

@ExtendWith(MockKExtension::class)
class GithubClientTest {

    @InjectMockKs
    private lateinit var githubClient: GithubClient

    @MockK(relaxed = true)
    private lateinit var githubFeignClient: GithubFeignClient

    @Test
    fun `should return single run when retrieve single run info from github`() {
        every {
            githubFeignClient.retrieveSingleRun(token, owner, repo, runId)
        } returns SingleRunResponse(
            id = 123,
            name = "234",
            headBranch = "master",
            runNumber = 23456,
            status = "completed",
            conclusion = "SUCCESS",
            url = "12345",
            headCommit = SingleRunResponse.HeadCommit(
                id = "1234",
                timestamp = ZonedDateTime.parse("2021-08-17T12:23:25Z")
            ),
            createdAt = ZonedDateTime.parse("2021-08-17T12:23:25Z"),
            updatedAt = ZonedDateTime.parse("2021-08-17T12:23:25Z")
        )

        val run = githubClient.retrieveSingleRun(token, owner, repo, runId)

        assertEquals(
            run,
            GithubActionsRun(
                id = 123,
                status = "completed",
                conclusion = "SUCCESS",
                url = "12345",
                branch = "master",
                ZonedDateTime.parse("2021-08-17T12:23:25Z"),
                ZonedDateTime.parse("2021-08-17T12:23:25Z")
            )
        )
    }

    @Test
    fun `should return multiple runs when retrieve multiple runs from github`() {
        every {
            githubFeignClient.retrieveMultipleRuns(token, owner, repo, perPage, pageIndex)
        } returns MultipleRunResponse(
            listOf(
                SingleRunResponse(
                    id = 123,
                    name = "234",
                    headBranch = "master",
                    runNumber = 23456,
                    status = "completed",
                    conclusion = "SUCCESS",
                    url = "12345",
                    headCommit = SingleRunResponse.HeadCommit(
                        id = "1234",
                        timestamp = ZonedDateTime.parse("2021-08-17T12:23:25Z")
                    ),
                    createdAt = ZonedDateTime.parse("2021-08-17T12:23:25Z"),
                    updatedAt = ZonedDateTime.parse("2021-08-17T12:23:25Z")
                ),
                SingleRunResponse(
                    id = 456,
                    name = "456",
                    headBranch = "master",
                    runNumber = 23457,
                    status = "completed",
                    conclusion = "FAILURE",
                    url = "12345",
                    headCommit = SingleRunResponse.HeadCommit(
                        id = "1234",
                        timestamp = ZonedDateTime.parse("2021-08-17T12:23:25Z")
                    ),
                    createdAt = ZonedDateTime.parse("2021-08-17T12:23:25Z"),
                    updatedAt = ZonedDateTime.parse("2021-08-17T12:23:25Z")
                )
            )
        )

        val runs = githubClient.retrieveMultipleRuns(token, owner, repo, perPage, pageIndex)

        assertEquals(
            runs,
            listOf(
                GithubActionsRun(
                    id = 123,
                    status = "completed",
                    conclusion = "SUCCESS",
                    url = "12345",
                    branch = "master",
                    ZonedDateTime.parse("2021-08-17T12:23:25Z"),
                    ZonedDateTime.parse("2021-08-17T12:23:25Z")
                ),
                GithubActionsRun(
                    id = 456,
                    status = "completed",
                    conclusion = "FAILURE",
                    url = "12345",
                    branch = "master",
                    ZonedDateTime.parse("2021-08-17T12:23:25Z"),
                    ZonedDateTime.parse("2021-08-17T12:23:25Z")
                )
            )
        )
    }

    @Test
    fun `should return commits when retrieve commits from github`() {
        every {
            githubFeignClient.retrieveCommits(token, owner, repo, perPage = perPage, pageIndex = pageIndex)
        } returns listOf(
            CommitResponse(
                sha = "123",
                commit = CommitInfo(
                    committer = Committer(
                        date = ZonedDateTime.parse("2021-08-17T12:23:25Z")
                    )
                )
            ),
            CommitResponse(
                sha = "456",
                commit = CommitInfo(
                    committer = Committer(
                        date = ZonedDateTime.parse("2021-08-17T12:23:25Z")
                    )
                )
            )
        )

        val commits = githubClient.retrieveCommits(token, owner, repo, perPage = perPage, pageIndex = pageIndex)

        assertEquals(
            commits,
            listOf(
                GithubCommit(id = "123", timestamp = ZonedDateTime.parse("2021-08-17T12:23:25Z")),
                GithubCommit(id = "456", timestamp = ZonedDateTime.parse("2021-08-17T12:23:25Z"))
            )
        )
    }

    private companion object {
        const val token = "token"
        const val owner = "test_owner"
        const val repo = "test_repo"
        const val perPage = 100
        const val pageIndex = 1
        const val runId = "runID"
    }
}
