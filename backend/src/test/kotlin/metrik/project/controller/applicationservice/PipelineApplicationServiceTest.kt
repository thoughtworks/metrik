package metrik.project.controller.applicationservice

import metrik.MockitoHelper.anyObject
import metrik.MockitoHelper.argThat
import metrik.exception.BadRequestException
import metrik.project.buildPipeline
import metrik.project.model.Pipeline
import metrik.project.model.PipelineType
import metrik.project.model.Project
import metrik.project.repository.BuildRepository
import metrik.project.repository.PipelineRepository
import metrik.project.repository.ProjectRepository
import metrik.project.service.PipelineService
import metrik.project.service.factory.PipelineServiceFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpStatus

@ExtendWith(MockitoExtension::class)
internal class PipelineApplicationServiceTest {
    @Mock
    private lateinit var pipelineServiceMock: PipelineService

    @Mock
    private lateinit var pipelineServiceFactory: PipelineServiceFactory

    @Mock
    private lateinit var pipelineRepository: PipelineRepository

    @Mock
    private lateinit var projectRepository: ProjectRepository

    @Mock
    private lateinit var buildRepository: BuildRepository

    @InjectMocks
    private lateinit var pipelineApplicationService: PipelineApplicationService

    @BeforeEach
    fun setup() {
        Mockito.lenient().`when`(pipelineServiceFactory.getService(anyObject())).thenReturn(pipelineServiceMock)
    }

    @Test
    internal fun `should invoke jenkinsPipelineService to verify when verifyPipeline() called given pipeline type is JENKINS`() {
        val pipeline = buildPipeline().copy(type = PipelineType.JENKINS)

        pipelineApplicationService.verifyPipelineConfiguration(pipeline)

        verify(pipelineServiceMock, times(1)).verifyPipelineConfiguration(
            pipeline
        )
    }

    @Test
    internal fun `should verify pipeline given pipeline type is BAMBOO`() {
        val pipeline = buildPipeline(PipelineType.BAMBOO).copy(type = PipelineType.BAMBOO)

        pipelineApplicationService.verifyPipelineConfiguration(pipeline)

        verify(pipelineServiceMock, times(1)).verifyPipelineConfiguration(
            pipeline
        )
    }

    @Test
    internal fun `should throw BadRequestException when createPipeline() called given pipeline name already exist`() {
        val pipeline = buildPipeline()
        val projectId = pipeline.projectId
        val pipelineInDB = Pipeline()
        `when`(pipelineRepository.findByNameAndProjectId(pipeline.name, projectId)).thenReturn(
            pipelineInDB
        )

        val exception = assertThrows<BadRequestException> { pipelineApplicationService.createPipeline(pipeline) }

        verify(projectRepository).findById(projectId)
        verify(pipelineRepository, times(0)).save(anyObject())
        assertEquals("Pipeline name already exist", exception.message)
        assertEquals(HttpStatus.BAD_REQUEST, exception.httpStatus)
    }

    @Test
    internal fun `should return saved pipeline when createPipeline() called given valid Pipeline`() {
        val pipeline = buildPipeline()
        val projectId = pipeline.projectId
        `when`(pipelineRepository.findByNameAndProjectId(pipeline.name, projectId)).thenReturn(null)
        `when`(pipelineRepository.save(anyObject())).thenReturn(pipeline)

        val result = pipelineApplicationService.createPipeline(pipeline)

        verify(projectRepository).findById(projectId)
        verify(pipelineServiceMock, times(1)).verifyPipelineConfiguration(
            pipeline
        )
        verify(pipelineRepository).save(argThat {
            assertEquals(projectId, it.projectId)
            assertNotNull(it.id)
            assertEquals(pipeline.name, it.name)
            assertEquals(pipeline.username, it.username)
            assertEquals(pipeline.credential, it.credential)
            assertEquals(pipeline.url, it.url)
            assertEquals(pipeline.type, it.type)
            true
        })
        assertEquals(pipeline.name, result.name)
        assertEquals(pipeline.url, result.url)
        assertEquals(pipeline.username, result.username)
        assertEquals(pipeline.credential, result.credential)
        assertEquals(pipeline.type, result.type)
    }

