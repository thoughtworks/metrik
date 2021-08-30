package metrik.project.domain.service.githubactions

import metrik.infrastructure.utlils.toTimestamp
import metrik.project.domain.model.Commit
import metrik.project.domain.model.Pipeline
import metrik.project.domain.repository.CommitRepository
import metrik.project.infrastructure.github.GithubClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.ZonedDateTime

@Service
class GithubActionsCommitService(
    private val githubClient: GithubClient,
    private val githubBuildConverter: GithubBuildConverter,
    private val commitRepository: CommitRepository
) {
    private var logger = LoggerFactory.getLogger(javaClass.name)

    fun getCommitsBetweenTimePeriod(
        startTime: ZonedDateTime? = null,
        endTime: ZonedDateTime,
        branch: String? = null,
        pipeline: Pipeline
    ): List<Commit> {
        logger.info("Started sync for Github Actions commits [${pipeline.url}]/[${branch}]")

        if (recordsExistInDb(pipeline, startTime, endTime)) return commitRepository.findByTimePeriod(
            pipeline.id,
            startTime?.toTimestamp() ?: 0,
            endTime.toTimestamp()
        )

        logger.info("No records exist in DB, send API request, time period: [${startTime}] to [${endTime}]")

        var keepRetrieving = true
        var pageIndex = 1
        val allCommits = mutableSetOf<GithubCommit>()

        while (keepRetrieving) {
            logger.info(
                "Get Github Commits - " +
                        "Sending request to Github Feign Client with owner: ${pipeline.url}, " +
                        "since: $startTime, until: $endTime, branch: $branch, pageIndex: $pageIndex"
            )

            val commitsFromGithub = githubClient.retrieveCommits(
                pipeline.url,
                pipeline.credential,
                startTime?.toString(),
                endTime.toString(),
                branch,
                pageIndex
            ) ?: emptyList()

            allCommits.addAll(commitsFromGithub)

            val hasDuplicationInDB = commitRepository.hasDuplication(commitsFromGithub)
            if (hasDuplicationInDB) {
                logger.info("Found duplication in DB, breaking out, current index: $pageIndex")
            }

            keepRetrieving = commitsFromGithub.isNotEmpty() && !hasDuplicationInDB
            pageIndex++
        }

        commitRepository.save(pipeline.id, allCommits.toList().toMutableList())

        return allCommits.map { githubBuildConverter.convertToCommit(it, pipeline.id) }
    }

    private fun recordsExistInDb(
        pipeline: Pipeline,
        startTime: ZonedDateTime?,
        endTime: ZonedDateTime
    ): Boolean {
        val theLatestCommitInDB = commitRepository.getTheLatestCommit(pipeline.id)
        if (theLatestCommitInDB != null && theLatestCommitInDB.timestamp > endTime.toTimestamp()) {
            logger.info("Commit records exist in DB, skip API request, time period: [${startTime}] to [${endTime}]")
            return true
        }
        return false
    }

}
