package fourkeymetrics.project.service.bamboo

import fourkeymetrics.common.model.Build
import fourkeymetrics.common.model.Commit
import fourkeymetrics.common.model.Stage
import fourkeymetrics.common.model.Status
import fourkeymetrics.common.utlils.RequestUtil.buildBearerHeader
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
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import java.net.URL


@Service("bambooPipelineService")
class BambooPipelineService(
        @Autowired private var restTemplate: RestTemplate,
        @Autowired private var pipelineRepository: PipelineRepository,
        @Autowired private var buildRepository: BuildRepository
) : PipelineService() {

    override fun verifyPipelineConfiguration(pipeline: Pipeline) {
        val headers = buildBearerHeader(pipeline.credential)
        val entity = HttpEntity<String>(headers)

        try {
            restTemplate.exchange<String>(
                    "${getDomain(pipeline.url)}/rest/api/latest/project/", HttpMethod.GET, entity)
        } catch (ex: HttpServerErrorException) {
            throw ApplicationException(HttpStatus.SERVICE_UNAVAILABLE, "Verify website unavailable")
        } catch (ex: HttpClientErrorException) {
            throw ApplicationException(HttpStatus.BAD_REQUEST, "Verify failed")
        }
    }

    override fun syncBuilds(pipelineId: String): List<Build> {
        val pipeline = pipelineRepository.findById(pipelineId)
        val credential = pipeline.credential
        val headers = buildBearerHeader(credential)
        val entity = HttpEntity<String>(headers)

        try {
            val planKey = URL(pipeline.url).path.split("/").last()
            val maxBuildNumber = getMaxBuildNumber(pipeline, planKey, entity)

            val buildsToSync = (1..maxBuildNumber).filter {
                val buildInDB = buildRepository.findByBuildNumber(pipelineId, it)
                buildInDB == null || buildInDB.result == Status.IN_PROGRESS
            }

            val builds = buildsToSync.map { i ->
                val buildDetailResponse = getBuildDetails(pipeline, planKey, i, entity)
                convertToBuild(buildDetailResponse, pipelineId)
            }

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
                "Unknown" -> {
                    Status.IN_PROGRESS
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


    private fun mapBuildStatus(statusInPipeline: String?, stages: List<Stage>): Status {
        return if (stages.any { it.status == Status.IN_PROGRESS }) Status.IN_PROGRESS
            else mapBuildStatus(statusInPipeline)
    }


    private fun convertToBuild(buildDetailResponse: BuildDetailDTO, pipelineId: String): Build {
        val buildTimestamp = getBuildTimestamp(buildDetailResponse)
        val stages = buildDetailResponse.stages.stage.map {
            convertToBuildStage(it)
        }

        return Build(
                pipelineId,
                buildDetailResponse.buildNumber,
                mapBuildStatus(buildDetailResponse.buildState, stages),
                buildDetailResponse.buildDuration,
                buildTimestamp,
                buildDetailResponse.link.href,
                stages,
                buildDetailResponse.changes.change.map {
                    Commit(it.changesetId, mapDateToTimeStamp(it.date)!!, it.date.toString(), it.comment)
                }
        )
    }

    private fun getBuildDetails(pipeline: Pipeline, planKey: String, buildNumber: Int,
                                entity: HttpEntity<String>): BuildDetailDTO {
        return restTemplate.exchange<BuildDetailDTO>(
                "${getDomain(pipeline.url)}/rest/api/latest/result/${planKey}-${buildNumber}.json?" +
                        "expand=changes.change,stages.stage.results",
                HttpMethod.GET, entity
        ).body!!
    }

    private fun getMaxBuildNumber(pipeline: Pipeline, planKey: String, entity: HttpEntity<String>): Int {
        val response = restTemplate.exchange<BuildSummaryDTO>(
                "${getDomain(pipeline.url)}/rest/api/latest/result/${planKey}.json", HttpMethod.GET, entity
        )
        val buildSummaryDTO = response.body!!
        return buildSummaryDTO.results.result.first().buildNumber
    }

    private fun getBuildTimestamp(buildDetailResponse: BuildDetailDTO) =
            if (mapDateToTimeStamp(buildDetailResponse.buildStartedTime) == null)
                mapDateToTimeStamp(buildDetailResponse.buildCompletedTime)!!
            else mapDateToTimeStamp(buildDetailResponse.buildStartedTime)!!

    private fun convertToBuildStage(stageDTO: StageDTO): Stage {
        val startTimeMillis = stageDTO.results.result
                .mapNotNull { result -> mapDateToTimeStamp(result.buildStartedTime) }
                .minOrNull()
        val completedTimeMillis = stageDTO.results.result
                .mapNotNull { result -> mapDateToTimeStamp(result.buildCompletedTime) }
                .maxOrNull()
        val durationMillis: Long? =
                if (startTimeMillis != null && completedTimeMillis != null)
                    completedTimeMillis - startTimeMillis
                else
                    null
        return Stage(
                stageDTO.name,
                mapStageStatus(stageDTO.state),
                startTimeMillis,
                durationMillis,
                0,
                completedTimeMillis
        )
    }
}
