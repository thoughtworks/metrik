package metrik.project.domain.service.githubactions

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import metrik.infrastructure.utlils.toTimestamp
import metrik.project.*
import metrik.project.domain.model.Commit
import metrik.project.domain.repository.BuildRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.ZonedDateTime

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
                ZonedDateTime.parse("2021-04-23T13:41:01.779Z")!!.toTimestamp(),
                ZonedDateTime.parse("2021-08-17T12:23:25Z")!!.toTimestamp(),
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
                startTimeStamp = 0,
                ZonedDateTime.parse("2021-08-17T12:23:25Z")!!.toTimestamp(),
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
                0,
                ZonedDateTime.parse("2021-08-17T12:23:25Z")!!.toTimestamp(),
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
        val buildFirst = githubActionsExecution.copy(changeSets = listOf(commit.copy(timestamp = previousTimeStamp)))
        val buildSecond = githubActionsExecution.copy(changeSets = listOf(commit.copy(timestamp = 1619098860779)))
        val buildThird = githubActionsExecution.copy(changeSets = listOf(commit.copy(timestamp = 1618926060779)))

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

        val commit1 = commit.copy(timestamp = 1629203005000)
        val commit2 = commit.copy(timestamp = 1619185260779)
        val commit3 = commit.copy(timestamp = 1619098860779)

        every {
            commitService.getCommitsBetweenTimePeriod(
                ZonedDateTime.parse("2021-04-20T13:41:01.779Z")!!.toTimestamp(),
                ZonedDateTime.parse("2021-08-17T12:23:25Z")!!.toTimestamp(),
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
    fun `should map first run given previous run does not have commits`() {
        val build = githubActionsExecution.copy(changeSets = emptyList())

        every {
            buildRepository.getPreviousBuild(
                pipelineId,
                any(),
                branch
            )
        } returns build

        every {
            commitService.getCommitsBetweenTimePeriod(
                startTimeStamp = 0,
                endTimeStamp = ZonedDateTime.parse("2021-08-17T12:23:25Z")!!.toTimestamp(),
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
}
