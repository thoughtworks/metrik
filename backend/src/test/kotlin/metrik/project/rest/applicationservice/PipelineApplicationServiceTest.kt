package metrik.project.rest.applicationservice

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import metrik.metrics.exception.BadRequestException
import metrik.project.TestFixture.buildPipeline
import metrik.project.domain.model.PipelineConfiguration
import metrik.project.domain.model.PipelineType
import metrik.project.domain.model.Project
import metrik.project.domain.repository.BuildRepository
import metrik.project.domain.repository.PipelineRepository
import metrik.project.domain.repository.ProjectRepository
import metrik.project.domain.service.PipelineService
import metrik.project.domain.service.factory.PipelineServiceFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus

@ExtendWith(MockKExtension::class)
internal class PipelineApplicationServiceTest {
    @MockK
    private lateinit var pipelineServiceFactory: PipelineServiceFactory

    @MockK(relaxed = true)
    private lateinit var pipelineServiceMock: PipelineService

    @MockK(relaxed = true)
    private lateinit var pipelineRepository: PipelineRepository

    @MockK(relaxed = true)
    private lateinit var projectRepository: ProjectRepository

    @MockK(relaxed = true)
    private lateinit var buildRepository: BuildRepository

    @InjectMockKs
    private lateinit var pipelineApplicationService: PipelineApplicationService

    @BeforeEach
    fun setup() {
        every { pipelineServiceFactory.getService(any()) } returns pipelineServiceMock
    }

    @Test
    fun `should verify pipeline given a pipeline config`() {
        val pipeline = buildPipeline(PipelineType.GITHUB_ACTIONS)

        pipelineApplicationService.verifyPipelineConfiguration(pipeline)

        verify(exactly = 1) { pipelineServiceMock.verifyPipelineConfiguration(pipeline) }
    }

    @Test
    fun `should throw BadRequestException when createPipeline() called given pipeline name already exist`() {
        val pipeline = buildPipeline()
        val projectId = pipeline.projectId
        val pipelineInDB = PipelineConfiguration()
        every { pipelineRepository.findByNameAndProjectId(pipeline.name, projectId) } returns pipelineInDB

        val exception = assertThrows<BadRequestException> { pipelineApplicationService.createPipeline(pipeline) }

        verify(exactly = 1) { projectRepository.findById(projectId) }
        verify(exactly = 0) { pipelineRepository.save(any()) }
        assertEquals("Pipeline name already exist", exception.message)
        assertEquals(HttpStatus.BAD_REQUEST, exception.httpStatus)
    }

    @Test
    fun `should return saved pipeline when createPipeline() called given valid Pipeline`() {
        val pipeline = buildPipeline()
        val projectId = pipeline.projectId
        every { pipelineRepository.findByNameAndProjectId(pipeline.name, projectId) } returns null
        every { pipelineRepository.save(any()) } returns pipeline

        val result = pipelineApplicationService.createPipeline(pipeline)

        assertEquals(pipeline.name, result.name)
        assertEquals(pipeline.url, result.url)
        assertEquals(pipeline.username, result.username)
        assertEquals(pipeline.credential, result.credential)
        assertEquals(pipeline.type, result.type)
        verify(exactly = 1) { projectRepository.findById(projectId) }
        verify(exactly = 1) { pipelineServiceMock.verifyPipelineConfiguration(pipeline) }
        verify(exactly = 1) {
            pipelineRepository.save(
                withArg {
                    assertNotNull(it.id)
                    assertEquals(projectId, it.projectId)
                    assertEquals(pipeline.name, it.name)
                    assertEquals(pipeline.username, it.username)
                    assertEquals(pipeline.credential, it.credential)
                    assertEquals(pipeline.url, it.url)
                    assertEquals(pipeline.type, it.type)
                }
            )
        }
    }

    @Test
    fun `should return saved pipeline when updatePipeline() called and successfully`() {
        val pipeline = buildPipeline()
        val savedPipeline = buildPipeline()
        every { pipelineRepository.save(any()) } returns savedPipeline
        every { pipelineRepository.findByNameAndProjectId(any(), any()) } returns null

        val result = pipelineApplicationService.updatePipeline(pipeline)

        verify(exactly = 1) { projectRepository.findById(pipeline.projectId) }
        verify(exactly = 1) { pipelineRepository.findByIdAndProjectId(pipeline.id, pipeline.projectId) }
        verify(exactly = 1) { pipelineRepository.findByNameAndProjectId(pipeline.name, pipeline.projectId) }
        verify(exactly = 1) { pipelineServiceMock.verifyPipelineConfiguration(pipeline) }
        verify(exactly = 1) {
            pipelineRepository.save(
                withArg {
                    assertEquals(pipeline.projectId, it.projectId)
                    assertEquals(pipeline.id, it.id)
                    assertEquals(pipeline.name, it.name)
                    assertEquals(pipeline.username, it.username)
                    assertEquals(pipeline.credential, it.credential)
                    assertEquals(pipeline.url, it.url)
                    assertEquals(pipeline.type, it.type)
                }
            )
        }
        assertEquals(pipeline.name, result.name)
        assertEquals(pipeline.url, result.url)
        assertEquals(pipeline.username, result.username)
        assertEquals(pipeline.credential, result.credential)
        assertEquals(pipeline.type, result.type)
    }

