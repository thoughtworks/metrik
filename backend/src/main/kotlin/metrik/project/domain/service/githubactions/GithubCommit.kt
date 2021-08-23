package metrik.project.domain.service.githubactions

import java.time.ZonedDateTime

data class GithubCommit(
    val id: String,
    val timestamp: ZonedDateTime
)
