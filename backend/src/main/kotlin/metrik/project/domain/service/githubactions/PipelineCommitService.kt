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
import java.util.concurrent.atomic.AtomicInteger

@Service("githubActionsPipelineCommitsService")
class PipelineCommitService(
    private val buildRepository: BuildRepository,
    private val commitService: CommitService
) {
    fun mapCommitToRun(
        pipeline: PipelineConfiguration,
        runs: MutableList<GithubActionsRun>,
        emitCb: (SyncProgress) -> Unit
    ): Map<String, Map<GithubActionsRun, List<Commit>>> {
        val branchCommitsMap: MutableMap<String, Map<GithubActionsRun, List<Commit>>> = mutableMapOf()
        val runsGroupedByBranch = runs.groupBy { it.branch }
        val numberOfBranches = runsGroupedByBranch.size
        val progressCounter = AtomicInteger(0)

        runsGroupedByBranch
            .forEach { (branch, run) ->
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
                branchCommitsMap[branch] = mapRunToCommits(pipeline, run)
            }
        return branchCommitsMap.toMap()
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
        val previousRunBeforeLastRun = buildRepository.getPreviousBuild(
            pipeline.id,
            lastRun.commitTimeStamp.toTimestamp(),
            lastRun.branch
        )?.let { it.changeSets.firstOrNull()?.timestamp }
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

    private companion object {
        const val COMMIT_OFFSET: Long = 1
    }
}
