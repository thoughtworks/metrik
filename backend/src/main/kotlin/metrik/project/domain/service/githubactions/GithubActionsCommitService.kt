package metrik.project.domain.service.githubactions

import metrik.project.domain.model.Commit
import metrik.project.domain.model.Pipeline
import metrik.project.infrastructure.github.GithubClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.ZonedDateTime

@Service
class GithubActionsCommitService(
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

        var ifRetrieving = true
        var pageIndex = 1

        val allCommits = mutableListOf<GithubCommit>()

        while (ifRetrieving) {

            logger.info(
                "Get Github Commits - " +
                    "Sending request to Github Feign Client with owner: ${pipeline.url}, " +
                    "since: $sinceTimeStamp, until: $untilTimeStamp, branch: $branch, pageIndex: $pageIndex"
            )

            val commits = githubClient.retrieveCommits(
                pipeline.url,
                pipeline.credential,
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

    private companion object {
        const val defaultMaxPerPage = 100
    }
}
