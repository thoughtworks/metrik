package fourkeymetrics.pipeline

import fourkeymetrics.dto.AllBuildDTO
import fourkeymetrics.dto.BuildSummaryDTO
import fourkeymetrics.dto.BuildDetailsDTO
import fourkeymetrics.model.Build
import fourkeymetrics.model.Commit
import fourkeymetrics.model.Stage
import fourkeymetrics.repository.DashboardRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import kotlin.streams.toList

@Service
class Jenkins(@Autowired private var restTemplate: RestTemplate,
              @Autowired private var dashboardRepository: DashboardRepository) : Pipeline() {
    override fun fetchAllBuilds(dashboardId: String, pipelineId: String): List<Build> {
        val pipelineConfiguration = dashboardRepository.getPipelineConfiguration(dashboardId, pipelineId)!!
        val username = pipelineConfiguration.username
        val token = pipelineConfiguration.token
        val baseUrl = pipelineConfiguration.url

        val buildSummaries = getBuildSummariesFromJenkins(username, token, baseUrl)

        return buildSummaries.parallelStream().map { buildSummary ->
            val buildDetails = getBuildDetailsFromJenkins(username, token, baseUrl, buildSummary)

            Build(pipelineId,
                buildSummary.number,
                buildSummary.result,
                buildSummary.duration,
                buildSummary.timestamp,
                buildSummary.url,
                constructBuildStages(buildDetails),
                constructBuildCommits(buildSummary).flatten()
            )
        }.toList()
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
            Stage(stageDTO.name, stageDTO.status, stageDTO.startTimeMillis,
                stageDTO.durationMillis, stageDTO.pauseDurationMillis)
        }
    }

    private fun getBuildDetailsFromJenkins(username: String, token: String, baseUrl: String,
                                           buildSummary: BuildSummaryDTO): BuildDetailsDTO {
        val buildDetailResponse = restTemplate.getForEntity(
            "http://$username:$token@$baseUrl/${buildSummary.number}/wfapi/describe",
            BuildDetailsDTO::class.java)
        return buildDetailResponse.body!!
    }

    private fun getBuildSummariesFromJenkins(username: String, token: String,
                                             baseUrl: String): List<BuildSummaryDTO> {
        val allBuildsResponse = restTemplate.getForEntity(
            "http://$username:$token@$baseUrl/api/json?tree=allBuilds[building,number," +
                "result,timestamp,duration,url,changeSets[items[commitId,timestamp,msg,date]]]",
            AllBuildDTO::class.java)
        return allBuildsResponse.body!!.allBuilds
    }
}
