package metrik.project.domain.service.githubactions

import java.time.ZonedDateTime

data class GithubActionsRun(
    val id: Int,
    val status: String,
    val conclusion: String,
    val url: String,
    val branch: String,
    val commitTimeStamp: ZonedDateTime,
    val timestamp: ZonedDateTime
)
