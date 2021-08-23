package metrik.project.infrastructure.github

import metrik.project.domain.service.githubactions.GithubActionsCommit
import metrik.project.domain.service.githubactions.GithubActionsRun
import metrik.project.infrastructure.github.feign.GithubFeignClient
import org.springframework.stereotype.Component


@Component
class GithubCoreClient(
    private val githubFeignClient: GithubFeignClient
) {
    fun retrieveMultipleRuns(
        token: String,
        owner: String,
        repo: String,
        perPage: Int?,
        pageIndex: Int?
    ): List<GithubActionsRun> {
        TODO()
    }

    fun retrieveSingleRun(
        token: String,
        owner: String,
        repo: String,
        runId: String
    ): GithubActionsRun {
        TODO()
    }

    fun retrieveCommits(
        token: String,
        owner: String,
        repo: String,
        since: String?,
        until: String?,
        branch: String?,
        perPage: String?,
        pageIndex: String?
    ): GithubActionsCommit {
        TODO()
    }


}