package metrik.project.infrastructure.github

import metrik.project.domain.service.githubactions.GithubActionsRun
import metrik.project.domain.service.githubactions.GithubCommit
import metrik.project.infrastructure.github.feign.GithubFeignClient
import metrik.project.infrastructure.github.feign.mapper.GithubActionsRunMapper.Companion.MAPPER
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException

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
    ): List<GithubActionsRun>? {
        val runs = withApplicationException {
            githubFeignClient.retrieveMultipleRuns(token, owner, repo, perPage, pageIndex)
        }
        return runs?.let { run -> run.workflowRuns.map { MAPPER.mapToGithubActionsRun(it) } }

    }

    fun retrieveSingleRun(
        token: String,
        owner: String,
        repo: String,
        runId: String
    ): GithubActionsRun? =
        withApplicationException {
            MAPPER.mapToGithubActionsRun(githubFeignClient.retrieveSingleRun(token, owner, repo, runId))
        }

    fun retrieveCommits(
        token: String,
        owner: String,
        repo: String,
        since: String? = null,
        until: String? = null,
        branch: String? = null,
        perPage: Int? = null,
        pageIndex: Int? = null
    ): List<GithubCommit>? {
        val commits = withApplicationException {
            githubFeignClient.retrieveCommits(token, owner, repo, since, until, branch, perPage, pageIndex)
        }
        return commits?.map { MAPPER.mapToGithubActionsCommit(it) }
    }


    protected fun <T> withApplicationException(action: () -> T): T? =
        try {
            action()
        } catch (clientErrorException: HttpClientErrorException) {
            when (clientErrorException.statusCode) {
                HttpStatus.NOT_FOUND -> null
                else -> throw clientErrorException
            }
        }

}
