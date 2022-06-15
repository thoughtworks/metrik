package metrik.project.domain.service.githubactions

import metrik.infrastructure.utlils.toTimestamp
import metrik.project.domain.model.Commit
import metrik.project.domain.model.Execution
import metrik.project.domain.model.Stage
import metrik.project.domain.model.Status
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class ExecutionConverter {
    private var logger = LoggerFactory.getLogger(javaClass.name)

    fun convertToBuild(run: GithubActionsRun, pipelineId: String, commits: List<Commit>): Execution {
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

        val execution = Execution(
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
            "Github Actions converting: Build converted result: [$execution]"
        )

        return execution
    }
}
