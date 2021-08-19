package metrik.project.domain.service.githubactions

import metrik.infrastructure.utlils.RequestUtil
import metrik.project.domain.model.Commit
import metrik.project.domain.model.Pipeline
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import org.springframework.web.util.UriComponentsBuilder
import java.time.ZonedDateTime

@Service
class GithubActionsCommitService(
    private var restTemplate: RestTemplate
) {
    private var logger = LoggerFactory.getLogger(javaClass.name)

    fun getCommitsBetweenBuilds(
        sinceTimeStamp: ZonedDateTime? = null,
        untilTimeStamp: ZonedDateTime,
        branch: String? = null,
        pipeline: Pipeline
    ): List<Commit> {
        logger.info("Started sync for Github Actions commits [$pipeline]")
        val headers = RequestUtil.buildHeaders(
            mapOf(
                Pair("Authorization", "Bearer ${pipeline.credential}"),
                Pair("Connection", "close")
            )
        )
        val entity = HttpEntity<String>(headers)

        var ifRetrieving = true
        var pageIndex = 1

        val totalResponseBody = mutableListOf<CommitSummaryDTO>()

        while (ifRetrieving) {
            val baseUrl = "${pipeline.url}$commitSuffix"

            val urlBuilder = UriComponentsBuilder.fromHttpUrl(baseUrl)

            sinceTimeStamp?.also { urlBuilder.queryParam("since", it.toString()) }

            urlBuilder.queryParam("until", untilTimeStamp.toString())

            urlBuilder.queryParam("per_page", defaultMaxPerPage)

            branch?.also { urlBuilder.queryParam("sha", branch) }

            urlBuilder.queryParam("page", pageIndex)

            val url = urlBuilder.toUriString()

            logger.info("Github Actions commits - Sending request to [$url] with entity [$entity]")

            withApplicationException(
                {
                    val responseEntity =
                        restTemplate.exchange<MutableList<CommitSummaryDTO>>(url, HttpMethod.GET, entity)
                    logger.info("Github Actions commits - Response from [$url]: $responseEntity")

                    val commits = responseEntity.body!!

                    when {
                        commits.isEmpty() -> ifRetrieving = false
                        else -> totalResponseBody.addAll(commits)
                    }
                    true
                },
                url
            ) ?: break

            pageIndex++
        }

        return totalResponseBody.map { it.convertToMetrikCommit(pipeline.id) }
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
        const val commitSuffix = "/commits"
        const val defaultMaxPerPage = 100
    }
}
