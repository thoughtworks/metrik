package metrik.project.domain.service.buddy

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import feign.FeignException
import feign.Request
import feign.Response
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import metrik.project.domain.model.Execution
import metrik.project.domain.model.PipelineConfiguration
import metrik.project.domain.model.Stage
import metrik.project.domain.model.Status
import metrik.project.domain.repository.BuildRepository
import metrik.project.exception.PipelineConfigVerifyException
import metrik.project.infrastructure.buddy.feign.BuddyFeignClient
import metrik.project.infrastructure.buddy.feign.BuddyFeignClientConfiguration
import metrik.project.rest.vo.response.SyncProgress
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.ValueSource
import java.net.URI
import java.util.stream.Stream

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
@ExtendWith(MockKExtension::class)
class BuddyPipelineServiceTest {

    private val objectMapper: ObjectMapper = BuddyFeignClientConfiguration().objectMapper()

    private val buildRepository = mockk<BuildRepository>(relaxed = true)

    private val buddyFeignClient = mockk<BuddyFeignClient>(relaxed = true)

    @InjectMockKs
    private lateinit var buddyPipelineService: BuddyPipelineService

    @Test
    fun `should verify pipeline resource`() {
        val pipelineConfiguration = PipelineConfiguration(
            credential = "credential",
            url = "https://api.sls.io/workspaces/beta/projects/metrik/pipelines/124763"
        )
        every {
            buddyFeignClient.getPipeline(URI.create(pipelineConfiguration.url), pipelineConfiguration.credential)
        } returns objectMapper.readValue(javaClass.getResource("/pipeline/buddy/verify-pipeline.json").readText())

        buddyPipelineService.verifyPipelineConfiguration(pipelineConfiguration)
    }

    @Test
    fun `should throw exception when url is not correct pipeline resource`() {
        val pipelineConfiguration = PipelineConfiguration(
            credential = "credential",
            url = "https://api.sls.io/workspaces/beta/projects/metrik"
        )
        every {
            buddyFeignClient.getPipeline(URI.create(pipelineConfiguration.url), pipelineConfiguration.credential)
        } returns objectMapper.readValue(javaClass.getResource("/pipeline/buddy/verify-project.json").readText())

        val ex = assertThrows<PipelineConfigVerifyException> {
            buddyPipelineService.verifyPipelineConfiguration(pipelineConfiguration)
        }
        assertEquals("Invalid pipeline resource", ex.message)
    }

    private object VerifyFailedProvider : ArgumentsProvider {
        override fun provideArguments(context: ExtensionContext?): Stream<Arguments> = Stream.of(
            Arguments.of("https://api.sls.io/workspaces/beta/projects/metrik/invalid/124763", 400),
            Arguments.of("https://api.sls.io/workspaces/beta/projects/metrik/pipelines/124763", 401),
            Arguments.of("https://api.sls.io/workspaces/beta/projects/metrik/pipelines/999999", 404),
        )
    }

    @ParameterizedTest
    @ArgumentsSource(VerifyFailedProvider::class)
    fun `should throw exception on 4XX server response`(url: String, status: Int) {
        every {
            buddyFeignClient.getPipeline(any(), any())
        } throws FeignException.errorStatus(
            "GET",
            Response.builder().status(status).request(
                Request.create(Request.HttpMethod.GET, url, mapOf(), null, null, null)
            ).build()
        )

        val ex = assertThrows<PipelineConfigVerifyException> {
            buddyPipelineService.verifyPipelineConfiguration(PipelineConfiguration(url = url))
        }
        assertEquals("Verify failed", ex.message)
    }

    @Test
    fun `should return sorted stage name lists when getStagesSortedByName() called`() {
        every { buildRepository.getAllBuilds("testPipelineId") } returns listOf(
            Execution(
                stages = listOf(
                    Stage(name = "build"),
                    Stage(name = "deploy"),
                    Stage(name = "success notification")
                )
            ),
            Execution(
                stages = listOf(
                    Stage(name = "build"),
                    Stage("failure notification")
                )
            )
        )

        val result = buddyPipelineService.getStagesSortedByName("testPipelineId")

        assertEquals(listOf("build", "deploy", "failure notification", "success notification"), result)
    }

    @ParameterizedTest
    @ValueSource(strings = ["executions-empty.json", "executions-empty-unpaged.json"])
    fun `should return zero builds when no executions`(source: String) {
        val pipeline = PipelineConfiguration(
            id = "pipelineId",
            credential = "credential",
            url = "https://api.sls.io/workspaces/beta/projects/metrik/pipelines/124763",
            name = "deploy"
        )
        val emitCb = mockk<(SyncProgress) -> Unit>(relaxed = true)
        every { buildRepository.getByBuildNumber(any(), any()) } returns null
        every {
            buddyFeignClient.getExecutionPage(any(), any(), any())
        } returns objectMapper.readValue(javaClass.getResource("/pipeline/buddy/$source").readText())

        val builds = buddyPipelineService.syncBuildsProgressively(pipeline, emitCb)

        verify(exactly = 1) { emitCb.invoke(any()) }
        verify(exactly = 1) { emitCb.invoke(SyncProgress(pipeline.id, pipeline.name, 0, 0, 1, 2)) }
        assertEquals(0, builds.size)
    }

