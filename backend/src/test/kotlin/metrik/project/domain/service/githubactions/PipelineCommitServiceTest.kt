package metrik.project.domain.service.githubactions

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import metrik.infrastructure.utlils.toTimestamp
import metrik.project.TestFixture.branch
import metrik.project.TestFixture.commit
import metrik.project.TestFixture.currentTimeStamp
import metrik.project.TestFixture.githubActionsExecution
import metrik.project.TestFixture.githubActionsPipeline
import metrik.project.TestFixture.githubActionsRun1
import metrik.project.TestFixture.githubActionsRun2
import metrik.project.TestFixture.mockEmitCb
import metrik.project.TestFixture.pipelineId
import metrik.project.TestFixture.previousTimeStamp
import metrik.project.domain.model.Commit
import metrik.project.domain.repository.BuildRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

@ExtendWith(MockKExtension::class)
class PipelineCommitServiceTest {
    @MockK(relaxed = true)
    private lateinit var buildRepository: BuildRepository

    @MockK(relaxed = true)
    private lateinit var commitService: CommitService

    @InjectMockKs
    private lateinit var pipelineCommitService: PipelineCommitService

    @Test
    fun `should map one run to its commits`() {
        val build = githubActionsExecution.copy(changeSets = listOf(commit.copy(timestamp = previousTimeStamp)))

        every {
            buildRepository.getPreviousBuild(
                pipelineId,
                currentTimeStamp,
                branch
            )
        } returns build

        every {
            commitService.getCommitsBetweenTimePeriod(
                ZonedDateTime.parse("2021-04-23T13:41:01.779Z")!!,
                ZonedDateTime.parse("2021-08-17T12:23:25Z")!!,
                branch = branch,
                pipeline = githubActionsPipeline
            )
        } returns listOf(commit)

        val map = mapOf(branch to mapOf(githubActionsRun1 to listOf(commit)))

        val actualMap = pipelineCommitService.mapCommitToRun(
            githubActionsPipeline,
            mutableListOf(githubActionsRun1),
            mockEmitCb
        )
        Assertions.assertEquals(map, actualMap)
    }

    @Test
    fun `should map one run to its commit given no previous build and commits are in correct time range`() {
        every {
            buildRepository.getPreviousBuild(
                pipelineId,
                currentTimeStamp,
                branch
            )
        } returns null

        every {
            commitService.getCommitsBetweenTimePeriod(
                startTimeStamp = null,
                ZonedDateTime.parse("2021-08-17T12:23:25Z")!!,
                branch = branch,
                pipeline = githubActionsPipeline
            )
        } returns listOf(commit)

        val map = mapOf(branch to mapOf(githubActionsRun1 to listOf(commit)))

        Assertions.assertEquals(
            map,
            pipelineCommitService.mapCommitToRun(
                githubActionsPipeline,
                mutableListOf(githubActionsRun1),
                mockEmitCb
            )
        )
    }

    @Test
    fun `should map one run to its commit given no previous build and commits are not in correct time range`() {
        every {
            buildRepository.getPreviousBuild(
                pipelineId,
                currentTimeStamp,
                branch
            )
        } returns null

        every {
            commitService.getCommitsBetweenTimePeriod(
                null,
                ZonedDateTime.parse("2021-08-17T12:23:25Z")!!,
                branch = branch,
                pipeline = githubActionsPipeline
            )
        } returns listOf(commit.copy(timestamp = 1629856700010))

        val map = mapOf(branch to mapOf(githubActionsRun1 to emptyList<Commit>()))

        Assertions.assertEquals(
            map,
            pipelineCommitService.mapCommitToRun(
                githubActionsPipeline,
                mutableListOf(githubActionsRun1),
                mockEmitCb
            )
        )
    }

    @Test
    fun `should map multiple runs to their commits`() {
        val buildFirst = githubActionsExecution.copy(changeSets = listOf(commit.copy(commitId = "commit build 1", timestamp = previousTimeStamp)))
        val buildSecond = githubActionsExecution.copy(changeSets = listOf(commit.copy(commitId = "commit build 2", timestamp = 1619098860779)))
        val buildThird = githubActionsExecution.copy(changeSets = listOf(commit.copy(commitId = "commit build 3", timestamp = 1618926060779)))

        val githubActionsRun2 = githubActionsRun1.copy(
            commitTimeStamp = ZonedDateTime.parse("2021-04-23T13:41:00.779Z")
        )

        val githubActionsRun3 = githubActionsRun1.copy(
            commitTimeStamp = ZonedDateTime.parse("2021-04-22T13:41:00.779Z")
        )

        every {
            buildRepository.getPreviousBuild(
                pipelineId,
                any(),
                branch
            )
        } returns buildThird andThen buildFirst andThen buildSecond andThen buildThird

        val commit1 = commit.copy(commitId = "commit 1", timestamp = 1629203005000)
        val commit2 = commit.copy(commitId = "commit 2", timestamp = 1619185260779)
        val commit3 = commit.copy(commitId = "commit 3", timestamp = 1619098860779)

        every {
            commitService.getCommitsBetweenTimePeriod(
                ZonedDateTime.parse("2021-04-20T13:41:01.779Z")!!,
                ZonedDateTime.parse("2021-08-17T12:23:25Z")!!,
                branch = branch,
                pipeline = githubActionsPipeline
            )
        } returns listOf(commit1, commit2, commit3)

        val map = mapOf(
            branch to mapOf(
                githubActionsRun1 to listOf(commit1),
                githubActionsRun2 to listOf(commit2),
                githubActionsRun3 to listOf(commit3)
            )
        )
        val runs = mutableListOf(githubActionsRun1, githubActionsRun2, githubActionsRun3)

        Assertions.assertEquals(
            map,
            pipelineCommitService.mapCommitToRun(
                githubActionsPipeline,
                runs,
                mockEmitCb
            )
        )
    }

