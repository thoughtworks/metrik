package fourkeymetrics.dashboard.service.jenkins

import fourkeymetrics.common.model.Build
import fourkeymetrics.common.model.Commit
import fourkeymetrics.common.model.Stage
import fourkeymetrics.dashboard.repository.BuildRepository
import fourkeymetrics.dashboard.repository.PipelineRepository
import fourkeymetrics.dashboard.service.PipelineService
import fourkeymetrics.dashboard.service.jenkins.dto.BuildDetailsDTO
import fourkeymetrics.dashboard.service.jenkins.dto.BuildSummaryCollectionDTO
import fourkeymetrics.dashboard.service.jenkins.dto.BuildSummaryDTO
import fourkeymetrics.exception.ApplicationException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import java.nio.charset.Charset
import java.util.*
import kotlin.streams.toList

@Service
class JenkinsPipelineService(
    @Autowired private var restTemplate: RestTemplate,
    @Autowired private var pipelineRepository: PipelineRepository,
    @Autowired private var buildRepository: BuildRepository
) : PipelineService() {
    override fun syncBuilds(pipelineId: String): List<Build> {
        val pipeline = pipelineRepository.findById(pipelineId)

        val username = pipeline.username
        val credential = pipeline.credential
        val baseUrl = pipeline.url

        val buildsNeedToSync = getBuildSummariesFromJenkins(username, credential, baseUrl)
            .parallelStream()
            .filter { buildRepository.findByBuildNumber(pipelineId, it.number)?.result == null }
            .toList()

        val builds = buildsNeedToSync.parallelStream().map { buildSummary ->
            val buildDetails = getBuildDetailsFromJenkins(username, credential, baseUrl, buildSummary)

            Build(
                pipelineId,
                buildSummary.number,
                buildSummary.result,
                buildSummary.duration,
                buildSummary.timestamp,
                buildSummary.url,
                constructBuildStages(buildDetails),
                constructBuildCommits(buildSummary).flatten()
            )
        }.toList()

        buildRepository.save(builds)

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
                stageDTO.name, stageDTO.status, stageDTO.startTimeMillis,
                stageDTO.durationMillis, stageDTO.pauseDurationMillis
            )
        }
    }

    private fun getBuildDetailsFromJenkins(
        username: String, credential: String, baseUrl: String,
        buildSummary: BuildSummaryDTO
    ): BuildDetailsDTO {
        val headers = setAuthHeader(username, credential)
        val entity = HttpEntity<String>(headers)
        val buildDetailResponse: ResponseEntity<BuildDetailsDTO> =
            restTemplate.exchange("$baseUrl/${buildSummary.number}/wfapi/describe", HttpMethod.GET, entity)
        return buildDetailResponse.body!!
    }

    private fun getBuildSummariesFromJenkins(
        username: String, credential: String,
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

    override fun verifyPipelineConfiguration(url: String, username: String, credential: String) {
        val headers = setAuthHeader(username, credential)
        val entity = HttpEntity<String>("", headers)
        try {
            val response = restTemplate.exchange<String>(
                "$url/wfapi/", HttpMethod.GET, entity
            )
            if (!response.statusCode.is2xxSuccessful) {
                throw ApplicationException(response.statusCode, response.body!!)
            }
        } catch (ex: HttpServerErrorException) {
            throw ApplicationException(HttpStatus.SERVICE_UNAVAILABLE, "Verify website unavailable")
        } catch (ex: HttpClientErrorException) {
            throw ApplicationException(HttpStatus.BAD_REQUEST, "Verify failed")
        }
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
