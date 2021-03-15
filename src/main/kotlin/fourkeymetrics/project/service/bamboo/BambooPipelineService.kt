package fourkeymetrics.project.service.bamboo

import fourkeymetrics.common.model.Build
import fourkeymetrics.common.model.Commit
import fourkeymetrics.common.model.Stage
import fourkeymetrics.common.model.Status
import fourkeymetrics.exception.ApplicationException
import fourkeymetrics.project.model.Pipeline
import fourkeymetrics.project.repository.BuildRepository
import fourkeymetrics.project.repository.PipelineRepository
import fourkeymetrics.project.service.PipelineService
import fourkeymetrics.project.service.bamboo.dto.BuildDetailDTO
import fourkeymetrics.project.service.bamboo.dto.BuildSummaryDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import java.net.URL
import java.time.ZonedDateTime

private const val HTTP_DEFAULT_PORT = 80

@Service("bambooPipelineService")
class BambooPipelineService(
        @Autowired private var restTemplate: RestTemplate,
        @Autowired private var pipelineRepository: PipelineRepository,
        @Autowired private var buildRepository: BuildRepository
) : PipelineService() {

    override fun verifyPipelineConfiguration(pipeline: Pipeline) {
        val headers = setAuthHeader(pipeline.credential)
        val entity = HttpEntity<String>(headers)
        try {
            val url = URL(pipeline.url)
            val port = if (url.port == -1) {
                HTTP_DEFAULT_PORT
            } else {
                url.port
            }
            restTemplate.exchange<String>(
                    "http://${url.host}:${port}/rest/api/latest/project/", HttpMethod.GET, entity
            )
        } catch (ex: HttpServerErrorException) {
            throw ApplicationException(HttpStatus.SERVICE_UNAVAILABLE, "Verify website unavailable")
        } catch (ex: HttpClientErrorException) {
            throw ApplicationException(HttpStatus.BAD_REQUEST, "Verify failed")
        }
    }

    override fun syncBuilds(pipelineId: String): List<Build> {
        val pipeline = pipelineRepository.findById(pipelineId)
        val credential = pipeline.credential
        val headers = setAuthHeader(credential)
        val entity = HttpEntity<String>(headers)

        try {
            val url = URL(pipeline.url)
            val planKey = url.path.split("/").last()
            val port = if (url.port == -1) {
                HTTP_DEFAULT_PORT
            } else {
                url.port
            }
            val response = restTemplate.exchange<BuildSummaryDTO>(
                    "http://${url.host}:${port}/rest/api/latest/result/${planKey}.json", HttpMethod.GET, entity
            )
            val buildSummaryDTO = response.body!!
            val maxBuildNumber = buildSummaryDTO.results.result.first().buildNumber


            val builds = (1..maxBuildNumber).map { i ->
                val buildDetailResponse = restTemplate.exchange<BuildDetailDTO>(
                        "http://${url.host}:${port}/rest/api/latest/result/${planKey}-${i}.json?expand=changes.change,stages.stage.results", HttpMethod.GET, entity
                ).body!!

                Build(
                        pipelineId,
                        buildDetailResponse.buildNumber,
                        mapBuildStatus(buildDetailResponse.buildState),
                        buildDetailResponse.buildDuration,
                        mapDateToTimeStamp(buildDetailResponse.buildStartedTime)!!,
                        buildDetailResponse.link.href,
                        buildDetailResponse.stages.stage.map {
                            val startTimeMillis = it.results.result.mapNotNull { result -> mapDateToTimeStamp(result.buildStartedTime) }.minOrNull()
                            val completedTimeMillis = it.results.result.mapNotNull { result -> mapDateToTimeStamp(result.buildCompletedTime) }.maxOrNull()
                            val durationMillis: Long? = if(startTimeMillis != null && completedTimeMillis != null) completedTimeMillis - startTimeMillis else null
                            Stage(
                                    it.name,
                                    mapStageStatus(it.state),
                                    startTimeMillis,
                                    durationMillis,
                                    0,
                                    completedTimeMillis
                            )
                        },
                        buildDetailResponse.changes.change.map {
                            Commit(
                                    it.changesetId,
                                    mapDateToTimeStamp(it.date)!!,
                                    it.date.toString(),
                                    it.comment
                            )
                        }
                )
            }

            buildRepository.save(builds)

            return builds
        } catch (ex: HttpServerErrorException) {
            throw ApplicationException(HttpStatus.SERVICE_UNAVAILABLE, "Verify website unavailable")
        } catch (ex: HttpClientErrorException) {
            throw ApplicationException(HttpStatus.BAD_REQUEST, "Verify failed")
        }

        return emptyList()

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
            "Unknown" -> {
                Status.IN_PROGRESS
            }
            else -> {
                Status.OTHER
            }
    }

    fun mapDateToTimeStamp(date: ZonedDateTime?): Long? {
        if (date == null) {
            return null
        }
        return date.toInstant().toEpochMilli()
    }

    private fun setAuthHeader(credential: String): HttpHeaders {
        val headers = HttpHeaders()
        headers.setBearerAuth(credential)
        return headers
    }
}
