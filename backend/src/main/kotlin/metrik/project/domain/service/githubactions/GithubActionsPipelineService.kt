package metrik.project.domain.service.githubactions

import feign.FeignException.FeignClientException
import feign.FeignException.FeignServerException
import metrik.infrastructure.utlils.toTimestamp
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
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.streams.toList

@Service("githubActionsPipelineService")
class GithubActionsPipelineService(
    private val githubCommitService: GithubCommitService,
    private val githubRunService: GithubRunService,
    private val buildRepository: BuildRepository,
    private val githubExecutionConverter: GithubExecutionConverter,
    private val githubFeignClient: GithubFeignClient
) : PipelineService {
    private var logger = LoggerFactory.getLogger(javaClass.name)

    override fun verifyPipelineConfiguration(pipeline: PipelineConfiguration) {
        logger.info("Started verification for Github Actions pipeline [name: ${pipeline.name}, url: ${pipeline.url}, " +
                "type: ${pipeline.type}]")

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

            val latestTimestamp = when (
                val latestBuild = buildRepository.getLatestBuild(pipeline.id)
            ) {
                null -> Long.MIN_VALUE
                else -> latestBuild.timestamp
            }

            val newRuns = githubRunService.syncRunsByPage(pipeline, latestTimestamp)

            val inProgressRuns = buildRepository.getInProgressBuilds(pipeline.id)
                .parallelStream()
                .map { githubRunService.syncSingleRun(pipeline, it.url) }
                .toList()
                .filterNotNull()

            newRuns.addAll(inProgressRuns)

            val totalBuildNumbersToSync = newRuns.size

            logger.info(
                "For Github Actions pipeline [${pipeline.id}] - " + "[$totalBuildNumbersToSync] need to be synced"
            )

            val branchRunCommitsMap = mapCommitToRun(pipeline, newRuns)

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

    fun mapCommitToRun(
        pipeline: PipelineConfiguration,
        runs: MutableList<GithubActionsRun>
    ): Map<String, Map<GithubActionsRun, List<Commit>>> {
        val branchCommitsMap: MutableMap<String, Map<GithubActionsRun, List<Commit>>> = mutableMapOf()
        runs.groupBy { it.branch }
            .forEach { (branch, run) -> branchCommitsMap[branch] = mapRunToCommits(pipeline, run) }
        return branchCommitsMap.toMap()
    }

    private fun mapRunToCommits(pipeline: PipelineConfiguration, runs: List<GithubActionsRun>)
    : Map<GithubActionsRun, List<Commit>> {
        val map: MutableMap<GithubActionsRun, List<Commit>> = mutableMapOf()

        runs.sortedByDescending { it.commitTimeStamp }

        val latestTimestampInRuns = runs.first().commitTimeStamp
        val lastRun = runs.last()
        val previousRunBeforeLastRun = buildRepository.getPreviousBuild(
            pipeline.id,
            lastRun.commitTimeStamp.toTimestamp(),
            lastRun.branch
        )?.let { it.changeSets.firstOrNull()?.timestamp }
        val previousRunZonedDateTime = previousRunBeforeLastRun?.let {
            ZonedDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneOffset.UTC)
        }
        val allCommits = githubCommitService.getCommitsBetweenTimePeriod(
            previousRunZonedDateTime?.plus(COMMIT_OFFSET, ChronoUnit.SECONDS)?.toTimestamp() ?: 0,
            latestTimestampInRuns.toTimestamp(),
            branch = lastRun.branch,
            pipeline = pipeline,
        )

        runs.forEachIndexed { index, run ->
            val currentRunTimeStamp = run.commitTimeStamp.toTimestamp()
            val previousBuildInRepo = buildRepository.getPreviousBuild(
                pipeline.id,
                run.createdTimestamp.toTimestamp(),
                run.branch
            )?.let { it.changeSets.firstOrNull()?.timestamp }
            val lastRunTimeStamp = when (index) {
                runs.lastIndex -> previousRunBeforeLastRun
                else -> {
                    val previousRunTimeStamp = runs[index + 1].commitTimeStamp.toTimestamp()
                    if (previousBuildInRepo == null) previousRunTimeStamp
                    else maxOf(previousRunTimeStamp, previousBuildInRepo)
                }
            }
            val commits = when (lastRunTimeStamp) {
                null -> allCommits.filter { it.timestamp <= currentRunTimeStamp }
                else -> allCommits.filter { it.timestamp in (lastRunTimeStamp + 1)..currentRunTimeStamp }
            }
            map[run] = commits
        }
        return map.toMap()
    }

    private companion object {
        const val COMMIT_OFFSET: Long = 1
        const val ownerIndex = 2
    }


    private fun getOwnerRepoFromUrl(url: String): Pair<String, String> {
        val components = URL(url).path.split("/")
        val owner = components[components.size - ownerIndex]
        val repo = components.last()
        return Pair(owner, repo)
    }
}
