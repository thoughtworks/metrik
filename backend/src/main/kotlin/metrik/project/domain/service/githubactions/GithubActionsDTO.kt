package metrik.project.domain.service.githubactions

import com.fasterxml.jackson.databind.PropertyNamingStrategy.SnakeCaseStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming
import metrik.infrastructure.utlils.toTimestamp
import metrik.project.domain.model.Build
import metrik.project.domain.model.Commit
import metrik.project.domain.model.Stage
import metrik.project.domain.model.Status
import org.slf4j.LoggerFactory
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
    OTHER(null)
}

@JsonNaming(SnakeCaseStrategy::class)
data class BuildSummaryDTO(
    val totalCount: Int = 0
)

@JsonNaming(SnakeCaseStrategy::class)
data class BuildDetailDTO(var workflowRuns: MutableList<WorkflowRuns>)

@JsonNaming(SnakeCaseStrategy::class)
data class WorkflowRuns(
    val id: Int,
    val name: String,
    val runNumber: Int,
    val status: String,
    val conclusion: String,
    val url: String,
    val headCommit: HeadCommit,
    val createdAt: ZonedDateTime,
    val updatedAt: ZonedDateTime,
) {
    private var logger = LoggerFactory.getLogger(this.javaClass.name)

    fun getBuildTimestamp(timestamp: ZonedDateTime): Long {
        return timestamp.toTimestamp()
    }

    private fun getBuildExecutionStatus(): Status =
        when {
            status == GithubActionsStatus.QUEUED.value || status == GithubActionsStatus.IN_PROGRESS.value ->
                Status.IN_PROGRESS
            conclusion == GithubActionsConclusion.SUCCESS.value ->
                Status.SUCCESS
            conclusion == GithubActionsConclusion.FAILURE.value ->
                Status.FAILED
            else -> Status.OTHER
        }

    fun convertToMetrikBuild(pipelineId: String): Build {
        logger.info(
            "Github Actions converting: Started converting WorkflowRuns [$this] for pipeline [$pipelineId]"
        )

        try {

            val status = getBuildExecutionStatus()

            val startTimeMillis = getBuildTimestamp(createdAt)
            val completedTimeMillis = getBuildTimestamp(updatedAt)
            val durationMillis: Long = completedTimeMillis - startTimeMillis

            val stage: Stage =
                when (status) {
                    Status.IN_PROGRESS -> Stage()
                    else -> Stage(
                        name,
                        status,
                        startTimeMillis,
                        durationMillis,
                        0,
                        completedTimeMillis
                    )
                }

            val build = Build(
                pipelineId,
                id,
                status,
                durationMillis,
                startTimeMillis,
                url,
                listOf(stage),
                listOf(headCommit.convertToMetrikCommit())
            )

            logger.info(
                "Github Actions converting: Build converted result: [$build]"
            )

            return build
        } catch (e: RuntimeException) {
            logger.error("Converting Github Actions DTO failed, DTO: [$this], exception: [$e]")
            throw e
        }
    }
}

data class HeadCommit(
    val id: String,
    val message: String,
    val timestamp: ZonedDateTime
) {

    fun convertToMetrikCommit(): Commit =
        Commit(
            id,
            timestamp.toTimestamp(),
            timestamp.toString(),
            message
        )
}
