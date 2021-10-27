package metrik.project.domain.service.bamboo

import feign.FeignException
import metrik.infrastructure.utlils.RequestUtil.getDomain
import metrik.project.domain.model.Execution
import metrik.project.domain.model.PipelineConfiguration
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
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.atomic.AtomicInteger
import kotlin.streams.toList

private const val PARALLELISM = 4
private val FORK_JOIN_POOL = ForkJoinPool(PARALLELISM)

@Service("bambooDeploymentPipelineService")
class BambooDeploymentPipelineService(
    @Autowired private var buildRepository: BuildRepository,
    @Autowired private var bambooFeignClient: BambooFeignClient
) : PipelineService {

    private var logger = LoggerFactory.getLogger(this.javaClass.name)

    override fun verifyPipelineConfiguration(pipeline: PipelineConfiguration) {
        logger.info("Started verification for Bamboo pipeline [$pipeline]")

        try {
            val url = getDomain(pipeline.url)
            logger.info("Bamboo verification - Sending request to [$url]")
            bambooFeignClient.verify(URI(url), pipeline.credential)
            logger.info("Bamboo verification success")
        } catch (ex: FeignException.FeignServerException) {
            throw PipelineConfigVerifyException("Verify website unavailable")
        } catch (ex: FeignException.FeignClientException) {
            throw PipelineConfigVerifyException("Verify failed")
        }
    }

    @Synchronized
    override fun syncBuildsProgressively(pipeline: PipelineConfiguration, emitCb: (SyncProgress) -> Unit)
    : List<Execution> {
        logger.info("Started data sync for Bamboo deployment pipeline [$pipeline.id]")
        val credential = pipeline.credential
        val progressCounter = AtomicInteger(0)

        try {
            val (planKey, environmentList) = getPlanKeyAndEnvironments(pipeline)
            val maxBuildNumber = getMaxBuildNumber(pipeline, planKey, credential)
            val latestDeployTimestamp = buildRepository.getLatestDeployTimestamp(pipeline.id)
            val deploymentNumbersToSync =
                getNewDeploymentResultRelatedBuildNumbers(pipeline, environmentList, latestDeployTimestamp)
            val buildNumbersToSync = buildRepository.getBambooJenkinsBuildNumbersNeedSync(pipeline.id, maxBuildNumber)
            val allNeedToSync = mutableSetOf<Int>()
            allNeedToSync.addAll(deploymentNumbersToSync)
            allNeedToSync.addAll(buildNumbersToSync)
            logger.info(
                "For Bamboo pipeline [${pipeline.id}] - total build number is [$maxBuildNumber], " +
                        "[${allNeedToSync.size}] of them need to be synced"
            )
            val retrieveBuildDetails = {
                allNeedToSync.parallelStream().map { buildNumber ->
                    val buildDetailDTO = getBuildDetails(pipeline, planKey, buildNumber, credential)
                    val deploymentResultMap =
                        getDeploymentResultsForBuild(pipeline, environmentList, "$planKey-$buildNumber")
                    val convertedBuild =
                        buildDetailDTO?.convertToMetricBuildWithDeploymentResult(pipeline.id, deploymentResultMap)
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
        } catch (ex: FeignException.FeignServerException) {
            throw SynchronizationException("Verify website unavailable")
        } catch (ex: FeignException.FeignClientException) {
            throw SynchronizationException("Verify failed")
        }
    }


    override fun getStagesSortedByName(pipelineId: String): List<String> =
        buildRepository.getAllBuilds(pipelineId)
            .flatMap { it.stages }
            .map { it.name }
            .distinct()
            .sortedBy { it.toUpperCase() }
            .toList()

    private fun getPlanKeyAndEnvironments(pipeline: PipelineConfiguration): Pair<String, List<Environment>> {
        val deployProjectId = pipeline.url.split("id=").last()
        val url = "${getDomain(pipeline.url)}/rest/api/latest/deploy/project/$deployProjectId"
        logger.info("Get plan key and deploy environments - Sending request to [$url]")
        val deployProjectDTO = bambooFeignClient.getDeploySummary(URI(url), pipeline.credential)
        logger.info("Get plan key and deploy environments - Response from [$url]: $deployProjectDTO")
        return Pair(deployProjectDTO!!.planKey.key, deployProjectDTO.environments)
    }

    private fun getDeploymentResultsForBuild(
        pipeline: PipelineConfiguration,
        environmentList: List<Environment>,
        buildResultKey: String
    ): MutableMap<String, List<DeploymentResult>> {
        val deploymentResultMap = mutableMapOf<String, List<DeploymentResult>>()
        logger.info("Start getting deploy results - Getting results for [$environmentList]")
        for (environment in environmentList) {
            val url = "${getDomain(pipeline.url)}/rest/api/latest/deploy/environment/${environment.id}/results?expand"
            logger.info("Get deploy results - Sending request to [$url]")
            val deployResults = bambooFeignClient.getDeployResults(URI(url), pipeline.credential)
            logger.info("Get deploy results - Response from [$url]: $deployResults")
            val allDeployResults = deployResults?.results ?: emptyList()
            deploymentResultMap[environment.name] = allDeployResults.filter {
                getDeploymentVersionPlanResultKey(pipeline, it.deploymentVersion.id) == buildResultKey
            }
        }
        return deploymentResultMap
    }

    private fun getNewDeploymentResultRelatedBuildNumbers(
        pipeline: PipelineConfiguration,
        environmentList: List<Environment>,
        latestTimestamp: Long
    ): List<Int> {
        val buildNeedToSyncNumbers = mutableSetOf<Int>()
        for (environment in environmentList) {
            val url = "${getDomain(pipeline.url)}/rest/api/latest/deploy/environment/${environment.id}/results?expand"
            logger.info("Get deploy results - Sending request to [$url]")
            val deployResults = bambooFeignClient.getDeployResults(URI(url), pipeline.credential)
            logger.info("Get deploy results - Response from [$url]: $deployResults")
            val resultKeys = deployResults?.results!!.filter { it.startedDate!! > latestTimestamp }
                .map { getDeploymentVersionPlanResultKey(pipeline, it.deploymentVersion.id) }
            resultKeys.map { it.split("-").last().toInt() }.toCollection(buildNeedToSyncNumbers)
        }
        return buildNeedToSyncNumbers.toList()
    }

    private fun getDeploymentVersionPlanResultKey(pipeline: PipelineConfiguration, versionId: Long): String {
        val url = "${getDomain(pipeline.url)}/rest/api/latest/deploy/version/$versionId/build-result"
        logger.info("Get deploy version information - Sending request to [$url]")
        val deploymentVersionBuildResultDTO =
            bambooFeignClient.getDeployVersionInfo(URI(url), pipeline.credential)
        return deploymentVersionBuildResultDTO!!.planResultKey.key
    }

    private fun getMaxBuildNumber(pipeline: PipelineConfiguration, planKey: String, credential: String): Int {
        val url = "${getDomain(pipeline.url)}/rest/api/latest/result/$planKey.json"
        logger.info("Get max build number - Sending request to [$url]")
        val summaryDTO = bambooFeignClient.getMaxBuildNumber(URI(url), credential)
        logger.info("Get max build number - Response from [$url]: $summaryDTO")
        val buildSummaryDTO = summaryDTO!!
        return buildSummaryDTO.results.result.first().buildNumber
    }

    private fun getBuildDetails(
        pipeline: PipelineConfiguration,
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
        } catch (ex: FeignException.FeignClientException) {
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

}