    @Test
    internal fun `should return saved pipeline when updatePipeline() called and successfully`() {
        val pipeline = buildPipeline()
        val savedPipeline = buildPipeline()
        `when`(pipelineRepository.save(anyObject())).thenReturn(savedPipeline)

        val result = pipelineApplicationService.updatePipeline(pipeline)

        verify(projectRepository).findById(pipeline.projectId)
        verify(pipelineRepository).findByIdAndProjectId(pipeline.id, pipeline.projectId)
        verify(pipelineRepository).findByNameAndProjectId(pipeline.name, pipeline.projectId)
        verify(pipelineServiceMock, times(1)).verifyPipelineConfiguration(
            pipeline
        )
        verify(pipelineRepository).save(argThat {
            assertEquals(pipeline.projectId, it.projectId)
            assertEquals(pipeline.id, it.id)
            assertEquals(pipeline.name, it.name)
            assertEquals(pipeline.username, it.username)
            assertEquals(pipeline.credential, it.credential)
            assertEquals(pipeline.url, it.url)
            assertEquals(pipeline.type, it.type)
            true
        })
        assertEquals(pipeline.name, result.name)
        assertEquals(pipeline.url, result.url)
        assertEquals(pipeline.username, result.username)
        assertEquals(pipeline.credential, result.credential)
        assertEquals(pipeline.type, result.type)
    }

    @Test
    internal fun `should throw Exception when updatePipeline() with pipeline name already exist`() {
        val pipeline = buildPipeline()
        val pipelineInDB = Pipeline()
        `when`(pipelineRepository.findByNameAndProjectId(pipeline.name, pipeline.projectId)).thenReturn(pipelineInDB)

        val exception = assertThrows<BadRequestException> { pipelineApplicationService.updatePipeline(pipeline) }

        assertEquals(exception.message, "Pipeline name already exist")
        verify(projectRepository).findById(pipeline.projectId)
        verify(pipelineRepository).findByIdAndProjectId(pipeline.id, pipeline.projectId)
    }

    @Test
    internal fun `should return pipeline when getPipeline() called`() {
        val pipeline = buildPipeline()
        val pipelineId = pipeline.id
        val projectId = pipeline.projectId
        `when`(pipelineRepository.findByIdAndProjectId(pipelineId, projectId)).thenReturn(pipeline)

        val result = pipelineApplicationService.getPipeline(projectId, pipelineId)

        verify(projectRepository).findById(projectId)
        assertEquals(pipeline.id, result.id)
        assertEquals(pipeline.name, result.name)
        assertEquals(pipeline.username, result.username)
        assertEquals(pipeline.credential, result.credential)
        assertEquals(pipeline.url, result.url)
        assertEquals(pipeline.type, result.type)
        assertEquals(pipeline.type, result.type)
    }

    @Test
    internal fun `should delete pipeline and its builds when deletePipeline() called`() {
        val pipelineId = "pipelineId"
        val projectId = "projectId"
        pipelineApplicationService.deletePipeline(projectId, pipelineId)

        verify(projectRepository).findById(projectId)
        verify(pipelineRepository).findByIdAndProjectId(pipelineId, projectId)
        verify(pipelineRepository).deleteById(pipelineId)
        verify(buildRepository).clear(pipelineId)
    }

    @Test
    internal fun `should return Pipelines when getPipelineStages() called given projectId`() {
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
        val pipeline1 = Pipeline(pipeline1Id, projectId, pipeline1Name)
        val pipeline2 = Pipeline(pipeline2Id, projectId, pipeline2Name)
        val pipeline3 = Pipeline(pipeline3Id, projectId, pipeline3Name)
        val expectedProject = Project(projectId, projectName)

        `when`(projectRepository.findById(projectId)).thenReturn(expectedProject)
        `when`(pipelineRepository.findByProjectId(projectId)).thenReturn(listOf(pipeline1, pipeline2, pipeline3))
        `when`(pipelineServiceMock.getStagesSortedByName(pipeline1Id)).thenReturn(listOf(pipeline1StageName))
        `when`(pipelineServiceMock.getStagesSortedByName(pipeline2Id)).thenReturn(listOf(pipeline2StageName))
        `when`(pipelineServiceMock.getStagesSortedByName(pipeline3Id)).thenReturn(listOf(pipeline3StageName))

        val pipelineStages = pipelineApplicationService.getPipelineStages(projectId)

        assertEquals(pipelineStages.size, 3)
        assertEquals(pipelineStages[0].pipelineId, pipeline2Id)
        assertEquals(pipelineStages[1].pipelineId, pipeline3Id)
        assertEquals(pipelineStages[2].pipelineId, pipeline1Id)
    }
}