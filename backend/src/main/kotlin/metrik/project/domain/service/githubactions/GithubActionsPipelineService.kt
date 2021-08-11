package metrik.project.domain.service.githubactions

import metrik.infrastructure.utlils.RequestUtil
import metrik.infrastructure.utlils.RequestUtil.getDomain
import metrik.project.domain.model.Build
import metrik.project.domain.model.Pipeline
import metrik.project.domain.repository.BuildRepository
import metrik.project.domain.service.PipelineService
import metrik.project.exception.PipelineConfigVerifyException
import metrik.project.exception.SynchronizationException
import metrik.project.rest.vo.response.SyncProgress
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
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
import java.util.concurrent.atomic.AtomicInteger

@Service("githubActionsPipelineService")
class GithubActionsPipelineService(
    @Autowired
    private var restTemplate: RestTemplate,
    @Autowired
    private var buildRepository: BuildRepository
) : PipelineService {
    private var logger = LoggerFactory.getLogger(this.javaClass.name)

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
            val url = "${getDomain(pipeline.url)}$urlSummarySuffix"
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

        try {
            val maxBuildNumber = getMaxBuildNumber(pipeline, entity)
            val (inProgressBuildsToSync, newBuildsToSync) =
                buildRepository.getBuildNumbersNeedSyncNoFlatten(pipeline.id, maxBuildNumber)

            val totalBuildNumbersToSync = inProgressBuildsToSync.size + newBuildsToSync.size

            logger.info(
                "For Github Actions pipeline [${pipeline.id}] - total build number is [$maxBuildNumber], " +
                    "[$totalBuildNumbersToSync] of them need to be synced"
            )

            val buildDetailResponse = getNewBuildDetails(pipeline, newBuildsToSync.size, entity)

            val inProgressBuildDetailResponse = getInProgressBuildDetails(pipeline, inProgressBuildsToSync, entity)

            buildDetailResponse.workflowRuns.addAll(inProgressBuildDetailResponse)

//            buildDetailResponse.workflowRuns.forEach { it.jobs.add(getJobsInWorkflow(pipeline, it.id, entity)) }

            val retrieveBuildDetails = buildDetailResponse.workflowRuns.mapNotNull {
                val convertedBuild = it.convertToMetrikBuild(pipeline.id)
                if (convertedBuild != null) {
                    buildRepository.save(convertedBuild)
                }

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
            throw SynchronizationException("Verify website unavailable")
        } catch (ex: HttpClientErrorException) {
            throw SynchronizationException("Verify failed")
        }
    }

    private fun getNewBuildDetails(
        pipeline: Pipeline,
        buildNumber: Int,
        entity: HttpEntity<String>
    ): BuildDetailDTO {

        val callTimes = buildNumber / maxPerPage + 1

        val totalResponseBody = BuildDetailDTO(mutableListOf())

        for (page in 1..callTimes) {

            val url =
                "${getDomain(pipeline.url)}$urlSuffix?per_page=$maxPerPage&page=$page"

            logger.info("Get build details - Sending request to [$url] with entity [$entity]")
            var responseEntity: ResponseEntity<BuildDetailDTO>

            withApplicationException(
                {
                    responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity)
                    logger.info("Get build details - Response from [$url]: $responseEntity")
                    when (page) {
                        callTimes -> totalResponseBody.workflowRuns.addAll(
                            responseEntity.body!!.workflowRuns.take(buildNumber % maxPerPage)
                        )
                        else -> totalResponseBody.workflowRuns.addAll(responseEntity.body!!.workflowRuns)
                    }
                },
                url
            )
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
                    "${getDomain(pipeline.url)}$urlSuffix/$runID"
                logger.info("Get build details - Sending request to [$url] with entity [$entity]")
                var responseEntity: ResponseEntity<WorkflowRuns>

                val response = withApplicationException(
                    {
                        responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity)
                        logger.info("Get build details - Response from [$url]: $responseEntity")
                        responseEntity
                    },
                    url
                )?.body!!

                totalWorkflowRuns.add(response)
            }
        }

        return totalWorkflowRuns
    }

    protected fun <T> withApplicationException(action: () -> T, url: String): T? {
        try {
            return action()
        } catch (clientErrorException: HttpClientErrorException) {
            if (clientErrorException.statusCode.equals(HttpStatus.NOT_FOUND)) {
                logger.info(
                    """
                    Getting NOT_FOUND error found.
                    Exception: [$clientErrorException]
                    """.trimIndent()
                )
                return null
            } else {
                logger.error("Getting Github Actions build details failed, URL: [$url], exception: [$clientErrorException]")
                throw clientErrorException
            }
        }
    }

//    private fun getJobsInWorkflow(pipeline: Pipeline, runID: String, entity: HttpEntity<String>): Job {
//        val url = "${getDomain(pipeline.url)}$urlSuffix/$runID/jobs"
//        logger.info("Get jobs in run: $runID - Sending request to [$url] with entity [$entity]")
//        val response = restTemplate.exchange<Job>(url, HttpMethod.GET, entity)
//        logger.info("Get jobs in run - Response from [$url]: $response")
//        return response.body!!
//    }

    private fun getMaxBuildNumber(pipeline: Pipeline, entity: HttpEntity<String>): Int {
        val url = "${getDomain(pipeline.url)}$urlSuffix?per_page=1"
        logger.info("Get max build number - Sending request to [$url] with entity [$entity]")
        val response = restTemplate.exchange<BuildSummaryDTO>(url, HttpMethod.GET, entity)
        logger.info("Get max build number - Response from [$url]: $response")
        val buildSummaryDTO = response.body!!
        return buildSummaryDTO.totalCount
    }

    private companion object {
        const val urlSummarySuffix = "/actions/runs?per_page=1"
//        const val urlRepoWorkflow = "/actions/workflows"
        const val urlSuffix = "/actions/runs"
        const val maxPerPage = 100
    }
}
