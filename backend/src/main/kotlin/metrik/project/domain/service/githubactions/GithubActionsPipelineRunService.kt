package metrik.project.domain.service.githubactions;

import metrik.project.domain.model.PipelineConfiguration;
import metrik.project.domain.repository.BuildRepository
import org.springframework.stereotype.Service

@Service("githubActionsPipelineRunService")
class GithubActionsPipelineRunService(
    private val buildRepository:BuildRepository,
    private val githubRunService:GithubRunService,
    private val githubBranchService: GithubBranchService
) {
    fun getNewRuns(pipeline:PipelineConfiguration): MutableList<GithubActionsRun> {
        val latestTimestamp = getLatestBuildTimeStamp(pipeline)
        var newRuns = githubRunService.syncRunsByPage(pipeline, latestTimestamp)
        val branches = githubBranchService.retrieveBranches(pipeline)
        return newRuns.filter { branches.contains(it.branch) } as MutableList<GithubActionsRun>
    }

    fun getProgressRuns(
        pipeline: PipelineConfiguration,
        newRuns: MutableList<GithubActionsRun>
    ) {
        val inProgressRuns = buildRepository.getInProgressBuilds(pipeline.id)
            .parallelStream()
            .map { githubRunService.syncSingleRun(pipeline, it.url) }
            .toList()
            .filterNotNull()

        newRuns.addAll(inProgressRuns)
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