    @Test
    fun `should return all builds for unpaged executions`() {
        val pipeline = PipelineConfiguration(
            id = "pipelineId",
            credential = "credential",
            url = "https://api.sls.io/workspaces/beta/projects/metrik/pipelines/124763",
            name = "deploy"
        )
        val emitCb = mockk<(SyncProgress) -> Unit>(relaxed = true)
        every { buildRepository.getByBuildNumber(any(), any()) } returns null
        every {
            buddyFeignClient.getExecutionPage(any(), any(), any())
        } returns objectMapper.readValue(javaClass.getResource("/pipeline/buddy/executions-unpaged.json").readText())

        val builds = buddyPipelineService.syncBuildsProgressively(pipeline, emitCb)

        verify(exactly = 22) { emitCb.invoke(any()) }
        verify(exactly = 1) { emitCb.invoke(SyncProgress(pipeline.id, pipeline.name, 0, 0, 1, 2)) }
        verify(exactly = 1) { emitCb.invoke(SyncProgress(pipeline.id, pipeline.name, 20, 20, 1, 2)) }
        (1..20).forEach {
            verify(exactly = 1) { emitCb.invoke(SyncProgress(pipeline.id, pipeline.name, it, 20, 2, 2)) }
        }
        assertEquals(20, builds.size)
    }

    @Test
    fun `should return all builds for all paged executions`() {
        val pipeline = PipelineConfiguration(
            id = "pipelineId",
            credential = "credential",
            url = "https://api.sls.io/workspaces/beta/projects/metrik/pipelines/124763",
            name = "deploy"
        )
        val emitCb = mockk<(SyncProgress) -> Unit>(relaxed = true)
        every { buildRepository.getByBuildNumber(any(), any()) } returns null
        (1..2).forEach { page ->
            every {
                buddyFeignClient.getExecutionPage(any(), any(), page)
            } returns objectMapper.readValue(javaClass.getResource("/pipeline/buddy/executions-page-$page.json").readText())
        }

        val builds = buddyPipelineService.syncBuildsProgressively(pipeline, emitCb)

        verify(exactly = 41) { emitCb.invoke(any()) }
        verify(exactly = 1) { emitCb.invoke(SyncProgress(pipeline.id, pipeline.name, 0, 0, 1, 2)) }
        verify(exactly = 1) { emitCb.invoke(SyncProgress(pipeline.id, pipeline.name, 20, 38, 1, 2)) }
        verify(exactly = 1) { emitCb.invoke(SyncProgress(pipeline.id, pipeline.name, 38, 38, 1, 2)) }
        (1..38).forEach {
            verify(exactly = 1) { emitCb.invoke(SyncProgress(pipeline.id, pipeline.name, it, 38, 2, 2)) }
        }
        assertEquals(38, builds.size)
    }

    @ParameterizedTest
    @EnumSource(value = Status::class, names = ["SUCCESS", "FAILED", "OTHER"])
    fun `should not return already saved builds with statuses`(status: Status) {
        val pipeline = PipelineConfiguration(
            id = "pipelineId",
            credential = "credential",
            url = "https://api.sls.io/workspaces/beta/projects/metrik/pipelines/124763",
            name = "deploy"
        )
        val emitCb = mockk<(SyncProgress) -> Unit>(relaxed = true)
        every { buildRepository.getByBuildNumber(any(), any()) } returns Execution(result = status)
        (1..2).forEach { page ->
            every {
                buddyFeignClient.getExecutionPage(any(), any(), page)
            } returns objectMapper.readValue(javaClass.getResource("/pipeline/buddy/executions-page-$page.json").readText())
        }

        val builds = buddyPipelineService.syncBuildsProgressively(pipeline, emitCb)

        verify(exactly = 3) { emitCb.invoke(any()) }
        verify(exactly = 1) { emitCb.invoke(SyncProgress(pipeline.id, pipeline.name, 0, 0, 1, 2)) }
        verify(exactly = 1) { emitCb.invoke(SyncProgress(pipeline.id, pipeline.name, 20, 38, 1, 2)) }
        verify(exactly = 1) { emitCb.invoke(SyncProgress(pipeline.id, pipeline.name, 38, 38, 1, 2)) }
        assertEquals(0, builds.size)
    }
}
