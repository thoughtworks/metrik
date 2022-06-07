package metrik.project.domain.service.githubactions

import metrik.project.domain.model.Status
import java.time.ZonedDateTime

enum class GithubActionsStatus(val value: String) {
    COMPLETED("completed"),
    QUEUED("queued"),
    IN_PROGRESS("in_progress")
}

enum class GithubActionsConclusion(val value: String?) {
    FAILURE("failure"),
    CANCELLED("cancelled"),
    SUCCESS("success"),
    ACTION_REQUIRED("action_required"),
    SKIPPED("skipped"),
    STALE("stale"),
    TIMED_OUT("timed_out"),
    NEUTRAL("neutral"),
    OTHER(null)
}

data class GithubActionsRun(
    val id: Long,
    val name: String,
    val status: String,
    val conclusion: String?,
    val url: String,
    val branch: String,
    val commitTimeStamp: ZonedDateTime,
    val createdTimestamp: ZonedDateTime,
    val updatedTimestamp: ZonedDateTime,
) {
    val buildStatus: Status
        get() = when {
            status == GithubActionsStatus.QUEUED.value || status == GithubActionsStatus.IN_PROGRESS.value ->
                Status.IN_PROGRESS
            conclusion == GithubActionsConclusion.SUCCESS.value ->
                Status.SUCCESS
            conclusion == GithubActionsConclusion.FAILURE.value ->
                Status.FAILED
            else -> Status.OTHER
        }
}
