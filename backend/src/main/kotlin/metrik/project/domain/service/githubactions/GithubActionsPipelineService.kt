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
import metrik.project.rest.vo.response.SyncProgress
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import java.net.URL
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.atomic.AtomicInteger

@Service("githubActionsPipelineService")
class GithubActionsPipelineService(
    private var restTemplate: RestTemplate,
    private var githubActionsCommitService: GithubActionsCommitService,
    private var buildRepository: BuildRepository
) : PipelineService {
    private var logger = LoggerFactory.getLogger(javaClass.name)

    override fun verifyPipelineConfiguration(pipeline: Pipeline) {
        logger.info("Started verification for Github Actions pipeline [$pipeline]")
        val headers = RequestUtil.buildHeaders(
            mapOf(
                Pair("Authorization", "Bearer ${pipeline.credential}"),
                Pair("Connection", "close")
            )
        )
        val entity = HttpEntity<String>(headers)

        try {
            val url = "${pipeline.url}$urlSummarySuffix"
            logger.info("Github Actions verification - Sending request to [$url] with entity [$entity]")
            val responseEntity = restTemplate.exchange<String>(url, HttpMethod.GET, entity)
            logger.info("Github Actions verification - Response from [$url]: $responseEntity")
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
        val credential = pipeline.credential
        val headers = RequestUtil.buildHeaders(mapOf(Pair("Authorization", "Bearer $credential")))
        val entity = HttpEntity<String>(headers)

        val progressCounter = AtomicInteger(0)

        val maxBuildNumber = getMaxBuildNumber(pipeline, entity)

        try {

            val latestTimestamp = when (
                val latestBuild = buildRepository.getLatestBuild(pipeline.id)
            ) {
                null -> Long.MIN_VALUE
                else -> latestBuild.timestamp
            }

            val buildDetailResponse = getNewBuildDetails(pipeline, latestTimestamp, entity)

            val inProgressBuilds = buildRepository.getInProgressBuilds(pipeline.id)
            val inProgressBuildDetailResponse = getInProgressBuildDetails(pipeline, inProgressBuilds, entity)

            buildDetailResponse.workflowRuns.addAll(inProgressBuildDetailResponse)

            val totalBuildNumbersToSync = buildDetailResponse.workflowRuns.size

            logger.info(
                "For Github Actions pipeline [${pipeline.id}] - total build number is [$maxBuildNumber], " +
                    "[$totalBuildNumbersToSync] of them need to be synced"
            )

            buildDetailResponse.workflowRuns.sortedByDescending { it.headCommit.timestamp }

            val mapToCommits = mapCommitToWorkflow(pipeline, buildDetailResponse.workflowRuns)

            val retrieveBuildDetails = buildDetailResponse.workflowRuns.map {

                val commits: List<Commit> = mapToCommits[it.headBranch]!![it]!!
                val convertedBuild = it.convertToMetrikBuild(pipeline.id, commits)

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

            return retrieveBuildDetails
        } catch (ex: HttpServerErrorException) {
            throw SynchronizationException("Connection to Github Actions is unavailable")
        } catch (ex: HttpClientErrorException) {
            throw SynchronizationException("Syncing Github Commits details failed")
        }
    }

    private fun getNewBuildDetails(
        pipeline: Pipeline,
        latestTimestamp: Long,
        entity: HttpEntity<String>,
        maxPerPage: Int = defaultMaxPerPage
    ): BuildDetailDTO {

        var ifRetrieving = true
        var pageIndex = 1

        val totalResponseBody = BuildDetailDTO(mutableListOf())

        while (ifRetrieving) {

            val url =
                "${pipeline.url}$urlSuffix?per_page=$maxPerPage&page=$pageIndex"

            logger.info("Get build details - Sending request to [$url] with entity [$entity]")
            var responseEntity: ResponseEntity<BuildDetailDTO>

            withApplicationException(
                {
                    responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity)
                    logger.info("Get build details - Response from [$url]: $responseEntity")

                    val buildsNeedToSync = responseEntity.body!!.workflowRuns
                        .filter { it.getBuildTimestamp(it.createdAt) > latestTimestamp }

                    ifRetrieving = buildsNeedToSync.size == maxPerPage

                    totalResponseBody.workflowRuns.addAll(buildsNeedToSync)
                    true
                },
                url
            ) ?: break

            pageIndex++
        }
        return totalResponseBody
    }

    private fun getInProgressBuildDetails(
        pipeline: Pipeline,
        builds: List<Build>,
        entity: HttpEntity<String>
    ): MutableList<WorkflowRuns> {
        val totalWorkflowRuns = mutableListOf<WorkflowRuns>()

        builds.forEach { build ->
            run {
                val runID = URL(build.url).path.split("/").last()
                val url =
                    "${pipeline.url}$urlSuffix/$runID"
                logger.info("Get build details - Sending request to [$url] with entity [$entity]")
                var responseEntity: ResponseEntity<WorkflowRuns>

                val response = withApplicationException(
                    {
                        responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity)
                        logger.info("Get build details - Response from [$url]: $responseEntity")
                        responseEntity
                    },
                    url
                )?.let { it.body!! }

                response?.also { totalWorkflowRuns.add(it) }
            }
        }

        return totalWorkflowRuns
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
                    "Getting Github Actions build details failed, URL: [$url], exception: [$clientErrorException]"
                )
                throw clientErrorException
            }
        }
    }

    fun mapCommitToWorkflow(pipeline: Pipeline, workflows: MutableList<WorkflowRuns>):
        Map<String, Map<WorkflowRuns, List<Commit>>> {
            val workflowsNewMap: MutableMap<String, Map<WorkflowRuns, List<Commit>>> = mutableMapOf()
            workflows
                .groupBy { it.headBranch }
                .forEach { (branch, workflow) -> workflowsNewMap[branch] = mapRunToCommits(pipeline, workflow) }
            return workflowsNewMap.toMap()
        }

    private fun mapRunToCommits(pipeline: Pipeline, runs: List<WorkflowRuns>): Map<WorkflowRuns, List<Commit>> {
        val map: MutableMap<WorkflowRuns, List<Commit>> = mutableMapOf()

        val latestTimestamp = runs.first().headCommit.timestamp
        val lastRun = runs.last()
        val previousRunBeforeLastRun = buildRepository.getPreviousBuild(
            pipeline.id,
            lastRun.getBuildTimestamp(lastRun.headCommit.timestamp),
            lastRun.headBranch
        )?.changeSets?.first()?.timestamp
        val previousZonedDateTime = previousRunBeforeLastRun?.let {
            ZonedDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneOffset.UTC)
        }
        val totalCommits = githubActionsCommitService.getCommitsBetweenBuilds(
            previousZonedDateTime?.plus(COMMIT_OFFSET, ChronoUnit.SECONDS),
            latestTimestamp,
            branch = lastRun.headBranch,
            pipeline = pipeline,
        )

        runs.forEachIndexed { index, run ->
            val runTimeStamp = run.headCommit.timestamp.toTimestamp()
            val previousBuild = buildRepository.getPreviousBuild(
                pipeline.id,
                run.getBuildTimestamp(run.createdAt),
                run.headBranch
            )?.changeSets?.first()?.timestamp
            val lastTimeStamp = when (index) {
                runs.lastIndex -> previousRunBeforeLastRun
                else -> {
                    val toEpochSecond = runs[index + 1].headCommit.timestamp.toTimestamp()
                    if (previousBuild == null) toEpochSecond
                    else maxOf(toEpochSecond, previousBuild)
                }
            }
            val commits = when (lastTimeStamp) {
                null -> totalCommits.filter { it.timestamp <= runTimeStamp }
                else -> totalCommits.filter { it.timestamp in (lastTimeStamp + 1)..runTimeStamp }
            }
            map[run] = commits
        }
        return map.toMap()
    }

    private fun getMaxBuildNumber(pipeline: Pipeline, entity: HttpEntity<String>): Int {
        val url = "${pipeline.url}$urlSuffix?per_page=1"
        logger.info("Get max build number - Sending request to [$url] with entity [$entity]")
        val response = restTemplate.exchange<BuildSummaryDTO>(url, HttpMethod.GET, entity)
        logger.info("Get max build number - Response from [$url]: $response")
        val buildSummaryDTO = response.body!!
        return buildSummaryDTO.totalCount
    }

    private companion object {
        const val urlSuffix = "/actions/runs"
        const val urlSummarySuffix = "$urlSuffix?per_page=1"
        const val defaultMaxPerPage = 100
        const val COMMIT_OFFSET: Long = 1
    }
}
