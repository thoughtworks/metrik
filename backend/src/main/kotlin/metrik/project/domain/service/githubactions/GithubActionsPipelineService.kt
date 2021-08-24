package metrik.project.domain.service.githubactions

import metrik.infrastructure.utlils.RequestUtil
import metrik.infrastructure.utlils.toTimestamp
import metrik.project.domain.model.Build
import metrik.project.domain.model.Commit
import metrik.project.domain.model.Pipeline
import metrik.project.domain.repository.BuildRepository
import metrik.project.domain.service.PipelineService
import metrik.project.exception.PipelineConfigVerifyException
import metrik.project.exception.SynchronizationException
import metrik.project.infrastructure.github.GithubClient
import metrik.project.rest.vo.response.SyncProgress
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException
import java.net.URL
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.atomic.AtomicInteger

@Service("githubActionsPipelineService")
class GithubActionsPipelineService(
    private val githubActionsCommitService: GithubActionsCommitService,
    private val buildRepository: BuildRepository,
    private val githubUtil: GithubUtil,
    private val githubBuildConverter: GithubBuildConverter,
    private val githubClient: GithubClient
) : PipelineService {
    private var logger = LoggerFactory.getLogger(javaClass.name)

    override fun verifyPipelineConfiguration(pipeline: Pipeline) {
        logger.info("Started verification for Github Actions pipeline [$pipeline]")
        val token = githubUtil.getToken(pipeline.credential)
        val (owner, repo) = githubUtil.getOwnerRepoFromUrl(pipeline.url)

        try {
//            val url = "${pipeline.url}$urlSummarySuffix"
//            logger.info("Github Actions verification - Sending request to [$url] with entity [$entity]")
//            val responseEntity = restTemplate.exchange<String>(url, HttpMethod.GET, entity)
//            logger.info("Github Actions verification - Response from [$url]: $responseEntity")
            githubClient.retrieveMultipleRuns(token, owner, repo)
        } catch (ex: HttpServerErrorException) {
            throw PipelineConfigVerifyException("Verify website unavailable")
        } catch (ex: HttpClientErrorException) {
            throw PipelineConfigVerifyException("Verify failed")
        }
    }

    override fun getStagesSortedByName(pipelineId: String): List<String> =
        buildRepository.getAllBuilds(pipelineId)
            .flatMap { it.stages }
            .map { it.name }
            .distinct()
            .sortedBy { it.toUpperCase() }
            .toList()

    @Synchronized
    override fun syncBuildsProgressively(pipeline: Pipeline, emitCb: (SyncProgress) -> Unit): List<Build> {
        logger.info("Started data sync for Github Actions pipeline [$pipeline.id]")
//        val entity = getHttpHeader(pipeline)

        val progressCounter = AtomicInteger(0)

//        val maxBuildNumber = getMaxBuildNumber(pipeline, entity)

        try {

            val latestTimestamp = when (
                val latestBuild = buildRepository.getLatestBuild(pipeline.id)
            ) {
                null -> Long.MIN_VALUE
                else -> latestBuild.timestamp
            }

            val newRuns = getNewRuns(pipeline, latestTimestamp)

            val inProgressBuilds = buildRepository.getInProgressBuilds(pipeline.id)
            val inProgressRuns = getInProgressRuns(pipeline, inProgressBuilds)

            newRuns.addAll(inProgressRuns)

            val totalBuildNumbersToSync = newRuns.size

            logger.info(
                "For Github Actions pipeline [${pipeline.id}] - " + "[$totalBuildNumbersToSync] need to be synced"
            )

            val mapToCommits = mapCommitToWorkflow(pipeline, newRuns)

            val builds = newRuns.map {

                val commits: List<Commit> = mapToCommits[it.branch]!![it]!!
                val convertedBuild = githubBuildConverter.convertToBuild(it, pipeline.id, commits)

                buildRepository.save(convertedBuild)

                emitCb(
                    SyncProgress(
                        pipeline.id,
                        pipeline.name,
                        progressCounter.incrementAndGet(),
                        totalBuildNumbersToSync
                    )
                )

                logger.info("[${pipeline.id}] sync progress: [${progressCounter.get()}/$totalBuildNumbersToSync]")
                convertedBuild
            }

            logger.info(
                "For Github Actions pipeline [${pipeline.id}] - Successfully synced [$totalBuildNumbersToSync] builds"
            )

            return builds
        } catch (ex: HttpServerErrorException) {
            throw SynchronizationException("Connection to Github Actions is unavailable")
        } catch (ex: HttpClientErrorException) {
            throw SynchronizationException("Syncing Github Commits details failed")
        }
    }

    fun getNewRuns(
        pipeline: Pipeline,
        latestTimestamp: Long,
        maxPerPage: Int = defaultMaxPerPage
    ): MutableList<GithubActionsRun> {

        val token = githubUtil.getToken(pipeline.credential)
        val (owner, repo) = githubUtil.getOwnerRepoFromUrl(pipeline.url)

        var ifRetrieving = true
        var pageIndex = 1

        val totalRuns = mutableListOf<GithubActionsRun>()

        while (ifRetrieving) {

//            val url = "${pipeline.url}$urlSuffix?per_page=$maxPerPage&page=$pageIndex"

//            logger.info("Get build details - Sending request to [$url] with entity [$entity]")
//            var responseEntity: ResponseEntity<BuildDetailDTO>

//                    responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity)
//                    logger.info("Get build details - Response from [$url]: $responseEntity")

            val runs = githubClient.retrieveMultipleRuns(token, owner, repo, maxPerPage, pageIndex) ?: break

            val runsNeedToSync = runs.filter { it.createdTimestamp.toTimestamp() > latestTimestamp }

            ifRetrieving = runsNeedToSync.size == maxPerPage

            totalRuns.addAll(runsNeedToSync)

            pageIndex++
        }
        return totalRuns
    }

    fun mapCommitToWorkflow(pipeline: Pipeline, runs: MutableList<GithubActionsRun>):
        Map<String, Map<GithubActionsRun, List<Commit>>> {
            val mapRunsToCommits: MutableMap<String, Map<GithubActionsRun, List<Commit>>> = mutableMapOf()
            runs
                .groupBy { it.branch }
                .forEach { (branch, run) -> mapRunsToCommits[branch] = mapRunToCommits(pipeline, run) }
            return mapRunsToCommits.toMap()
        }

    private fun getInProgressRuns(
        pipeline: Pipeline,
        builds: List<Build>,
    ): MutableList<GithubActionsRun> {

        val token = githubUtil.getToken(pipeline.credential)
        val (owner, repo) = githubUtil.getOwnerRepoFromUrl(pipeline.url)

        val runs = mutableListOf<GithubActionsRun>()

        builds.forEach { build ->
            run {
                val runId = URL(build.url).path.split("/").last()
//                val url = "${pipeline.url}$urlSuffix/$runId"
//                logger.info("Get build details - Sending request to [$url] with entity [$entity]")
//                var responseEntity: ResponseEntity<WorkflowRuns>

                val run = githubClient.retrieveSingleRun(token, owner, repo, runId)
//                        responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity)
//                        logger.info("Get build details - Response from [$url]: $responseEntity")

                run?.also { runs.add(it) }
            }
        }

        return runs
    }

    private fun <T> withApplicationException(action: () -> T, url: String): T? {
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
                    "Getting Github Actions build details failed, URL: [$url], exception: [$clientErrorException]"
                )
                throw clientErrorException
            }
        }
    }

    private fun getHttpHeader(pipeline: Pipeline): HttpEntity<String> {
        val headers = RequestUtil.buildHeaders(
            mapOf(
                Pair("Authorization", "Bearer ${pipeline.credential}"),
                Pair("Connection", "close")
            )
        )
        return HttpEntity(headers)
    }

    private fun mapRunToCommits(pipeline: Pipeline, runs: List<GithubActionsRun>): Map<GithubActionsRun, List<Commit>> {
        val map: MutableMap<GithubActionsRun, List<Commit>> = mutableMapOf()

        runs.sortedByDescending { it.commitTimeStamp }

        val latestTimestampInRuns = runs.first().commitTimeStamp
        val lastRun = runs.last()
        val previousRunBeforeLastRun = buildRepository.getPreviousBuild(
            pipeline.id,
            lastRun.commitTimeStamp.toTimestamp(),
            lastRun.branch
        )?.changeSets?.first()?.timestamp
        val previousRunZonedDateTime = previousRunBeforeLastRun?.let {
            ZonedDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneOffset.UTC)
        }
        val allCommits = githubActionsCommitService.getCommitsBetweenBuilds(
            previousRunZonedDateTime?.plus(COMMIT_OFFSET, ChronoUnit.SECONDS),
            latestTimestampInRuns,
            branch = lastRun.branch,
            pipeline = pipeline,
        )

        runs.forEachIndexed { index, run ->
            val currentRunTimeStamp = run.commitTimeStamp.toTimestamp()
            val previousBuildInRepo = buildRepository.getPreviousBuild(
                pipeline.id,
                run.createdTimestamp.toTimestamp(),
                run.branch
            )?.changeSets?.first()?.timestamp
            val lastRunTimeStamp = when (index) {
                runs.lastIndex -> previousRunBeforeLastRun
                else -> {
                    val previousRunTimeStamp = runs[index + 1].commitTimeStamp.toTimestamp()
                    if (previousBuildInRepo == null) previousRunTimeStamp
                    else maxOf(previousRunTimeStamp, previousBuildInRepo)
                }
            }
            val commits = when (lastRunTimeStamp) {
                null -> allCommits.filter { it.timestamp <= currentRunTimeStamp }
                else -> allCommits.filter { it.timestamp in (lastRunTimeStamp + 1)..currentRunTimeStamp }
            }
            map[run] = commits
        }
        return map.toMap()
    }

//    private fun getMaxBuildNumber(pipeline: Pipeline, entity: HttpEntity<String>): Int {
//        val url = "${pipeline.url}$urlSummarySuffix"
//        logger.info("Get max build number - Sending request to [$url] with entity [$entity]")
//        val response = restTemplate.exchange<BuildSummaryDTO>(url, HttpMethod.GET, entity)
//        logger.info("Get max build number - Response from [$url]: $response")
//        val buildSummaryDTO = response.body!!
//        return buildSummaryDTO.totalCount
//    }

    private companion object {
        const val urlSuffix = "/actions/runs"
        const val defaultMaxPerPage = 100
        const val COMMIT_OFFSET: Long = 1
    }
}
