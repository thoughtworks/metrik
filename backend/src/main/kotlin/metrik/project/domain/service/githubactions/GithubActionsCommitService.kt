package metrik.project.domain.service.githubactions

import metrik.infrastructure.utlils.RequestUtil
import metrik.project.domain.model.Commit
import metrik.project.domain.model.Pipeline
import metrik.project.infrastructure.github.GithubClient
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import org.springframework.web.util.UriComponentsBuilder
import java.net.URL
import java.time.ZonedDateTime

@Service
class GithubActionsCommitService(
    private val githubUtil: GithubUtil,
    private val githubClient: GithubClient,
    private val githubBuildConverter: GithubBuildConverter,
) {
    private var logger = LoggerFactory.getLogger(javaClass.name)

    fun getCommitsBetweenBuilds(
        sinceTimeStamp: ZonedDateTime? = null,
        untilTimeStamp: ZonedDateTime,
        branch: String? = null,
        pipeline: Pipeline
    ): List<Commit> {
        logger.info("Started sync for Github Actions commits [$pipeline]")

        val token = githubUtil.getToken(pipeline.credential)
        val (owner, repo) = githubUtil.getOwnerRepoFromUrl(pipeline.url)

        var ifRetrieving = true
        var pageIndex = 1

        val allCommits = mutableListOf<GithubCommit>()

        while (ifRetrieving) {
//            val baseUrl = "${pipeline.url}$commitSuffix"
//
//            val urlBuilder = UriComponentsBuilder.fromHttpUrl(baseUrl)
//
//            sinceTimeStamp?.also { urlBuilder.queryParam("since", it.toString()) }
//
//            urlBuilder.queryParam("until", untilTimeStamp.toString())
//
//            urlBuilder.queryParam("per_page", defaultMaxPerPage)
//
//            branch?.also { urlBuilder.queryParam("sha", branch) }
//
//            urlBuilder.queryParam("page", pageIndex)
//
//            val url = urlBuilder.toUriString()
//
//            logger.info("Github Actions commits - Sending request to [$url] with entity [$entity]")

//                    val responseEntity =
//                        restTemplate.exchange<MutableList<CommitSummaryDTO>>(url, HttpMethod.GET, entity)
//                    logger.info("Github Actions commits - Response from [$url]: $responseEntity")

            val commits = githubClient.retrieveCommits(
                token,
                owner,
                repo,
                sinceTimeStamp?.toString(),
                untilTimeStamp.toString(),
                branch,
                defaultMaxPerPage,
                pageIndex
            ) ?: break

            ifRetrieving = commits.size == defaultMaxPerPage

            allCommits.addAll(commits)

            pageIndex++
        }

        return allCommits.map { githubBuildConverter.convertToCommit(it, pipeline.id) }
    }

    fun <T> withApplicationException(action: () -> T, url: String): T? {
        try {
            return action()
        } catch (clientErrorException: HttpClientErrorException) {
            if (clientErrorException.statusCode == HttpStatus.NOT_FOUND) {
                logger.info(
                    """
                    Getting NOT_FOUND error found.
                    Exception: [$clientErrorException]
                    """.trimIndent()
                )
                return null
            } else {
                logger.error(
                    "Getting Github Commits details failed, URL: [$url], exception: [$clientErrorException]"
                )
                throw clientErrorException
            }
        }
    }


    private companion object {
        const val defaultMaxPerPage = 100
    }
}
