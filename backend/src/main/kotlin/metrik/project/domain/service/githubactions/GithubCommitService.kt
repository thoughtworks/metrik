package metrik.project.domain.service.githubactions

import metrik.infrastructure.utlils.toLocalDateTime
import metrik.infrastructure.utlils.toTimestamp
import metrik.project.domain.model.Commit
import metrik.project.domain.model.Pipeline
import metrik.project.domain.repository.CommitRepository
import metrik.project.infrastructure.github.feign.GithubFeignClient
import metrik.project.infrastructure.github.feign.mapper.GithubActionsRunMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.net.URL

@Service
class GithubCommitService(
    private val githubFeignClient: GithubFeignClient,
    private val commitRepository: CommitRepository
) {
    private var logger = LoggerFactory.getLogger(javaClass.name)
    private val tokenPrefix = "Bearer"
    private val defaultMaxPerPage = 100

    fun getCommitsBetweenTimePeriod(
        startTimeStamp: Long,
        endTimeStamp: Long,
        branch: String? = null,
        pipeline: Pipeline
    ): List<Commit> {
        logger.info("Started sync for Github Actions commits [${pipeline.url}]/[${branch}]")

        if (recordsExistInDb(pipeline, startTimeStamp, endTimeStamp)) {
            return commitRepository.findByTimePeriod(
                pipeline.id,
                startTimeStamp,
                endTimeStamp
            )
        }

        logger.info(
            "No records exist in DB, send API request, " +
                    "time period: [${startTimeStamp.toLocalDateTime()}] to [${endTimeStamp.toLocalDateTime()}]"
        )

        var keepRetrieving = true
        var pageIndex = 1
        val allCommits = mutableSetOf<GithubCommit>()

        while (keepRetrieving) {
            val commitsFromGithub =
                retrieveCommits(pipeline.url, pipeline.credential, startTimeStamp, endTimeStamp, branch, pageIndex)

            allCommits.addAll(commitsFromGithub)

            val hasDuplicationInDB = commitRepository.hasDuplication(commitsFromGithub)
            if (hasDuplicationInDB) {
                logger.info("Found duplication in DB, breaking out, current index: $pageIndex")
            }

            keepRetrieving = commitsFromGithub.isNotEmpty() && !hasDuplicationInDB
            pageIndex++
        }

        commitRepository.save(pipeline.id, allCommits.toList().toMutableList())

        return allCommits.map {
            Commit(
                commitId = it.id,
                timestamp = it.timestamp.toTimestamp(),
                date = it.timestamp.toString(),
                pipelineId = pipeline.id
            )
        }
    }

    private fun recordsExistInDb(
        pipeline: Pipeline,
        startTimeStamp: Long,
        endTimeStamp: Long
    ): Boolean {
        val theLatestCommitInDB = commitRepository.getTheLatestCommit(pipeline.id)
        if (theLatestCommitInDB != null && theLatestCommitInDB.timestamp > endTimeStamp) {
            logger.info("Commit records exist in DB, skip API request, " +
                    "time period: [${startTimeStamp.toLocalDateTime()}] to [${endTimeStamp.toLocalDateTime()}]")
            return true
        }
        return false
    }

    private fun retrieveCommits(
        url: String,
        token: String,
        startTimeStamp: Long,
        endTimeStamp: Long,
        branch: String? = null,
        pageIndex: Int? = null
    ): List<GithubCommit> {
        logger.info(
            "Get Github Commits - " +
                    "Sending request to Github Feign Client with owner: ${url}, " +
                    "since: ${startTimeStamp.toLocalDateTime()}, until: ${endTimeStamp.toLocalDateTime()}, " +
                    "branch: $branch, pageIndex: $pageIndex"
        )
        val commits = with(githubFeignClient) {
            getOwnerRepoFromUrl(url).let { (owner, repo) ->
                retrieveCommits(
                    buildToken(token),
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
        return commits.map { GithubActionsRunMapper.MAPPER.mapToGithubActionsCommit(it) }
    }

    private fun getOwnerRepoFromUrl(url: String): Pair<String, String> {
        val ownerIndex = 2
        val components = URL(url).path.split("/")
        val owner = components[components.size - ownerIndex]
        val repo = components.last()
        return Pair(owner, repo)
    }

    private fun buildToken(token: String) = "$tokenPrefix $token"
}
