package metrik.project.domain.service.githubactions

import feign.FeignException.FeignClientException
import feign.FeignException.FeignServerException
import metrik.infrastructure.utlils.toTimestamp
import metrik.project.domain.model.Build
import metrik.project.domain.model.Commit
import metrik.project.domain.model.Pipeline
import metrik.project.domain.repository.BuildRepository
import metrik.project.domain.service.PipelineService
import metrik.project.exception.PipelineConfigVerifyException
import metrik.project.exception.SynchronizationException
import metrik.project.infrastructure.github.GithubClient
import metrik.project.rest.vo.response.SyncProgress
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.net.URL
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.atomic.AtomicInteger

@Service("githubActionsPipelineService")
class GithubActionsPipelineService(
    private val githubActionsCommitService: GithubActionsCommitService,
    private val buildRepository: BuildRepository,
    private val githubBuildConverter: GithubBuildConverter,
    private val githubClient: GithubClient
) : PipelineService {
    private var logger = LoggerFactory.getLogger(javaClass.name)

    override fun verifyPipelineConfiguration(pipeline: Pipeline) {
        logger.info("Started verification for Github Actions pipeline [$pipeline]")

        try {
            githubClient.verifyGithubUrl(pipeline.url, pipeline.credential)
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
    override fun syncBuildsProgressively(pipeline: Pipeline, emitCb: (SyncProgress) -> Unit): List<Build> {
        logger.info("Started data sync for Github Actions pipeline [$pipeline.id]")

        val progressCounter = AtomicInteger(0)

        try {

            val latestTimestamp = when (
                val latestBuild = buildRepository.getLatestBuild(pipeline.id)
            ) {
                null -> Long.MIN_VALUE
                else -> latestBuild.timestamp
            }

            val newRuns = getNewRuns(pipeline, latestTimestamp)

            val inProgressBuilds = buildRepository.getInProgressBuilds(pipeline.id)
            val inProgressRuns = getInProgressRuns(pipeline, inProgressBuilds)

            newRuns.addAll(inProgressRuns)

            val totalBuildNumbersToSync = newRuns.size

            logger.info(
                "For Github Actions pipeline [${pipeline.id}] - " + "[$totalBuildNumbersToSync] need to be synced"
            )

            val mapToCommits = mapCommitToRun(pipeline, newRuns)

            val builds = newRuns.map {

                val commits: List<Commit> = mapToCommits[it.branch]!![it]!!
                val convertedBuild = githubBuildConverter.convertToBuild(it, pipeline.id, commits)

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

    fun getNewRuns(
        pipeline: Pipeline,
        latestTimestamp: Long,
        maxPerPage: Int = defaultMaxPerPage
    ): MutableList<GithubActionsRun> {
        var ifRetrieving = true
        var pageIndex = 1

        val totalRuns = mutableListOf<GithubActionsRun>()

        while (ifRetrieving) {

            logger.info(
                "Get Github Runs - " +
                        "Sending request to Github Feign Client with url: ${pipeline.url}, pageIndex: $pageIndex"
            )

            val runs = githubClient.retrieveMultipleRuns(
                pipeline.url, pipeline.credential, maxPerPage, pageIndex
            ) ?: break

            val runsNeedToSync = runs.filter { it.createdTimestamp.toTimestamp() > latestTimestamp }

            ifRetrieving = runsNeedToSync.size == maxPerPage

            totalRuns.addAll(runsNeedToSync)

            pageIndex++
        }
        return totalRuns
    }

    fun mapCommitToRun(
        pipeline: Pipeline,
        runs: MutableList<GithubActionsRun>
    ): Map<String, Map<GithubActionsRun, List<Commit>>> {
        val branchCommitsMap: MutableMap<String, Map<GithubActionsRun, List<Commit>>> = mutableMapOf()
        runs.groupBy { it.branch }
            .forEach { (branch, run) -> branchCommitsMap[branch] = mapRunToCommits(pipeline, run) }
        return branchCommitsMap.toMap()
    }

    private fun getInProgressRuns(
        pipeline: Pipeline,
        builds: List<Build>,
    ): MutableList<GithubActionsRun> {
        val runs = mutableListOf<GithubActionsRun>()

        builds.forEach { build ->
            run {
                val runId = URL(build.url).path.split("/").last()

                logger.info(
                    "Get Github Runs - " +
                            "Sending request to Github Feign Client with owner: ${pipeline.url}, runId: $runId"
                )

                val run = githubClient.retrieveSingleRun(pipeline.url, pipeline.credential, runId)

                run?.also { runs.add(it) }
            }
        }

        return runs
    }

    private fun mapRunToCommits(pipeline: Pipeline, runs: List<GithubActionsRun>): Map<GithubActionsRun, List<Commit>> {
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
        val allCommits = githubActionsCommitService.getCommitsBetweenBuilds(
            previousRunZonedDateTime?.plus(COMMIT_OFFSET, ChronoUnit.SECONDS),
            latestTimestampInRuns,
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
        const val defaultMaxPerPage = 100
        const val COMMIT_OFFSET: Long = 1
    }
}
