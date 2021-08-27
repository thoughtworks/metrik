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
        logger.debug(
            "Github Actions converting: Started converting WorkflowRuns [$this] for pipeline [$pipelineId]"
        )

        val startTimeMillis = run.createdTimestamp.toTimestamp()
        val completedTimeMillis = run.updatedTimestamp.toTimestamp()
        val durationMillis: Long = completedTimeMillis - startTimeMillis

        val stage: List<Stage> =
            when (run.buildStatus) {
                Status.IN_PROGRESS, Status.OTHER -> emptyList()
                else -> listOf(
                    Stage(
                        run.name,
                        run.buildStatus,
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
            run.buildStatus,
            durationMillis,
            startTimeMillis,
            run.url,
            run.branch,
            stage,
            commits
        )

        logger.debug(
            "Github Actions converting: Build converted result: [$build]"
        )

        return build
    }

    fun convertToCommit(commit: GithubCommit, pipelineId: String): Commit {
        logger.debug(
            "Github Actions converting: Started converting CommitsDTO [$this] for pipeline [$pipelineId]"
        )

        val timestamp = commit.timestamp
        val commitBuild = Commit(commit.id, timestamp.toTimestamp(), timestamp.toString())

        logger.debug(
            "Github Actions converting: Commit converted result: [$commitBuild]"
        )

        return commitBuild
    }
}
