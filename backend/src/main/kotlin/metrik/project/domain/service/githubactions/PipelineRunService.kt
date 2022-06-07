package metrik.project.domain.service.githubactions

import metrik.project.domain.model.PipelineConfiguration
import metrik.project.domain.repository.BuildRepository
import org.springframework.stereotype.Service

@Service("githubActionsPipelineRunService")
class PipelineRunService(
    private val buildRepository:BuildRepository,
    private val runService:RunService,
    private val branchService: BranchService
) {
    fun getNewRuns(pipeline:PipelineConfiguration): MutableList<GithubActionsRun> {
        val latestTimestamp = getLatestBuildTimeStamp(pipeline)
        var newRuns = runService.syncRunsByPage(pipeline, latestTimestamp)
        val branches = branchService.retrieveBranches(pipeline)
        return newRuns.filter { branches.contains(it.branch) } as MutableList<GithubActionsRun>
    }

    fun getInProgressRuns(
        pipeline: PipelineConfiguration
    ): List<GithubActionsRun> {
        return buildRepository.getInProgressBuilds(pipeline.id)
            .parallelStream()
            .map { runService.syncSingleRun(pipeline, it.url) }
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
