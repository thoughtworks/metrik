package metrik.project.controller.applicationservice

import metrik.MockitoHelper.anyObject
import metrik.MockitoHelper.argThat
import metrik.project.buildJenkinsPipelineRequest
import metrik.project.controller.vo.request.ProjectRequest
import metrik.project.exception.ProjectNameDuplicateException
import metrik.project.model.Pipeline
import metrik.project.model.PipelineType
import metrik.project.model.Project
import metrik.project.repository.BuildRepository
import metrik.project.repository.PipelineRepository
import metrik.project.repository.ProjectRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.anyList
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpStatus

@ExtendWith(MockitoExtension::class)
class ProjectApplicationServiceTest {
    @InjectMocks
    private lateinit var projectApplicationService: ProjectApplicationService

    @Mock
    private lateinit var pipelineApplicationService: PipelineApplicationService

    @Mock
    private lateinit var projectRepository: ProjectRepository

    @Mock
    private lateinit var pipelineRepository: PipelineRepository

    @Mock
    private lateinit var buildRepository: BuildRepository

    private var projectId = "601a2d059deac2220dd0756b"
    private var projectName = "first project"
    private var expectedProject = Project(projectId, projectName)
    private var pipelineId = "501a2d059deac2220dd0756f"
    private var pipelineName = "pipeline name"
    private var pipelineUrl = "htttp://localhost:8080/some-pipeline"
    private var pipelineUserName = "4km"
    private var pipelineCredential = "4km"
    private var expectedPipeline =
        Pipeline(
            pipelineId, projectId, pipelineName, pipelineUserName,
            pipelineCredential, pipelineUrl, PipelineType.JENKINS
        )

    @Test
    internal fun `test create project successfully`() {
        `when`(projectRepository.existWithGivenName(projectName)).thenReturn(false)
        `when`(projectRepository.save(anyObject())).thenReturn(expectedProject)
        `when`(pipelineRepository.saveAll(anyList())).thenReturn(listOf(expectedPipeline))

        val pipeline = buildJenkinsPipelineRequest()
        val projectDetailResponse =
            projectApplicationService.createProject(ProjectRequest(projectName, pipeline))

        assertEquals(projectDetailResponse.id, projectId)
        verify(pipelineApplicationService).verifyPipelineConfiguration(argThat {
            assertEquals(pipeline.url, it.url)
            assertEquals(pipeline.username, it.username)
            assertEquals(pipeline.credential, it.credential)
            assertEquals(PipelineType.JENKINS, it.type)
            true
        })
    }

    @Test
    internal fun `create project will fail if project name already exist`() {
        `when`(projectRepository.existWithGivenName(projectName)).thenReturn(true)

        val exception = assertThrows<ProjectNameDuplicateException> {
            projectApplicationService.createProject(ProjectRequest(projectName, buildJenkinsPipelineRequest()))
        }

        assertEquals(exception.httpStatus, HttpStatus.BAD_REQUEST)
        assertEquals(exception.message, "Project name duplicate")
    }

    @Test
    internal fun `update project name`() {
        `when`(projectRepository.findById(projectId)).thenReturn(expectedProject)
        `when`(projectRepository.save(expectedProject)).thenReturn(expectedProject)

        val newProjectName = "new project name"
        val updatedProject = projectApplicationService.updateProjectName(projectId, newProjectName)

        assertEquals(updatedProject.name, newProjectName)
    }

    @Test
    internal fun `test delete project`() {
        `when`(projectRepository.findById(projectId)).thenReturn(Project(projectId))
        `when`(pipelineRepository.findByProjectId(projectId)).thenReturn(listOf(Pipeline(pipelineId, projectId)))

        projectApplicationService.deleteProject(projectId)

        verify(projectRepository, times(1)).deleteById(projectId)
        verify(pipelineRepository, times(1)).deleteById(pipelineId)
        verify(buildRepository, times(1)).clear(pipelineId)
    }

    @Test
    internal fun `test get pipeline details`() {
        `when`(projectRepository.findById(projectId)).thenReturn(expectedProject)
        `when`(pipelineRepository.findByProjectId(projectId)).thenReturn(listOf(expectedPipeline))

        val projectDetailResponse = projectApplicationService.getProjectDetails(projectId)

        assertEquals(projectDetailResponse.id, projectId)
        assertEquals(projectDetailResponse.name, projectName)
        assertEquals(projectDetailResponse.pipelines[0].id, pipelineId)
        assertEquals(projectDetailResponse.pipelines[0].name, pipelineName)
    }

    @Test
    internal fun `test get project list`() {
        `when`(projectRepository.findAll()).thenReturn(listOf(expectedProject))

        val projects = projectApplicationService.getProjects()

        assertEquals(projects.size, 1)
        assertEquals(projects[0].id, projectId)
        assertEquals(projects[0].name, projectName)
    }
}