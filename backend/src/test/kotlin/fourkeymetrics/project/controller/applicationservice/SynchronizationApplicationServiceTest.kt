package fourkeymetrics.project.controller.applicationservice

import fourkeymetrics.MockitoHelper.anyObject
import fourkeymetrics.common.model.Build
import fourkeymetrics.exception.ApplicationException
import fourkeymetrics.project.model.Pipeline
import fourkeymetrics.project.model.PipelineType
import fourkeymetrics.project.model.Project
import fourkeymetrics.project.repository.PipelineRepository
import fourkeymetrics.project.repository.ProjectRepository
import fourkeymetrics.project.service.PipelineService
import fourkeymetrics.project.service.PipelineServiceFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.anyLong
import org.mockito.Mockito.anyString
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@Import(SynchronizationApplicationService::class, ProjectRepository::class, PipelineRepository::class)
internal class SynchronizationApplicationServiceTest {
    @Mock
    private lateinit var pipelineServiceMock: PipelineService

    @MockBean
    private lateinit var pipelineServiceFactory: PipelineServiceFactory

    @MockBean
    private lateinit var projectRepository: ProjectRepository

    @MockBean
    private lateinit var pipelineRepository: PipelineRepository

    @Autowired
    private lateinit var synchronizationApplicationService: SynchronizationApplicationService

    @BeforeEach
    fun setup() {
        Mockito.lenient().`when`(pipelineServiceFactory.getService(anyObject())).thenReturn(pipelineServiceMock)
    }

    @Test
    internal fun `should sync builds when there is no previous update`() {
        val projectId = "fake-project-id"
        val pipelineId = "fake-pipeline-id"
        val builds = listOf(Build())

        `when`(projectRepository.findById(anyString())).thenReturn(Project(projectId))
        `when`(pipelineRepository.findByProjectId(projectId)).thenReturn(listOf(Pipeline(id = pipelineId)))
        `when`(pipelineServiceMock.syncBuilds(pipelineId)).thenReturn(builds)

        val updatedTimestamp = synchronizationApplicationService.synchronize(projectId)

        verify(pipelineServiceMock, times(1)).syncBuilds(pipelineId)
        verify(projectRepository, times(1)).updateSynchronizationTime(anyString(), anyLong())

        assertThat(updatedTimestamp).isNotNull
    }

    @Test
    internal fun `should save builds from 2 weeks ago of previous update to now`() {
        val projectId = "fake-project-id"
        val pipelineId = "fake-pipeline-id"
        val builds = listOf(Build())
        val lastSyncTimestamp = 1610668800000

        `when`(projectRepository.findById(projectId)).thenReturn(Project(projectId, "name", lastSyncTimestamp))
        `when`(pipelineRepository.findByProjectId(projectId)).thenReturn(listOf(Pipeline(id = pipelineId)))
        `when`(projectRepository.updateSynchronizationTime(anyString(), anyLong())).thenReturn(lastSyncTimestamp + 1)
        `when`(pipelineServiceMock.syncBuilds(pipelineId)).thenReturn(builds)

        val updateTimestamp = synchronizationApplicationService.synchronize(projectId)

        verify(pipelineServiceMock, times(1)).syncBuilds(pipelineId)
        verify(projectRepository, times(1)).updateSynchronizationTime(anyString(), anyLong())

        assertThat(updateTimestamp).isGreaterThan(lastSyncTimestamp)
    }

    @Test
    internal fun `should not save any builds if fetch data from pipeline failed`() {
        val projectId = "fake-project-id"
        val pipelineId = "fake-pipeline-id"
        val lastSyncTimestamp = 1610668800000

        `when`(projectRepository.findById(projectId)).thenReturn(Project(projectId, "name", lastSyncTimestamp))
        `when`(pipelineRepository.findByProjectId(projectId)).thenReturn(listOf(Pipeline(id = pipelineId)))
        `when`(projectRepository.updateSynchronizationTime(anyString(), anyLong())).thenReturn(lastSyncTimestamp + 1)
        `when`(pipelineServiceMock.syncBuilds(pipelineId)).thenThrow(RuntimeException())

        assertThrows(ApplicationException::class.java) { synchronizationApplicationService.synchronize(projectId) }
    }

    @Test
    internal fun `should get last synchronization time`() {
        val projectId = "fake-project-id"
        val lastSyncTimestamp = 1610668800000

        `when`(projectRepository.findById(projectId)).thenReturn(Project(projectId, "name", lastSyncTimestamp))

        val updatedTimestamp = synchronizationApplicationService.getLastSyncTimestamp(projectId)

        assertThat(updatedTimestamp).isEqualTo(lastSyncTimestamp)
    }

    @Test
    internal fun `should sync builds for Bamboo when there is no previous update`() {
        val projectId = "fake-project-id"
        val pipelineId = "fake-pipeline-id"
        val builds = listOf(Build())

        `when`(projectRepository.findById(anyString())).thenReturn(Project(projectId))
        `when`(pipelineRepository.findByProjectId(projectId)).thenReturn(
            listOf(
                Pipeline(
                    id = pipelineId,
                    type = PipelineType.BAMBOO
                )
            )
        )
        `when`(pipelineServiceMock.syncBuilds(pipelineId)).thenReturn(builds)

        val updatedTimestamp = synchronizationApplicationService.synchronize(projectId)

        verify(pipelineServiceMock, times(1)).syncBuilds(pipelineId)
        verify(projectRepository, times(1)).updateSynchronizationTime(anyString(), anyLong())

        assertThat(updatedTimestamp).isNotNull
    }
}