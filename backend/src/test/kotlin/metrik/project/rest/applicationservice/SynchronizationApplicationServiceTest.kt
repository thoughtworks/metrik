package metrik.project.rest.applicationservice

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import io.mockk.spyk
import io.mockk.verify
import metrik.exception.ApplicationException
import metrik.project.domain.model.Build
import metrik.project.domain.model.Pipeline
import metrik.project.domain.model.PipelineType
import metrik.project.domain.model.Project
import metrik.project.domain.repository.PipelineRepository
import metrik.project.domain.repository.ProjectRepository
import metrik.project.domain.service.PipelineService
import metrik.project.domain.service.factory.PipelineServiceFactory
import metrik.project.rest.vo.response.SyncProgress
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class SynchronizationApplicationServiceTest {
    @MockK
    private lateinit var pipelineServiceFactory: PipelineServiceFactory

    @MockK(relaxed = true)
    private lateinit var pipelineServiceMock: PipelineService

    @MockK(relaxed = true)
    private lateinit var projectRepository: ProjectRepository

    @MockK(relaxed = true)
    private lateinit var pipelineRepository: PipelineRepository

    @InjectMockKs
    private lateinit var synchronizationApplicationService: SynchronizationApplicationService

    @BeforeEach
    fun setup() {
        every { pipelineServiceFactory.getService(any()) } returns pipelineServiceMock
    }

    @Test
    fun `should sync builds when there is no previous update`() {
        val projectId = "fake-project-id"
        val pipelineId = "fake-pipeline-id"
        val pipeline = Pipeline(id = pipelineId, type = PipelineType.BAMBOO)
        val builds = listOf(Build())

        every { projectRepository.findById(any()) } returns Project(projectId)
        every { pipelineRepository.findByProjectId(projectId) } returns listOf(pipeline)
        every { pipelineServiceMock.syncBuildsProgressively(any(), any()) } returns builds

        val updatedTimestamp = synchronizationApplicationService.synchronize(projectId)

        assertThat(updatedTimestamp).isNotNull
        verify(exactly = 1) {
            pipelineServiceMock.syncBuildsProgressively(
                withArg {
                    assertEquals(pipeline, it)
                },
                any()
            )
        }
    }

    @Test
    fun `should save builds from 2 weeks ago of previous update to now`() {
        val projectId = "fake-project-id"
        val pipelineId = "fake-pipeline-id"
        val pipeline = Pipeline(id = pipelineId, type = PipelineType.BAMBOO)
        val builds = listOf(Build())
        val lastSyncTimestamp = 1610668800000

        every { projectRepository.findById(projectId) } returns Project(projectId, "name", lastSyncTimestamp)
        every { pipelineRepository.findByProjectId(projectId) } returns listOf(pipeline)
        every { projectRepository.updateSynchronizationTime(any(), any()) } returns (lastSyncTimestamp + 1)
        every { pipelineServiceMock.syncBuildsProgressively(any(), any()) } returns builds

        val updateTimestamp = synchronizationApplicationService.synchronize(projectId)

        assertThat(updateTimestamp).isGreaterThan(lastSyncTimestamp)
        verify(exactly = 1) {
            pipelineServiceMock.syncBuildsProgressively(
                withArg {
                    assertEquals(pipeline, it)
                },
                any()
            )
        }
        verify(exactly = 1) {
            projectRepository.updateSynchronizationTime(
                projectId,
                withArg {
                    assertTrue(it > lastSyncTimestamp)
                }
            )
        }
    }

    @Test
    fun `should not save any builds if fetch data from pipeline failed`() {
        val projectId = "fake-project-id"
        val pipelineId = "fake-pipeline-id"
        val pipeline = Pipeline(id = pipelineId, type = PipelineType.BAMBOO)
        val lastSyncTimestamp = 1610668800000

        every { projectRepository.findById(projectId) } returns Project(projectId, "name", lastSyncTimestamp)
        every { pipelineRepository.findByProjectId(projectId) } returns listOf(pipeline)
        every { projectRepository.updateSynchronizationTime(any(), any()) } returns (lastSyncTimestamp + 1)
        every { pipelineServiceMock.syncBuildsProgressively(any(), any()) } throws RuntimeException()

        assertThrows<ApplicationException> { synchronizationApplicationService.synchronize(projectId) }
    }

    @Test
    fun `should get last synchronization time`() {
        val projectId = "fake-project-id"
        val lastSyncTimestamp = 1610668800000

        every { projectRepository.findById(projectId) } returns Project(projectId, "name", lastSyncTimestamp)

        assertEquals(lastSyncTimestamp, synchronizationApplicationService.getLastSyncTimestamp(projectId))
    }

    @Test
    fun `should sync builds for Bamboo when there is no previous update`() {
        val projectId = "fake-project-id"
        val pipelineId = "fake-pipeline-id"
        val pipeline = Pipeline(id = pipelineId, type = PipelineType.BAMBOO)
        val builds = listOf(Build())

        every { projectRepository.findById(projectId) } returns Project(projectId)
        every { pipelineRepository.findByProjectId(projectId) } returns listOf(
            Pipeline(id = pipelineId, type = PipelineType.BAMBOO)
        )
        every { pipelineServiceMock.syncBuildsProgressively(any(), any()) } returns builds

        val updatedTimestamp = synchronizationApplicationService.synchronize(projectId)

        verify(exactly = 1) {
            pipelineServiceMock.syncBuildsProgressively(
                withArg {
                    assertEquals(pipeline, it)
                },
                any()
            )
        }
        verify(exactly = 1) { projectRepository.updateSynchronizationTime(any(), any()) }

        assertThat(updatedTimestamp).isNotNull
    }

    @Test
    fun `should sync builds with progress emit callback passed in if callback function provided`() {
        val projectId = "fake-project-id"
        val pipelineId = "fake-pipeline-id"
        val pipeline = Pipeline(id = pipelineId, type = PipelineType.BAMBOO)
        val builds = listOf(Build())

        val progress = SyncProgress(pipelineId, "pipeline name", 1, 1)
        val mockEmitCb = spyk<(SyncProgress) -> Unit>()
        val callbackCaptor = slot<(SyncProgress) -> Unit>()

        every { projectRepository.findById(projectId) } returns Project(projectId)
        every { pipelineRepository.findByProjectId(projectId) } returns listOf(
            Pipeline(id = pipelineId, type = PipelineType.BAMBOO)
        )
        every {
            pipelineServiceMock.syncBuildsProgressively(
                pipeline = any(),
                emitCb = capture(callbackCaptor)
            )
        } answers {
            callbackCaptor.captured.invoke(progress)
            builds
        }

        val updatedTimestamp = synchronizationApplicationService.synchronize(projectId, mockEmitCb)

        assertThat(updatedTimestamp).isNotNull
        verify(exactly = 1) {
            pipelineServiceMock.syncBuildsProgressively(
                withArg {
                    assertEquals(pipeline, it)
                },
                any()
            )
        }
        verify(exactly = 1) { projectRepository.updateSynchronizationTime(any(), any()) }
        verify(exactly = 1) { mockEmitCb.invoke(progress) }
    }
}
