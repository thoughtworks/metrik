package metrik.project.domain.service.bamboo

import feign.FeignException.FeignClientException
import feign.FeignException.FeignServerException
import metrik.infrastructure.utlils.RequestUtil.getDomain
import metrik.project.domain.model.Execution
import metrik.project.domain.model.Pipeline
import metrik.project.domain.repository.BuildRepository
import metrik.project.domain.service.PipelineService
import metrik.project.exception.PipelineConfigVerifyException
import metrik.project.exception.SynchronizationException
import metrik.project.infrastructure.bamboo.feign.BambooFeignClient
import metrik.project.rest.vo.response.SyncProgress
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.net.URI
import java.net.URL
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.atomic.AtomicInteger
import kotlin.streams.toList

private const val PARALLELISM = 4
private val FORK_JOIN_POOL = ForkJoinPool(PARALLELISM)

@Service("bambooPipelineService")
class BambooPipelineService(
    @Autowired private var buildRepository: BuildRepository,
    @Autowired private var bambooFeignClient: BambooFeignClient
) : PipelineService {
    private var logger = LoggerFactory.getLogger(this.javaClass.name)

    override fun verifyPipelineConfiguration(pipeline: Pipeline) {
        logger.info("Started verification for Bamboo pipeline [$pipeline]")
        try {
            val url = getDomain(pipeline.url)
            logger.info("Bamboo verification - Sending request to [$url]")
            bambooFeignClient.verify(URI(url), pipeline.credential)
            logger.info("Bamboo verification success")
        } catch (ex: FeignServerException) {
            throw PipelineConfigVerifyException("Verify website unavailable")
        } catch (ex: FeignClientException) {
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
    override fun syncBuildsProgressively(pipeline: Pipeline, emitCb: (SyncProgress) -> Unit): List<Execution> {
        logger.info("Started data sync for Bamboo pipeline [$pipeline.id]")
        val credential = pipeline.credential
        val progressCounter = AtomicInteger(0)

        try {
            val planKey = URL(pipeline.url).path.split("/").last()
            val maxBuildNumber = getMaxBuildNumber(pipeline, planKey, credential)
            val buildNumbersToSync = buildRepository.getBambooJenkinsBuildNumbersNeedSync(pipeline.id, maxBuildNumber)

            logger.info(
                "For Bamboo pipeline [${pipeline.id}] - total build number is [$maxBuildNumber], " +
                        "[${buildNumbersToSync.size}] of them need to be synced"
            )

            val retrieveBuildDetails = {
                buildNumbersToSync.parallelStream().map { buildNumber ->
                    val buildDetailDTO = getBuildDetails(pipeline, planKey, buildNumber, credential)
                    val convertedBuild = buildDetailDTO?.convertToMetrikBuild(pipeline.id)

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
        } catch (ex: FeignServerException) {
            throw SynchronizationException("Verify website unavailable")
        } catch (ex: FeignClientException) {
            throw SynchronizationException("Verify failed")
        }
    }

    private fun getBuildDetails(
        pipeline: Pipeline,
        planKey: String,
        buildNumber: Int,
        credential: String
    ): BuildDetailDTO? {
        val url = "${getDomain(pipeline.url)}/rest/api/latest/result/$planKey-$buildNumber.json?" +
                "expand=changes.change,stages.stage.results"
        logger.info("Get build details - Sending request to [$url]")
        try {
            val buildDetailDTO: BuildDetailDTO? = bambooFeignClient.getBuildDetails(URI(url), credential)
            logger.info("Get build details - Response from [$url]: $buildDetailDTO")
            return buildDetailDTO!!
        } catch (ex: FeignClientException) {
            if (ex.status() == HttpStatus.NOT_FOUND.value()) {
                logger.info(
                    """
                    Getting NOT_FOUND error for build [$buildNumber], skip this build since not exist.
                    Exception: [$ex]
                    """.trimIndent()
                )
                return null
            } else {
                logger.error("Getting Bamboo build details failed, URL: [$url], exception: [$ex]")
                throw ex
            }
        }
    }

    private fun getMaxBuildNumber(pipeline: Pipeline, planKey: String, credential: String): Int {
        val url = "${getDomain(pipeline.url)}/rest/api/latest/result/$planKey.json"
        logger.info("Get max build number - Sending request to [$url]")
        val summaryDTO = bambooFeignClient.getMaxBuildNumber(URI(url), credential)
        logger.info("Get max build number - Response from [$url]: $summaryDTO")
        val buildSummaryDTO = summaryDTO!!
        return buildSummaryDTO.results.result.first().buildNumber
    }
}
