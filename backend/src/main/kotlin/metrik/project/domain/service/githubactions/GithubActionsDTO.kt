package metrik.project.domain.service.githubactions

import com.fasterxml.jackson.databind.PropertyNamingStrategy.SnakeCaseStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming
import metrik.infrastructure.utlils.toTimestamp
import metrik.project.domain.model.Build
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
    OTHER(null)
}

data class WorkflowRepository(
    val workflows: List<WorkFlowDetails>
)

data class WorkFlowDetails(
    val id: String,
    val name: String
)

@JsonNaming(SnakeCaseStrategy::class)
data class BuildSummaryDTO(
    val totalCount: Int = 0
)

@JsonNaming(SnakeCaseStrategy::class)
data class BuildDetailDTO(var workflowRuns: MutableList<WorkflowRuns>)

@JsonNaming(SnakeCaseStrategy::class)
data class WorkflowRuns(
    val id: String,
    val name: String,
    val runNumber: Int,
    val status: String,
    val conclusion: String,
    val url: String,
    val createdAt: ZonedDateTime?,
    val updatedAt: ZonedDateTime?,
    val jobs: MutableList<Job> = mutableListOf()
) {
    private var logger = LoggerFactory.getLogger(this.javaClass.name)

    private fun getBuildTimestamp(timestamp: ZonedDateTime?): Long {
        return timestamp!!.toTimestamp()
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

    fun convertToMetrikBuild(pipelineId: String): Build? {
        logger.info(
            "Github Actions converting: Started converting WorkflowRuns [$this] for pipeline [$pipelineId]"
        )

        try {
            val stages = jobs.mapNotNull { it.convertToMetrikBuildStage() }

            val startTimeMillis = getBuildTimestamp(createdAt)
            val completedTimeMillis = getBuildTimestamp(updatedAt)
            val durationMillis: Long = completedTimeMillis - startTimeMillis

            val build = Build(
                pipelineId,
                runNumber,
                getBuildExecutionStatus(),
                durationMillis,
                startTimeMillis,
                url,
                stages
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

@JsonNaming(SnakeCaseStrategy::class)
data class Job(
    val runId: String,
    val name: String,
    val status: String,
    val conclusion: String,
    val startedAt: ZonedDateTime?,
    val completedAt: ZonedDateTime?,
) {
    private var logger = LoggerFactory.getLogger(this.javaClass.name)

    private fun getStageTimestamp(timestamp: ZonedDateTime?): Long {
        return timestamp!!.toTimestamp()
    }

    private fun getStageExecutionStatus(): Status =
        when (conclusion) {
            GithubActionsConclusion.SUCCESS.value ->
                Status.SUCCESS
            GithubActionsConclusion.FAILURE.value ->
                Status.FAILED
            else -> Status.OTHER
        }

    fun convertToMetrikBuildStage(): Stage {
        logger.info("Github Actions converting: Started converting StageDTO [$this]")

        val startTimeMillis = getStageTimestamp(startedAt)
        val completedTimeMillis = getStageTimestamp(completedAt)
        val durationMillis: Long = completedTimeMillis - startTimeMillis

        val metrikStage = Stage(
            name,
            getStageExecutionStatus(),
            startTimeMillis,
            durationMillis,
            0,
            completedTimeMillis
        )

        logger.info("Github Actions converting: Stage converted result: [$metrikStage]")

        return metrikStage
    }
}
