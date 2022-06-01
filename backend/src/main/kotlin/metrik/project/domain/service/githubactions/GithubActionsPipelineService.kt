package metrik.project.domain.service.githubactions

import feign.FeignException.FeignClientException
import feign.FeignException.FeignServerException
import metrik.project.domain.model.Execution
import metrik.project.domain.model.Commit
import metrik.project.domain.model.PipelineConfiguration
import metrik.project.domain.repository.BuildRepository
import metrik.project.domain.service.PipelineService
import metrik.project.exception.PipelineConfigVerifyException
import metrik.project.exception.SynchronizationException
import metrik.project.infrastructure.github.feign.GithubFeignClient
import metrik.project.rest.vo.response.SyncProgress
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.net.URL
import java.util.concurrent.atomic.AtomicInteger

@Service("githubActionsPipelineService")
class GithubActionsPipelineService(
    private val buildRepository: BuildRepository,
    private val githubExecutionConverter: GithubExecutionConverter,
    private val githubFeignClient: GithubFeignClient,
    private val githubActionsPipelineRunService: GithubActionsPipelineRunService,
    private val githubActionsPipelineCommitService: GithubActionsPipelineCommitService
) : PipelineService {
    private var logger = LoggerFactory.getLogger(javaClass.name)

    override fun verifyPipelineConfiguration(pipeline: PipelineConfiguration) {
        logger.info(
            "Started verification for Github Actions pipeline [name: ${pipeline.name}, url: ${pipeline.url}, " +
                    "type: ${pipeline.type}]"
        )

        try {
            val (owner, repo) = getOwnerRepoFromUrl(pipeline.url)
            githubFeignClient.retrieveMultipleRuns(pipeline.credential, owner, repo)
                ?: throw PipelineConfigVerifyException("Verify failed")
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
    override fun syncBuildsProgressively(pipeline: PipelineConfiguration, emitCb: (SyncProgress) -> Unit)
            : List<Execution> {
        logger.info("Started data sync for Github Actions pipeline [name: ${pipeline.name}, url: ${pipeline.url}]")

        val progressCounter = AtomicInteger(0)

        try {

            var newRuns = githubActionsPipelineRunService.getNewRuns(pipeline)

            githubActionsPipelineRunService.getProgressRuns(pipeline, newRuns)

            val totalBuildNumbersToSync = newRuns.size

            logger.info(
                "For Github Actions pipeline [${pipeline.id}] - " + "[$totalBuildNumbersToSync] need to be synced"
            )

            val branchRunCommitsMap = githubActionsPipelineCommitService.mapCommitToRun(pipeline, newRuns)

            val builds = newRuns.map {

                val commits: List<Commit> = branchRunCommitsMap[it.branch]!![it]!!
                val convertedBuild = githubExecutionConverter.convertToBuild(it, pipeline.id, commits)

                buildRepository.save(convertedBuild)

                emitCb(
                    SyncProgress(
                        pipeline.id,
                        pipeline.name,
                        progressCounter.incrementAndGet(),
                        totalBuildNumbersToSync
                    )
                )

                logger.info("[${pipeline.id}] sync progress: [${progressCounter.get()}/$totalBuildNumbersToSync]")
                convertedBuild
            }

            logger.info(
                "For Github Actions pipeline [${pipeline.id}] - Successfully synced [$totalBuildNumbersToSync] builds"
            )

            return builds
        } catch (ex: FeignServerException) {
            throw SynchronizationException("Connection to Github Actions is unavailable")
        } catch (ex: FeignClientException) {
            throw SynchronizationException("Syncing Github Commits details failed")
        }
    }


    private fun getOwnerRepoFromUrl(url: String): Pair<String, String> {
        val components = URL(url).path.split("/")
        val owner = components[components.size - ownerIndex]
        val repo = components.last()
        return Pair(owner, repo)
    }

    private companion object {
        const val ownerIndex = 2
    }
}
