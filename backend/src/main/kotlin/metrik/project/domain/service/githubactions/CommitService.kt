package metrik.project.domain.service.githubactions

import metrik.infrastructure.utlils.toLocalDateTime
import metrik.infrastructure.utlils.toTimestamp
import metrik.project.domain.model.Commit
import metrik.project.domain.model.PipelineConfiguration
import metrik.project.infrastructure.github.feign.GithubFeignClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.net.URL

@Service
class CommitService(
    private val githubFeignClient: GithubFeignClient,
) {
    private var logger = LoggerFactory.getLogger(javaClass.name)
    private val defaultMaxPerPage = 100

    fun getCommitsBetweenTimePeriod(
        startTimeStamp: Long,
        endTimeStamp: Long,
        branch: String? = null,
        pipeline: PipelineConfiguration
    ): List<Commit> {
        logger.info("Started sync for Github Actions commits [${pipeline.url}]/[$branch]")

        var keepRetrieving = true
        var pageIndex = 1
        val allGithubCommits = mutableSetOf<GithubCommit>()
        while (keepRetrieving) {
            val commitsFromGithub =
                retrieveCommits(pipeline.credential, pipeline.url, startTimeStamp, endTimeStamp, branch, pageIndex)

            allGithubCommits.addAll(commitsFromGithub)

            keepRetrieving = commitsFromGithub.isNotEmpty()
            pageIndex++
        }

        return allGithubCommits.map {
            Commit(
                commitId = it.id,
                timestamp = it.timestamp.toTimestamp(),
                date = it.timestamp.toString(),
                pipelineId = pipeline.id
            )
        }
    }
    @Suppress("LongParameterList")
    private fun retrieveCommits(
        credential: String,
        url: String,
        startTimeStamp: Long,
        endTimeStamp: Long,
        branch: String? = null,
        pageIndex: Int? = null
    ): List<GithubCommit> {
        logger.info(
            "Get Github Commits - " +
                "Sending request to Github Feign Client with owner: $url, " +
                "since: ${startTimeStamp.toLocalDateTime()}, until: ${endTimeStamp.toLocalDateTime()}, " +
                "branch: $branch, pageIndex: $pageIndex"
        )
        val commits = with(githubFeignClient) {
            getOwnerRepoFromUrl(url).let { (owner, repo) ->
                retrieveCommits(
                    credential,
                    owner,
                    repo,
                    if (startTimeStamp == 0L) null else startTimeStamp.toString(),
                    endTimeStamp.toString(),
                    branch,
                    defaultMaxPerPage,
                    pageIndex
                )
            }
        }
        return commits?.map { it.toGithubCommit() } ?: listOf()
    }

    private fun getOwnerRepoFromUrl(url: String): Pair<String, String> {
        val components = URL(url).path.split("/")
        val owner = components[components.size - ownerIndex]
        val repo = components.last()
        return Pair(owner, repo)
    }
    private companion object {
        const val ownerIndex = 2
    }
}
