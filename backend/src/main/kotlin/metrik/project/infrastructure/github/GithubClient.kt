package metrik.project.infrastructure.github

import metrik.project.domain.service.githubactions.GithubActionsRun
import metrik.project.domain.service.githubactions.GithubCommit
import metrik.project.infrastructure.github.feign.GithubFeignClient
import metrik.project.infrastructure.github.feign.mapper.GithubActionsRunMapper.Companion.MAPPER
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import java.net.URL

@Component
class GithubClient(
    private val githubFeignClient: GithubFeignClient
) {
    fun verifyGithubUrl(
        url: String,
        token: String,
    ) {
        getOwnerRepoFromUrl(url).also { (owner, repo) ->
            githubFeignClient.retrieveMultipleRuns(getToken(token), owner, repo)
        }
    }

    fun retrieveMultipleRuns(
        url: String,
        token: String,
        perPage: Int? = null,
        pageIndex: Int? = null
    ): List<GithubActionsRun>? {
        val runs = getOwnerRepoFromUrl(url).let { (owner, repo) ->
            withApplicationException {
                githubFeignClient.retrieveMultipleRuns(getToken(token), owner, repo, perPage, pageIndex)
            }
        }
        return runs?.let { run -> run.workflowRuns.map { MAPPER.mapToGithubActionsRun(it) } }
    }

    fun retrieveSingleRun(
        url: String,
        token: String,
        runId: String
    ): GithubActionsRun? =
        getOwnerRepoFromUrl(url).let { (owner, repo) ->
            withApplicationException {
                MAPPER.mapToGithubActionsRun(githubFeignClient.retrieveSingleRun(getToken(token), owner, repo, runId))
            }
        }

    fun retrieveCommits(
        url: String,
        token: String,
        since: String? = null,
        until: String? = null,
        branch: String? = null,
        perPage: Int? = null,
        pageIndex: Int? = null
    ): List<GithubCommit>? {
        val commits = getOwnerRepoFromUrl(url).let { (owner, repo) ->
            withApplicationException {
                githubFeignClient.retrieveCommits(
                    getToken(token), owner, repo, since, until, branch, perPage, pageIndex
                )
            }
        }
        return commits?.map { MAPPER.mapToGithubActionsCommit(it) }
    }

    private fun getOwnerRepoFromUrl(url: String): Pair<String, String> {
        val components = URL(url).path.split("/")
        val owner = components[components.size - ownerIndex]
        val repo = components.last()
        return Pair(owner, repo)
    }

    private fun getToken(token: String) = "$tokenPrefix $token"

    private fun <T> withApplicationException(action: () -> T): T? =
        try {
            action()
        } catch (clientErrorException: HttpClientErrorException) {
            when (clientErrorException.statusCode) {
                HttpStatus.NOT_FOUND -> null
                else -> throw clientErrorException
            }
        }

    private companion object {
        const val ownerIndex = 2
        const val tokenPrefix = "Bearer"
    }
}
