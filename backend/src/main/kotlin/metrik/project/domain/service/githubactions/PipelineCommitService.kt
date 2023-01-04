package metrik.project.domain.service.githubactions

import metrik.infrastructure.utlils.toTimestamp
import metrik.project.constant.GithubActionConstants
import metrik.project.domain.model.Commit
import metrik.project.domain.model.PipelineConfiguration
import metrik.project.domain.repository.BuildRepository
import metrik.project.rest.vo.response.SyncProgress
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.Executors
import java.util.concurrent.FutureTask
import java.util.concurrent.atomic.AtomicInteger

@Service("githubActionsPipelineCommitsService")
class PipelineCommitService(
    private val buildRepository: BuildRepository,
    private val commitService: CommitService
) {
    private val defaultNumberOfBranchFetchConcurrency = 10
    private val executorService = Executors.newFixedThreadPool(defaultNumberOfBranchFetchConcurrency)

    fun mapCommitToRun(
        pipeline: PipelineConfiguration,
        runs: MutableList<GithubActionsRun>,
        emitCb: (SyncProgress) -> Unit
    ): Map<String, Map<GithubActionsRun, List<Commit>>> {
        val branchCommitsMap: MutableMap<String, Map<GithubActionsRun, List<Commit>>> = mutableMapOf()
        val runsGroupedByBranch = runs.groupBy { it.branch }
        val numberOfBranches = runsGroupedByBranch.size
        val progressCounter = AtomicInteger(0)

        val taskMap: MutableMap<String, FutureTask<Map<GithubActionsRun, List<Commit>>>> = mutableMapOf()
        runsGroupedByBranch.forEach { (branch, run) ->
            val branchTask = FutureTask {
                emitCb(
                    SyncProgress(
                        pipeline.id,
                        pipeline.name,
                        progressCounter.incrementAndGet(),
                        numberOfBranches,
                        GithubActionConstants.stepNumberOfFetchingCommits,
                        GithubActionConstants.totalNumberOfSteps,
                    )
                )
                mapRunToCommits(pipeline, run)
            }
            taskMap[branch] = branchTask
            executorService.submit(branchTask)
        }
        runsGroupedByBranch.forEach { (branch, _) ->
            branchCommitsMap[branch] = taskMap[branch]!!.get()
        }

        return cleanupExecutedCommit(pipeline, branchCommitsMap).toMap()
    }

    fun mapRunToCommits(
        pipeline: PipelineConfiguration,
        runs: List<GithubActionsRun>
    ): Map<GithubActionsRun, List<Commit>> {
        val map: MutableMap<GithubActionsRun, List<Commit>> = mutableMapOf()

        val (previousRunBeforeLastRun, allCommits) = getCommitsBetweenPeriodPerBranch(runs, pipeline)

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

    private fun getCommitsBetweenPeriodPerBranch(
        runs: List<GithubActionsRun>,
        pipeline: PipelineConfiguration
    ): Pair<Long?, List<Commit>> {
        runs.sortedByDescending { it.commitTimeStamp }

        val latestTimestampInRuns = runs.first().commitTimeStamp
        val lastRun = runs.last()
        val previousBuild = buildRepository.getPreviousBuild(
            pipeline.id,
            lastRun.commitTimeStamp.toTimestamp(),
            lastRun.branch
        )
        val previousRunBeforeLastRun = previousBuild?.let { it.changeSets.firstOrNull()?.timestamp ?: it.timestamp }
        val previousRunZonedDateTime = previousRunBeforeLastRun?.let {
            ZonedDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneOffset.UTC)
        }
        val allCommits = commitService.getCommitsBetweenTimePeriod(
            previousRunZonedDateTime?.plus(COMMIT_OFFSET, ChronoUnit.SECONDS),
            latestTimestampInRuns,
            branch = lastRun.branch,
            pipeline = pipeline,
        )
        return Pair(previousRunBeforeLastRun, allCommits)
    }

    private fun cleanupExecutedCommit(
        pipeline: PipelineConfiguration,
        branchCommitsMap: MutableMap<String, Map<GithubActionsRun, List<Commit>>>
    ): MutableMap<String, MutableMap<GithubActionsRun, List<Commit>>> {
        val executedCommit = getAllExecutedCommitId(pipeline)

        val fullMapOfRun = mutableMapOf<GithubActionsRun, List<Commit>>()
        branchCommitsMap.values.forEach { fullMapOfRun.putAll(it) }

        val allActionsRun = mutableListOf<GithubActionsRun>()
        allActionsRun.addAll(fullMapOfRun.keys)
        allActionsRun.sortBy { it.commitTimeStamp }

        val result = mutableMapOf<String, MutableMap<GithubActionsRun, List<Commit>>>()
        allActionsRun.forEach { run ->
            val validCommitForCurrentRun = fullMapOfRun[run]!!.filter { !executedCommit.contains(it.commitId) }
            val branchMap = result[run.branch] ?: mutableMapOf()
            branchMap[run] = validCommitForCurrentRun
            result[run.branch] = branchMap
            executedCommit.addAll(validCommitForCurrentRun.map { it.commitId })
        }
        return result
    }

    private fun getAllExecutedCommitId(pipeline: PipelineConfiguration): MutableSet<String> {
        val allChangeSets = this.buildRepository.getAllBuilds(pipelineId = pipeline.id)
            .map { o -> o.changeSets.map { it.commitId } }
        val allPreviousCommitId = mutableSetOf<String>()
        allChangeSets.forEach { allPreviousCommitId.addAll(it) }
        return allPreviousCommitId
    }

    private companion object {
        const val COMMIT_OFFSET: Long = 1
    }
}