    @Test
    fun `should map first run given previous run does not exist`() {
        every {
            buildRepository.getPreviousBuild(
                pipelineId,
                any(),
                branch
            )
        } returns null

        every {
            commitService.getCommitsBetweenTimePeriod(
                startTimeStamp = null,
                endTimeStamp = ZonedDateTime.parse("2021-08-17T12:23:25Z")!!,
                branch = branch,
                pipeline = githubActionsPipeline
            )
        } returns listOf(commit)

        val map = mapOf(
            branch to mapOf(
                githubActionsRun1 to listOf(commit),
            )
        )

        Assertions.assertEquals(
            map,
            pipelineCommitService.mapCommitToRun(
                githubActionsPipeline,
                mutableListOf(githubActionsRun1),
                mockEmitCb
            )
        )
    }

    @Test
    fun `should use previous run's timestamp when previous run does not have commits`() {
        val previousDateTime = ZonedDateTime.parse("2021-04-20T13:41:01.779Z")
        val build = githubActionsExecution.copy(timestamp = previousDateTime.toTimestamp(), changeSets = emptyList())
        every {
            buildRepository.getPreviousBuild(
                pipelineId,
                any(),
                branch
            )
        } returns build

        every {
            commitService.getCommitsBetweenTimePeriod(
                startTimeStamp = previousDateTime.plus(1, ChronoUnit.SECONDS),
                endTimeStamp = ZonedDateTime.parse("2021-08-17T12:23:25Z")!!,
                branch = branch,
                pipeline = githubActionsPipeline
            )
        } returns listOf(commit)

        val map = mapOf(
            branch to mapOf(
                githubActionsRun1 to listOf(commit),
            )
        )

        Assertions.assertEquals(
            map,
            pipelineCommitService.mapCommitToRun(
                githubActionsPipeline,
                mutableListOf(githubActionsRun1),
                mockEmitCb
            )
        )
    }

    @Test
    fun `should not include commit which already handled by existing builds`() {
        val previousDateTime = ZonedDateTime.parse("2021-04-20T13:41:01.779Z")
        val commit1 = commit.copy(commitId = "commit1", timestamp = 1629203005000)
        val commit2 = commit.copy(commitId = "commit2", timestamp = 1619185260779)
        val commit3 = commit.copy(commitId = "commit3", timestamp = 1619098860779)

        val build = githubActionsExecution.copy(timestamp = previousDateTime.toTimestamp(), changeSets = emptyList())
        val historyBuild = githubActionsExecution.copy(timestamp = previousDateTime.minusDays(1).toTimestamp(), changeSets = listOf(commit3))
        every {
            buildRepository.getPreviousBuild(
                pipelineId,
                any(),
                branch
            )
        } returns build
        every {
            buildRepository.getAllBuilds(pipelineId)
        } returns listOf(historyBuild, build)

        every {
            commitService.getCommitsBetweenTimePeriod(
                startTimeStamp = previousDateTime.plus(1, ChronoUnit.SECONDS),
                endTimeStamp = ZonedDateTime.parse("2021-08-17T12:23:25Z")!!,
                branch = branch,
                pipeline = githubActionsPipeline
            )
        } returns listOf(commit1, commit2, commit3)

        val map = mapOf(
            branch to mapOf(
                githubActionsRun1 to listOf(commit1, commit2),
            )
        )

        Assertions.assertEquals(
            map,
            pipelineCommitService.mapCommitToRun(
                githubActionsPipeline,
                mutableListOf(githubActionsRun1),
                mockEmitCb
            )
        )
    }

    @Test
    fun `should not include commit which already handled by another branch`() {
        val previousDateTime = ZonedDateTime.parse("2021-04-20T13:41:01.779Z")
        val commit1 = commit.copy(commitId = "commit1", timestamp = 1629203005000)
        val commit2 = commit.copy(commitId = "commit2", timestamp = 1619185260779)
        val commit3 = commit.copy(commitId = "commit3", timestamp = 1619098860779)

        val build = githubActionsExecution.copy(timestamp = previousDateTime.toTimestamp(), changeSets = emptyList())
        every {
            buildRepository.getPreviousBuild(
                pipelineId,
                any(),
                branch
            )
        } returns build

        every {
            commitService.getCommitsBetweenTimePeriod(
                startTimeStamp = previousDateTime.plus(1, ChronoUnit.SECONDS),
                endTimeStamp = ZonedDateTime.parse("2021-08-17T12:23:25Z")!!,
                branch = branch,
                pipeline = githubActionsPipeline
            )
        } returns listOf(commit1, commit2, commit3)
        every {
            commitService.getCommitsBetweenTimePeriod(
                startTimeStamp = any(),
                endTimeStamp = any(),
                branch = githubActionsRun2.branch,
                pipeline = githubActionsPipeline
            )
        } returns listOf(commit3)

        val map = mapOf(
            branch to mapOf(
                githubActionsRun1 to listOf(commit1, commit2),
            ),
            githubActionsRun2.branch to mapOf(
                githubActionsRun2 to listOf(commit3)
            )
        )
        Assertions.assertEquals(
            map,
            pipelineCommitService.mapCommitToRun(
                githubActionsPipeline,
                mutableListOf(githubActionsRun1, githubActionsRun2),
                mockEmitCb
            )
        )
    }
}
