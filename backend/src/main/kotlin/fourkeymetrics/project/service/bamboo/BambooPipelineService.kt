package fourkeymetrics.project.service.bamboo

import fourkeymetrics.common.model.Build
import fourkeymetrics.common.model.Commit
import fourkeymetrics.common.model.Stage
import fourkeymetrics.common.model.Status
import fourkeymetrics.common.utlils.RequestUtil.buildHeaders
import fourkeymetrics.common.utlils.RequestUtil.getDomain
import fourkeymetrics.common.utlils.TimeFormatUtil.mapDateToTimeStamp
import fourkeymetrics.exception.ApplicationException
import fourkeymetrics.project.model.Pipeline
import fourkeymetrics.project.repository.BuildRepository
import fourkeymetrics.project.repository.PipelineRepository
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
import kotlin.streams.toList


@Service("bambooPipelineService")
class BambooPipelineService(
    @Autowired private var restTemplate: RestTemplate,
    @Autowired private var pipelineRepository: PipelineRepository,
    @Autowired private var buildRepository: BuildRepository
) : PipelineService() {
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

    override fun syncBuilds(pipelineId: String): List<Build> {
        logger.info("Started data sync for Bamboo pipeline [$pipelineId]")
        val pipeline = pipelineRepository.findById(pipelineId)
        val credential = pipeline.credential
        val headers = buildHeaders(mapOf(Pair("Authorization", "Bearer $credential")))
        val entity = HttpEntity<String>(headers)

        try {
            val planKey = URL(pipeline.url).path.split("/").last()
            val maxBuildNumber = getMaxBuildNumber(pipeline, planKey, entity)

            val buildNumbersToSync = (1..maxBuildNumber).filter {
                val buildInDB = buildRepository.findByBuildNumber(pipelineId, it)
                buildInDB == null || buildInDB.result == Status.IN_PROGRESS
            }
            logger.info("For Bamboo pipeline [$pipelineId] - total build number is [$maxBuildNumber], " +
                    "[${buildNumbersToSync.size}] of them need to be synced")

            val builds = buildNumbersToSync.parallelStream().map { buildNumber ->
                val buildDetailResponse = getBuildDetails(pipeline, planKey, buildNumber, entity)
                convertToBuild(buildDetailResponse, pipelineId)
            }.toList()

            logger.info("For Bamboo pipeline [$pipelineId] - Successfully synced [${builds.size}] builds")
            buildRepository.save(builds)

            return builds
        } catch (ex: HttpServerErrorException) {
            throw ApplicationException(HttpStatus.SERVICE_UNAVAILABLE, "Verify website unavailable")
        } catch (ex: HttpClientErrorException) {
            throw ApplicationException(HttpStatus.BAD_REQUEST, "Verify failed")
        }
    }

    override fun mapStageStatus(statusInPipeline: String?): Status =
        when (statusInPipeline) {
            "Successful" -> {
                Status.SUCCESS
            }
            "Failed" -> {
                Status.FAILED
            }
            else -> {
                Status.OTHER
            }
        }

    override fun mapBuildStatus(statusInPipeline: String?): Status =
        when (statusInPipeline) {
            "Successful" -> {
                Status.SUCCESS
            }
            "Failed" -> {
                Status.FAILED
            }
            else -> {
                Status.OTHER
            }
        }

    private fun convertToBuild(buildDetailResponse: BuildDetailDTO, pipelineId: String): Build {
        logger.debug(
            "Bamboo converting: Started converting BuildDetailDTO " +
                    "[$buildDetailResponse] for pipeline [$pipelineId]"
        )
        try {
            val buildTimestamp = getBuildTimestamp(buildDetailResponse)
            val stages = buildDetailResponse.stages.stage.mapNotNull {
                convertToBuildStage(it)
            }

            val build = Build(
                pipelineId,
                buildDetailResponse.buildNumber,
                mapBuildStatus(buildDetailResponse.buildState, buildDetailResponse.stages.stage),
                buildDetailResponse.buildDuration,
                buildTimestamp,
                buildDetailResponse.link.href,
                stages,
                buildDetailResponse.changes.change.map {
                    Commit(it.changesetId, mapDateToTimeStamp(it.date), it.date.toString(), it.comment)
                }
            )
            logger.debug("Bamboo converting: Build converted result: [$build]")
            return build
        } catch (e: Exception) {
            logger.error("Converting Bamboo DTO failed, DTO: [$buildDetailResponse], exception: [$e]")
            throw e
        }
    }

    private fun mapBuildStatus(statusInPipeline: String, stageDTOs: List<StageDTO>): Status {
        return if (stageDTOs.any { it.state == "Unknown" }) Status.IN_PROGRESS else mapBuildStatus(statusInPipeline)
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

    private fun getBuildTimestamp(buildDetailResponse: BuildDetailDTO): Long {
        if (buildDetailResponse.buildStartedTime != null) {
            return mapDateToTimeStamp(buildDetailResponse.buildStartedTime!!)
        }
        return mapDateToTimeStamp(buildDetailResponse.buildCompletedTime!!)
    }

    private fun convertToBuildStage(stageDTO: StageDTO): Stage? {
        logger.debug("Bamboo converting: Started converting StageDTO [$stageDTO]")
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
            mapStageStatus(stageDTO.state),
            startTimeMillis,
            durationMillis,
            0,
            completedTimeMillis
        )
        logger.debug("Bamboo converting: Stage converted result: [$stage]")
        return stage
    }
}
