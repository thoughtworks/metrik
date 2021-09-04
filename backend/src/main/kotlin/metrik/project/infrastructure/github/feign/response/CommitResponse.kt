package metrik.project.infrastructure.github.feign.response

import metrik.project.domain.service.githubactions.GithubCommit
import java.time.ZonedDateTime

data class CommitResponse(
    val sha: String,
    val commit: CommitInfo
) {
    data class CommitInfo(
        val committer: Committer
    ) {
        data class Committer(
            val date: ZonedDateTime
        )
    }

    fun toGithubCommit(): GithubCommit =
        GithubCommit(id = sha, timestamp = commit.committer.date)
}
