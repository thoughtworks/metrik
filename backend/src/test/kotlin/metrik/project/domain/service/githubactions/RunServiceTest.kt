package metrik.project.domain.service.githubactions

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import metrik.infrastructure.utlils.toTimestamp
import metrik.project.domain.model.PipelineConfiguration
import metrik.project.infrastructure.github.feign.GithubFeignClient
import metrik.project.infrastructure.github.feign.response.MultipleRunResponse
import metrik.project.infrastructure.github.feign.response.SingleRunResponse
import metrik.project.mockEmitCb
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.ZoneId
import java.time.ZonedDateTime

@ExtendWith(MockKExtension::class)
internal class RunServiceTest {
    @InjectMockKs
    private lateinit var runService: RunService

    @MockK
    private lateinit var githubFeignClient: GithubFeignClient

    private val testPipeline =
        PipelineConfiguration(id = "test pipeline", credential = "fake token", url = "https://test.com/test/test")

    @Test
    internal fun `should keep syncing runs until meet timestamp limitation`() {
        every { githubFeignClient.retrieveMultipleRuns(any(), any(), any(), 2, 1) } returns MultipleRunResponse(
            listOf(
                SingleRunResponse(createdAt = ZonedDateTime.of(2021, 1, 10, 0, 0, 0, 0, ZoneId.systemDefault())),
                SingleRunResponse(createdAt = ZonedDateTime.of(2021, 1, 9, 0, 0, 0, 0, ZoneId.systemDefault())),
            ),
            2
        )
        every { githubFeignClient.retrieveMultipleRuns(any(), any(), any(), 2, 2) } returns MultipleRunResponse(
            listOf(
                SingleRunResponse(createdAt = ZonedDateTime.of(2021, 1, 8, 0, 0, 0, 0, ZoneId.systemDefault())),
                SingleRunResponse(createdAt = ZonedDateTime.of(2021, 1, 7, 0, 0, 0, 0, ZoneId.systemDefault())),
            ),
            2
        )
        every { githubFeignClient.retrieveMultipleRuns(any(), any(), any(), 2, 3) } returns MultipleRunResponse(
            listOf(
                SingleRunResponse(createdAt = ZonedDateTime.of(2021, 1, 6, 0, 0, 0, 0, ZoneId.systemDefault())),
            ),
            1
        )

        val syncedRuns = runService.syncRunsByPage(
            testPipeline,
            ZonedDateTime.of(2021, 1, 7, 0, 0, 0, 0, ZoneId.systemDefault()).toTimestamp(),
            mockEmitCb,
            2
        )

        assertThat(syncedRuns.size).isEqualTo(3)
        verify(exactly = 1) { githubFeignClient.retrieveMultipleRuns(any(), any(), any(), 2, 1) }
        verify(exactly = 1) { githubFeignClient.retrieveMultipleRuns(any(), any(), any(), 2, 2) }
        verify(exactly = 0) { githubFeignClient.retrieveMultipleRuns(any(), any(), any(), 2, 3) }
        verify(exactly = 2) { mockEmitCb(any()) }
    }

    @Test
    internal fun `should sync single run`() {
        every { githubFeignClient.retrieveSingleRun(any(), any(), any(), any()) } returns SingleRunResponse(id = 1)

        val run = runService.syncSingleRun(testPipeline, "https://test.com/123")

        assertThat(run!!.id).isEqualTo(1)
    }
}
