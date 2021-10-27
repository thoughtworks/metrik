package metrik.project.domain.service.jenkins

import feign.FeignException.FeignClientException
import feign.FeignException.FeignServerException
import metrik.project.domain.model.*
import metrik.project.domain.repository.BuildRepository
import metrik.project.domain.service.PipelineService
import metrik.project.exception.PipelineConfigVerifyException
import metrik.project.infrastructure.jenkins.feign.JenkinsFeignClient
import metrik.project.rest.vo.response.SyncProgress
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.net.URI
import java.nio.charset.Charset
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.streams.toList

@Service("jenkinsPipelineService")
class JenkinsPipelineService(
    @Autowired private var buildRepository: BuildRepository,
    @Autowired private var jenkinsFeignClient: JenkinsFeignClient
) : PipelineService {
    private var logger = LoggerFactory.getLogger(this.javaClass.name)

    override fun verifyPipelineConfiguration(pipeline: PipelineConfiguration) {
        val headers = getAuth(pipeline.username!!, pipeline.credential)
        val url = URI.create(pipeline.url)
        try {
            jenkinsFeignClient.verifyJenkinsUrl(url, headers)
        } catch (ex: FeignServerException) {
            throw PipelineConfigVerifyException("Verify website unavailable")
        } catch (ex: FeignClientException) {
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

    override fun syncBuildsProgressively(pipeline: PipelineConfiguration, emitCb: (SyncProgress) -> Unit)
    : List<Execution> {
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

            val convertedExecution = Execution(
                pipeline.id,
                buildSummary.number,
                buildSummary.getBuildExecutionStatus(),
                buildSummary.duration,
                buildSummary.timestamp,
                buildSummary.url,
                stages = constructBuildStages(buildDetails),
                changeSets = constructBuildCommits(buildSummary).flatten()
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
            convertedExecution
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
                Commit(commitDTO.commitId, commitDTO.timestamp, commitDTO.date)
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
        val headers = getAuth(username, credential)
        val url = URI.create(baseUrl)

        return jenkinsFeignClient.retrieveBuildDetailsFromJenkins(url, headers, buildSummary.number)!!
    }

    private fun getBuildSummariesFromJenkins(
        username: String,
        credential: String,
        baseUrl: String
    ): List<BuildSummaryDTO> {
        val headers = getAuth(username, credential)
        val url = URI.create(baseUrl)

        return jenkinsFeignClient.retrieveBuildSummariesFromJenkins(url, headers)!!.allBuilds
    }

    private fun getAuth(username: String, credential: String): String {
        val auth = "$username:$credential"
        val encodedAuth = Base64.getEncoder().encodeToString(auth.toByteArray(Charset.forName("UTF-8")))
        return "Basic $encodedAuth"
    }
}
