package metrik.project.domain.service.githubactions

import metrik.infrastructure.utlils.toTimestamp
import metrik.project.domain.model.Build
import metrik.project.domain.model.Commit
import metrik.project.domain.model.Stage
import metrik.project.domain.model.Status
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class GithubBuildConverter {
    private var logger = LoggerFactory.getLogger(javaClass.name)

    fun convertToBuild(run: GithubActionsRun, pipelineId: String, commits: List<Commit>): Build {
        logger.info(
            "Github Actions converting: Started converting WorkflowRuns [$this] for pipeline [$pipelineId]"
        )

        try {

            val status = run.getBuildExecutionStatus()

            val startTimeMillis = run.createdTimestamp.toTimestamp()
            val completedTimeMillis = run.updatedTimestamp.toTimestamp()
            val durationMillis: Long = completedTimeMillis - startTimeMillis

            val stage: List<Stage> =
                when (status) {
                    Status.IN_PROGRESS, Status.OTHER -> emptyList()
                    else -> listOf(
                        Stage(
                            run.name,
                            status,
                            startTimeMillis,
                            durationMillis,
                            0,
                            completedTimeMillis
                        )
                    )
                }

            val build = Build(
                pipelineId,
                run.id,
                status,
                durationMillis,
                startTimeMillis,
                run.url,
                run.branch,
                stage,
                commits
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

    fun convertToCommit(commit: GithubCommit, pipelineId: String): Commit {
        logger.info(
            "Github Actions converting: Started converting CommitsDTO [$this] for pipeline [$pipelineId]"
        )

        try {
            val timestamp = commit.timestamp
            val commitBuild = Commit(commit.id, timestamp.toTimestamp(), timestamp.toString())

            logger.info(
                "Github Actions converting: Commit converted result: [$commitBuild]"
            )

            return commitBuild
        } catch (e: RuntimeException) {
            logger.error("Converting Github Actions DTO failed, DTO: [$this], exception: [$e]")
            throw e
        }
    }

}