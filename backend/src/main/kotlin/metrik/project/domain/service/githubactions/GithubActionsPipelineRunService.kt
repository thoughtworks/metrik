package metrik.project.domain.service.githubactions;

import metrik.project.domain.model.PipelineConfiguration;
import metrik.project.domain.repository.BuildRepository
import org.springframework.stereotype.Service

@Service("githubActionsPipelineRunService")
class GithubActionsPipelineRunService(
    private val buildRepository:BuildRepository,
    private val githubRunService:GithubRunService
) {
    fun githubActionsRuns(pipeline:PipelineConfiguration): MutableList<GithubActionsRun> {
        val latestTimestamp = when (
                val latestBuild = buildRepository.getLatestBuild(pipeline.id)
        ) {
            null -> Long.MIN_VALUE
            else -> latestBuild.timestamp
        }
        return githubRunService.syncRunsByPage(pipeline, latestTimestamp)
    }
}
