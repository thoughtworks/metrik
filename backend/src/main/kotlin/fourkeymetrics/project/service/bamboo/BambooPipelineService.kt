package fourkeymetrics.project.service.bamboo

import fourkeymetrics.common.model.Build
import fourkeymetrics.common.model.Commit
import fourkeymetrics.common.model.Stage
import fourkeymetrics.common.utlils.RequestUtil.buildHeaders
import fourkeymetrics.common.utlils.RequestUtil.getDomain
import fourkeymetrics.common.utlils.TimeFormatUtil.mapDateToTimeStamp
import fourkeymetrics.exception.ApplicationException
import fourkeymetrics.project.controller.applicationservice.SyncProgress
import fourkeymetrics.project.model.Pipeline
import fourkeymetrics.project.repository.BuildRepository
import fourkeymetrics.project.service.PipelineService
import fourkeymetrics.project.service.bamboo.dto.BuildDetailDTO
import fourkeymetrics.project.service.bamboo.dto.BuildSummaryDTO
import fourkeymetrics.project.service.bamboo.dto.StageDTO
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

private const val PARALLELISM = 25
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
            throw ApplicationException(HttpStatus.SERVICE_UNAVAILABLE, "Verify website unavailable")
        } catch (ex: HttpClientErrorException) {
            throw ApplicationException(HttpStatus.BAD_REQUEST, "Verify failed")
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
    override fun syncBuilds(pipeline: Pipeline): List<Build> {
        logger.info("Started data sync for Bamboo pipeline [$pipeline.id]")
        val credential = pipeline.credential
        val headers = buildHeaders(mapOf(Pair("Authorization", "Bearer $credential")))
        val entity = HttpEntity<String>(headers)

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
                    val convertedBuild = convertToBuild(buildDetailResponse, pipeline.id)
                    if (convertedBuild != null) {
                        buildRepository.save(convertedBuild)
                    }
                    convertedBuild
                }.toList().mapNotNull { it }
            }
            val builds = FORK_JOIN_POOL.submit(retrieveBuildDetails).get()

            logger.info("For Bamboo pipeline [${pipeline.id}] - Successfully synced [${builds.size}] builds")

            return builds
        } catch (ex: HttpServerErrorException) {
            throw ApplicationException(HttpStatus.SERVICE_UNAVAILABLE, "Verify website unavailable")
        } catch (ex: HttpClientErrorException) {
            throw ApplicationException(HttpStatus.BAD_REQUEST, "Verify failed")
        }
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
                    val convertedBuild = convertToBuild(buildDetailResponse, pipeline.id)
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
            throw ApplicationException(HttpStatus.SERVICE_UNAVAILABLE, "Verify website unavailable")
        } catch (ex: HttpClientErrorException) {
            throw ApplicationException(HttpStatus.BAD_REQUEST, "Verify failed")
        }
    }

    private fun convertToBuild(buildDetailResponse: BuildDetailDTO, pipelineId: String): Build? {
        logger.info(
            "Bamboo converting: Started converting BuildDetailDTO " +
                    "[$buildDetailResponse] for pipeline [$pipelineId]"
        )
        try {
            val buildTimestamp = getBuildTimestamp(buildDetailResponse) ?: return null
            val stages = buildDetailResponse.stages.stage.mapNotNull {
                convertToBuildStage(it)
            }

            val build = Build(
                pipelineId,
                buildDetailResponse.buildNumber,
                buildDetailResponse.getBuildExecutionStatus(),
                buildDetailResponse.buildDuration,
                buildTimestamp,
                buildDetailResponse.link.href,
                stages,
                buildDetailResponse.changes.change.map {
                    Commit(it.changesetId, mapDateToTimeStamp(it.date), it.date.toString(), it.comment)
                }
            )
            logger.info("Bamboo converting: Build converted result: [$build]")
            return build
        } catch (e: Exception) {
            logger.error("Converting Bamboo DTO failed, DTO: [$buildDetailResponse], exception: [$e]")
            throw e
        }
    }

    private fun getBuildDetails(
        pipeline: Pipeline, planKey: String, buildNumber: Int,
        entity: HttpEntity<String>
    ): BuildDetailDTO {
        val url = "${getDomain(pipeline.url)}/rest/api/latest/result/${planKey}-${buildNumber}.json?" +
                "expand=changes.change,stages.stage.results"
        logger.info("Get build details - Sending request to [$url] with entity [$entity]")
        val responseEntity: ResponseEntity<BuildDetailDTO>
        try {
            responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity)
        } catch (e: Exception) {
            logger.error("Getting Bamboo build details failed, URL: [$url], exception: [$e]")
            throw e
        }

        logger.info("Get build details - Response from [$url]: $responseEntity")
        return responseEntity.body!!
    }

    private fun getMaxBuildNumber(pipeline: Pipeline, planKey: String, entity: HttpEntity<String>): Int {
        val url = "${getDomain(pipeline.url)}/rest/api/latest/result/${planKey}.json"
        logger.info("Get max build number - Sending request to [$url] with entity [$entity]")
        val response = restTemplate.exchange<BuildSummaryDTO>(url, HttpMethod.GET, entity)
        logger.info("Get max build number - Response from [$url]: $response")
        val buildSummaryDTO = response.body!!
        return buildSummaryDTO.results.result.first().buildNumber
    }

    private fun getBuildTimestamp(buildDetailResponse: BuildDetailDTO): Long? {
        if (buildDetailResponse.buildStartedTime != null) {
            return mapDateToTimeStamp(buildDetailResponse.buildStartedTime!!)
        } else if (buildDetailResponse.buildCompletedTime != null) {
            return mapDateToTimeStamp(buildDetailResponse.buildCompletedTime!!)
        }
        return null
    }

    private fun convertToBuildStage(stageDTO: StageDTO): Stage? {
        logger.info("Bamboo converting: Started converting StageDTO [$stageDTO]")
        val isInProgress = stageDTO.state == "Unknown" ||
                stageDTO.results.result.any {
                    it.buildStartedTime == null || it.buildCompletedTime == null || it.buildDuration == null
                }
        val shouldSkip = stageDTO.results.result.isEmpty() || isInProgress
        if (shouldSkip) {
            return null
        }

        val startTimeMillis = stageDTO.results.result
            .map { result -> mapDateToTimeStamp(result.buildStartedTime!!) }
            .minOrNull()!!
        val completedTimeMillis = stageDTO.results.result
            .map { result -> mapDateToTimeStamp(result.buildCompletedTime!!) }
            .maxOrNull()!!
        val durationMillis: Long = completedTimeMillis - startTimeMillis
        val stage = Stage(
            stageDTO.name,
            stageDTO.getStageExecutionStatus(),
            startTimeMillis,
            durationMillis,
            0,
            completedTimeMillis
        )
        logger.info("Bamboo converting: Stage converted result: [$stage]")
        return stage
    }
}
