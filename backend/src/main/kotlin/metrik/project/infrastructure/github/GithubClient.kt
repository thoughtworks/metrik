package metrik.project.infrastructure.github

import metrik.project.domain.service.githubactions.GithubActionsRun
import metrik.project.domain.service.githubactions.GithubCommit
import metrik.project.infrastructure.github.feign.GithubFeignClient
import metrik.project.infrastructure.github.feign.mapper.GithubActionsRunMapper.Companion.MAPPER
import org.springframework.stereotype.Component

@Component
class GithubClient(
    private val githubFeignClient: GithubFeignClient
) {
    fun retrieveMultipleRuns(
        token: String,
        owner: String,
        repo: String,
        perPage: Int? = null,
        pageIndex: Int? = null
    ): List<GithubActionsRun> {
        val runs = githubFeignClient.retrieveMultipleRuns(token, owner, repo, perPage, pageIndex)
        return runs.workflowRuns.map { MAPPER.mapToGithubActionsRun(it) }
    }

    fun retrieveSingleRun(
        token: String,
        owner: String,
        repo: String,
        runId: String
    ): GithubActionsRun =
        MAPPER.mapToGithubActionsRun(githubFeignClient.retrieveSingleRun(token, owner, repo, runId))

    fun retrieveCommits(
        token: String,
        owner: String,
        repo: String,
        since: String? = null,
        until: String? = null,
        branch: String? = null,
        perPage: Int? = null,
        pageIndex: Int? = null
    ): List<GithubCommit> {
        val commits = githubFeignClient.retrieveCommits(token, owner, repo, since, until, branch, perPage, pageIndex)
        return commits.map { MAPPER.mapToGithubActionsCommit(it) }
    }
}
