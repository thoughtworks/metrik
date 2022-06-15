package metrik.project.domain.service.githubactions

import metrik.infrastructure.utlils.toTimestamp
import metrik.project.constant.GithubActionConstants
import metrik.project.domain.model.PipelineConfiguration
import metrik.project.infrastructure.github.feign.GithubFeignClient
import metrik.project.rest.vo.response.SyncProgress
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.net.URL
import kotlin.math.ceil

@Service
class RunService(
    private val githubFeignClient: GithubFeignClient,
) {
    private var logger = LoggerFactory.getLogger(javaClass.name)

    fun syncRunsByPage(
        pipeline: PipelineConfiguration,
        latestTimestamp: Long,
        emitCb: (SyncProgress) -> Unit,
        maxPerPage: Int = defaultMaxPerPage
    ): MutableList<GithubActionsRun> {
        val (owner, repo) = getOwnerRepoFromUrl(pipeline.url)

        val syncedRuns = mutableListOf<GithubActionsRun>()
        var keepRetrieving = true
        var pageIndex = 1
        var totalNumberOfRuns = 0

        while (keepRetrieving) {
            logger.info(
                "Get Github Runs - " +
                    "Sending request to Github Feign Client with url: ${pipeline.url}, pageIndex: $pageIndex"
            )
            val syncedRunsResponse = githubFeignClient.retrieveMultipleRuns(
                pipeline.credential,
                owner,
                repo,
                maxPerPage,
                pageIndex
            )

            if (pageIndex == 1) {
                totalNumberOfRuns = syncedRunsResponse?.totalCount ?: 0
            }

            val totalPages = ceil(totalNumberOfRuns.toDouble() / maxPerPage.toDouble()).toInt()
            val syncedRunsFromCurrentPage =
                syncedRunsResponse?.let { response -> response.workflowRuns.map { it.toGithubActionsRun() } }
                    ?.filter { it.createdTimestamp.toTimestamp() > latestTimestamp } ?: listOf()

            keepRetrieving = syncedRunsFromCurrentPage.size == maxPerPage

            syncedRuns.addAll(syncedRunsFromCurrentPage)

            emitCb(
                SyncProgress(
                    pipeline.id,
                    pipeline.name,
                    pageIndex,
                    totalPages,
                    GithubActionConstants.stepNumberOfFetchingNewRuns,
                    GithubActionConstants.totalNumberOfSteps
                )
            )

            pageIndex++
        }
        return syncedRuns
    }

    fun syncSingleRun(
        pipeline: PipelineConfiguration,
        runUrl: String
    ): GithubActionsRun? {
        val runId = URL(runUrl).path.split("/").last()
        val (owner, repo) = getOwnerRepoFromUrl(pipeline.url)

        logger.info(
            "Get Github Runs - " +
                "Sending request to Github Feign Client with owner: ${pipeline.url}, runId: $runId"
        )

        return githubFeignClient.retrieveSingleRun(pipeline.credential, owner, repo, runId)?.toGithubActionsRun()
    }

    private fun getOwnerRepoFromUrl(url: String): Pair<String, String> {
        val components = URL(url).path.split("/")
        val owner = components[components.size - ownerIndex]
        val repo = components.last()
        return Pair(owner, repo)
    }

    private fun getToken(token: String) = "$tokenPrefix $token"

    private companion object {
        const val ownerIndex = 2
        const val tokenPrefix = "Bearer"
        const val defaultMaxPerPage = 100
    }
}
