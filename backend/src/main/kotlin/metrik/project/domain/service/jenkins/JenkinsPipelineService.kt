package metrik.project.domain.service.jenkins

import metrik.project.domain.model.Build
import metrik.project.domain.model.Commit
import metrik.project.domain.model.Pipeline
import metrik.project.domain.model.Stage
import metrik.project.domain.model.Status
import metrik.project.domain.repository.BuildRepository
import metrik.project.domain.service.PipelineService
import metrik.project.domain.service.jenkins.dto.BuildDetailsDTO
import metrik.project.domain.service.jenkins.dto.BuildSummaryCollectionDTO
import metrik.project.domain.service.jenkins.dto.BuildSummaryDTO
import metrik.project.exception.PipelineConfigVerifyException
import metrik.project.rest.vo.response.SyncProgress
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import java.nio.charset.Charset
import java.util.Base64
import java.util.concurrent.atomic.AtomicInteger
import kotlin.streams.toList

@Service("jenkinsPipelineService")
class JenkinsPipelineService(
    @Autowired private var restTemplate: RestTemplate,
    @Autowired private var buildRepository: BuildRepository
) : PipelineService {
    private var logger = LoggerFactory.getLogger(this.javaClass.name)

    override fun verifyPipelineConfiguration(pipeline: Pipeline) {
        val headers = setAuthHeader(pipeline.username!!, pipeline.credential)
        val entity = HttpEntity<String>("", headers)
        try {
            val response = restTemplate.exchange<String>(
                "${pipeline.url}/wfapi/", HttpMethod.GET, entity
            )
            if (!response.statusCode.is2xxSuccessful) {
                logger.error(
                    """
                    Failed to verify pipeline config for [${pipeline.url}]
                    statusCode: ${response.statusCode}
                    responseBody: ${response.body}          
                    """.trimIndent()
                )
                throw PipelineConfigVerifyException(response.body!!)
            }
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

    override fun syncBuildsProgressively(pipeline: Pipeline, emitCb: (SyncProgress) -> Unit): List<Build> {
        logger.info("Started data sync for Jenkins pipeline [$pipeline.id]")
        val progressCounter = AtomicInteger(0)

        val buildsNeedToSync = getBuildSummariesFromJenkins(pipeline.username!!, pipeline.credential, pipeline.url)
            .parallelStream()
            .filter {
                val buildInDB = buildRepository.getByBuildNumber(pipeline.id, it.number)
                buildInDB == null || buildInDB.result == Status.IN_PROGRESS
            }
            .toList()

        logger.info(
            "For Jenkins pipeline [${pipeline.id}] - found [${buildsNeedToSync.size}] builds need to be synced"
        )
        val builds = buildsNeedToSync.parallelStream().map { buildSummary ->
            val buildDetails =
                getBuildDetailsFromJenkins(pipeline.username!!, pipeline.credential, pipeline.url, buildSummary)

            val convertedBuild = Build(
                pipeline.id,
                buildSummary.number,
                buildSummary.getBuildExecutionStatus(),
                buildSummary.duration,
                buildSummary.timestamp,
                buildSummary.url,
                constructBuildStages(buildDetails),
                constructBuildCommits(buildSummary).flatten()
            )

            emitCb(
                SyncProgress(
                    pipeline.id,
                    pipeline.name,
                    progressCounter.incrementAndGet(),
                    buildsNeedToSync.size
                )
            )
            logger.info("[${pipeline.id}] sync progress: [${progressCounter.get()}/${buildsNeedToSync.size}]")
            convertedBuild
        }.toList()

        buildRepository.save(builds)

        logger.info(
            "For Jenkins pipeline [${pipeline.id}] - Successfully synced [${buildsNeedToSync.size}] builds"
        )
        return builds
    }

    private fun constructBuildCommits(buildSummary: BuildSummaryDTO): List<List<Commit>> {
        return buildSummary.changeSets.map { changeSetDTO ->
            changeSetDTO.items.map { commitDTO ->
                Commit(commitDTO.commitId, commitDTO.timestamp, commitDTO.date, commitDTO.msg)
            }
        }
    }

    private fun constructBuildStages(buildDetails: BuildDetailsDTO): List<Stage> {
        return buildDetails.stages.map { stageDTO ->
            Stage(
                stageDTO.name,
                stageDTO.getStageExecutionStatus(),
                stageDTO.startTimeMillis,
                stageDTO.durationMillis,
                stageDTO.pauseDurationMillis
            )
        }
    }

    private fun getBuildDetailsFromJenkins(
        username: String,
        credential: String,
        baseUrl: String,
        buildSummary: BuildSummaryDTO
    ): BuildDetailsDTO {
        val headers = setAuthHeader(username, credential)
        val entity = HttpEntity<String>(headers)
        val buildDetailResponse: ResponseEntity<BuildDetailsDTO> =
            restTemplate.exchange("$baseUrl/${buildSummary.number}/wfapi/describe", HttpMethod.GET, entity)
        return buildDetailResponse.body!!
    }

    private fun getBuildSummariesFromJenkins(
        username: String,
        credential: String,
        baseUrl: String
    ): List<BuildSummaryDTO> {
        val headers = setAuthHeader(username, credential)
        val entity = HttpEntity<String>(headers)
        val allBuildsResponse: ResponseEntity<BuildSummaryCollectionDTO> =
            restTemplate.exchange(
                "$baseUrl/api/json?tree=allBuilds[building,number," +
                    "result,timestamp,duration,url,changeSets[items[commitId,timestamp,msg,date]]]",
                HttpMethod.GET,
                entity
            )
        return allBuildsResponse.body!!.allBuilds
    }

    private fun setAuthHeader(username: String, credential: String): HttpHeaders {
        val headers = HttpHeaders()
        val auth = "$username:$credential"
        val encodedAuth = Base64.getEncoder().encodeToString(auth.toByteArray(Charset.forName("UTF-8")))
        val authHeader = "Basic $encodedAuth"
        headers.set("Authorization", authHeader)
        return headers
    }
}
