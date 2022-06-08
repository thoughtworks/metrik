package metrik.project.domain.service.githubactions

import metrik.project.constant.GithubActionConstants
import metrik.project.domain.model.PipelineConfiguration
import metrik.project.domain.repository.BuildRepository
import metrik.project.rest.vo.response.SyncProgress
import org.springframework.stereotype.Service
import java.util.concurrent.atomic.AtomicInteger
import kotlin.streams.toList

@Service("githubActionsPipelineRunService")
class PipelineRunService(
    private val buildRepository:BuildRepository,
    private val runService:RunService,
    private val branchService: BranchService
) {
    fun getNewRuns(pipeline:PipelineConfiguration, emitCb: (SyncProgress) -> Unit): MutableList<GithubActionsRun> {
        val latestTimestamp = getLatestBuildTimeStamp(pipeline)
        val newRuns = runService.syncRunsByPage(pipeline, latestTimestamp, emitCb)
        val branches = branchService.retrieveBranches(pipeline)
        return newRuns.filter { branches.contains(it.branch) } as MutableList<GithubActionsRun>
    }

    fun getInProgressRuns(
        pipeline: PipelineConfiguration,
        emitCb: (SyncProgress) -> Unit
    ): List<GithubActionsRun> {
        val inProgressRuns = buildRepository.getInProgressBuilds(pipeline.id)
        val numberOfInProgressRuns = inProgressRuns.size
        val progressCounter = AtomicInteger(0)

        return inProgressRuns
            .parallelStream()
            .map { it ->
                emitCb(
                    SyncProgress(
                        pipeline.id,
                        pipeline.name,
                        progressCounter.incrementAndGet(),
                        numberOfInProgressRuns,
                        GithubActionConstants.stepNumberOfFetchingInProgressRuns,
                        GithubActionConstants.totalNumberOfSteps
                    )
                )
                runService.syncSingleRun(pipeline, it.url)
            }
            .toList()
            .filterNotNull()
    }

    private fun getLatestBuildTimeStamp(pipeline: PipelineConfiguration): Long {
        val latestTimestamp = when (
            val latestBuild = buildRepository.getLatestBuild(pipeline.id)
        ) {
            null -> Long.MIN_VALUE
            else -> latestBuild.timestamp
        }
        return latestTimestamp
    }
}