    @Test
    fun `should throw Exception when updatePipeline() with pipeline name already exist`() {
        val pipeline = buildPipeline()
        val pipelineInDB = PipelineConfiguration()
        every { pipelineRepository.findByNameAndProjectId(pipeline.name, pipeline.projectId) } returns pipelineInDB

        val exception = assertThrows<BadRequestException> { pipelineApplicationService.updatePipeline(pipeline) }

        assertEquals(exception.message, "Pipeline name already exist")
        verify(exactly = 1) { projectRepository.findById(pipeline.projectId) }
        verify(exactly = 1) { pipelineRepository.findByIdAndProjectId(pipeline.id, pipeline.projectId) }
    }

    @Test
    fun `should return pipeline when getPipeline() called`() {
        val pipeline = buildPipeline()
        val pipelineId = pipeline.id
        val projectId = pipeline.projectId
        every { pipelineRepository.findByIdAndProjectId(pipelineId, projectId) } returns pipeline

        val result = pipelineApplicationService.getPipeline(projectId, pipelineId)

        verify(exactly = 1) { projectRepository.findById(projectId) }
        assertEquals(pipeline.id, result.id)
        assertEquals(pipeline.name, result.name)
        assertEquals(pipeline.username, result.username)
        assertEquals(pipeline.credential, result.credential)
        assertEquals(pipeline.url, result.url)
        assertEquals(pipeline.type, result.type)
        assertEquals(pipeline.type, result.type)
    }

    @Test
    fun `should delete pipeline and its builds when deletePipeline() called`() {
        val pipelineId = "pipelineId"
        val projectId = "projectId"
        pipelineApplicationService.deletePipeline(projectId, pipelineId)

        verify(exactly = 1) { projectRepository.findById(projectId) }
        verify(exactly = 1) { pipelineRepository.findByIdAndProjectId(pipelineId, projectId) }
        verify(exactly = 1) { pipelineRepository.deleteById(pipelineId) }
        verify(exactly = 1) { buildRepository.clear(pipelineId) }
    }

    @Test
    fun `should return Pipelines when getPipelineStages() called given projectId`() {
        val projectId = "projectId"
        val projectName = "projectName"
        val pipeline1Id = "pipeline1Id"
        val pipeline2Id = "pipeline2Id"
        val pipeline3Id = "pipeline3Id"
        val pipeline1Name = "bpipelineName"
        val pipeline2Name = "ApipelineName"
        val pipeline3Name = "apipelineName"
        val pipeline1StageName = "pipeline1StageName"
        val pipeline2StageName = "pipeline2StageName"
        val pipeline3StageName = "pipeline3StageName"
        val pipeline1 = PipelineConfiguration(pipeline1Id, projectId, pipeline1Name)
        val pipeline2 = PipelineConfiguration(pipeline2Id, projectId, pipeline2Name)
        val pipeline3 = PipelineConfiguration(pipeline3Id, projectId, pipeline3Name)
        val expectedProject = Project(projectId, projectName)

        every { projectRepository.findById(projectId) } returns expectedProject
        every { pipelineRepository.findByProjectId(projectId) } returns listOf(pipeline1, pipeline2, pipeline3)
        every { pipelineServiceMock.getStagesSortedByName(pipeline1Id) } returns listOf(pipeline1StageName)
        every { pipelineServiceMock.getStagesSortedByName(pipeline2Id) } returns listOf(pipeline2StageName)
        every { pipelineServiceMock.getStagesSortedByName(pipeline3Id) } returns listOf(pipeline3StageName)

        val pipelineStages = pipelineApplicationService.getPipelineStages(projectId)

        assertEquals(pipelineStages.size, 3)
        assertEquals(pipelineStages[0].pipelineId, pipeline2Id)
        assertEquals(pipelineStages[1].pipelineId, pipeline3Id)
        assertEquals(pipelineStages[2].pipelineId, pipeline1Id)
    }
}
