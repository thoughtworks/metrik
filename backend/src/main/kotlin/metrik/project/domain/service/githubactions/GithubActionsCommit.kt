package metrik.project.domain.service.githubactions

import java.time.ZonedDateTime

data class GithubActionsCommit(
    val id: String,
    val timestamp: ZonedDateTime
)