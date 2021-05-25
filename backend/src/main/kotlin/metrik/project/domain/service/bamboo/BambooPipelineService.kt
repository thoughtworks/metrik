package metrik.project.domain.service.bamboo

import metrik.project.domain.model.Build
import metrik.infrastructure.utlils.RequestUtil.buildHeaders
import metrik.infrastructure.utlils.RequestUtil.getDomain
import metrik.project.exception.PipelineConfigVerifyException
import metrik.project.exception.SynchronizationException
import metrik.project.domain.model.Pipeline
import metrik.project.domain.repository.BuildRepository
import metrik.project.domain.service.PipelineService
import metrik.project.domain.service.bamboo.dto.BuildDetailDTO
import metrik.project.domain.service.bamboo.dto.BuildSummaryDTO
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
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.atomic.AtomicInteger
import kotlin.streams.toList

private const val PARALLELISM = 4
private val FORK_JOIN_POOL = ForkJoinPool(PARALLELISM)

@Service("bambooPipelineService")
class BambooPipelineService(
    @Autowired private var restTemplate: RestTemplate,
    @Autowired private var buildRepository: BuildRepository
) : PipelineService {
    private var logger = LoggerFactory.getLogger(this.javaClass.name)

    override fun verifyPipelineConfiguration(pipeline: Pipeline) {
        logger.info("Started verification for Bamboo pipeline [$pipeline]")
        val headers = buildHeaders(
            mapOf(
                Pair("Authorization", "Bearer ${pipeline.credential}"),
                Pair("Connection", "close")
            )
        )
        val entity = HttpEntity<String>(headers)

        try {
            val url = "${getDomain(pipeline.url)}/rest/api/latest/project/"
            logger.info("Bamboo verification - Sending request to [$url] with entity [$entity]")
            val responseEntity = restTemplate.exchange<String>(url, HttpMethod.GET, entity)
            logger.info("Bamboo verification - Response from [$url]: $responseEntity")
        } catch (ex: HttpServerErrorException) {
            throw PipelineConfigVerifyException("Verify website unavailable")
        } catch (ex: HttpClientErrorException) {
            throw PipelineConfigVerifyException("Verify failed")
        }
    }

    override fun getStagesSortedByName(pipelineId: String): List<String> {
        return buildRepository.getAllBuilds(pipelineId)
            .flatMap { it.stages }
            .map { it.name }
            .distinct()
            .sortedBy { it.toUpperCase() }
            .toList()
    }

    @Synchronized
    override fun syncBuildsProgressively(pipeline: Pipeline, emitCb: (SyncProgress) -> Unit): List<Build> {
        logger.info("Started data sync for Bamboo pipeline [$pipeline.id]")
        val credential = pipeline.credential
        val headers = buildHeaders(mapOf(Pair("Authorization", "Bearer $credential")))
        val entity = HttpEntity<String>(headers)

        val progressCounter = AtomicInteger(0)

        try {
            val planKey = URL(pipeline.url).path.split("/").last()
            val maxBuildNumber = getMaxBuildNumber(pipeline, planKey, entity)
            val buildNumbersToSync = buildRepository.getBuildNumbersNeedSync(pipeline.id, maxBuildNumber)

            logger.info(
                "For Bamboo pipeline [${pipeline.id}] - total build number is [$maxBuildNumber], " +
                        "[${buildNumbersToSync.size}] of them need to be synced"
            )

            val retrieveBuildDetails = {
                buildNumbersToSync.parallelStream().map { buildNumber ->
                    val buildDetailResponse = getBuildDetails(pipeline, planKey, buildNumber, entity)
                    val convertedBuild = buildDetailResponse?.convertToMetrikBuild(pipeline.id)

                    if (convertedBuild != null) {
                        buildRepository.save(convertedBuild)
                    }
                    emitCb(
                        SyncProgress(
                            pipeline.id,
                            pipeline.name,
                            progressCounter.incrementAndGet(),
                            buildNumbersToSync.size
                        )
                    )
                    logger.info("[${pipeline.id}] sync progress: [${progressCounter.get()}/${buildNumbersToSync.size}]")
                    convertedBuild
                }.toList().mapNotNull { it }
            }
            val builds = FORK_JOIN_POOL.submit(retrieveBuildDetails).get()
            logger.info(
                "For Bamboo pipeline [${pipeline.id}] - Successfully synced [${buildNumbersToSync.size}] builds"
            )

            return builds
        } catch (ex: HttpServerErrorException) {
            throw SynchronizationException("Verify website unavailable")
        } catch (ex: HttpClientErrorException) {
            throw SynchronizationException("Verify failed")
        }
    }

    private fun getBuildDetails(
        pipeline: Pipeline, planKey: String, buildNumber: Int,
        entity: HttpEntity<String>
    ): BuildDetailDTO? {
        val url = "${getDomain(pipeline.url)}/rest/api/latest/result/${planKey}-${buildNumber}.json?" +
                "expand=changes.change,stages.stage.results"
        logger.info("Get build details - Sending request to [$url] with entity [$entity]")
        var responseEntity: ResponseEntity<BuildDetailDTO>
        try {
            responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity)
            logger.info("Get build details - Response from [$url]: $responseEntity")
            return responseEntity.body!!
        } catch (clientErrorException: HttpClientErrorException) {
            if (clientErrorException.statusCode.equals(HttpStatus.NOT_FOUND)) {
                logger.info(
                    """
                    Getting NOT_FOUND error for build [$buildNumber], skip this build since not exist.
                    Exception: [$clientErrorException]
                    """.trimIndent()
                )
                return null
            } else {
                logger.error("Getting Bamboo build details failed, URL: [$url], exception: [$clientErrorException]")
                throw clientErrorException
            }
        }
    }

    private fun getMaxBuildNumber(pipeline: Pipeline, planKey: String, entity: HttpEntity<String>): Int {
        val url = "${getDomain(pipeline.url)}/rest/api/latest/result/${planKey}.json"
        logger.info("Get max build number - Sending request to [$url] with entity [$entity]")
        val response = restTemplate.exchange<BuildSummaryDTO>(url, HttpMethod.GET, entity)
        logger.info("Get max build number - Response from [$url]: $response")
        val buildSummaryDTO = response.body!!
        return buildSummaryDTO.results.result.first().buildNumber
    }
}
