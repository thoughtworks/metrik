package metrik.project.rest.applicationservice

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import metrik.project.buildJenkinsPipelineRequest
import metrik.project.domain.model.PipelineConfiguration
import metrik.project.domain.model.PipelineType
import metrik.project.domain.model.Project
import metrik.project.domain.repository.BuildRepository
import metrik.project.domain.repository.PipelineRepository
import metrik.project.domain.repository.ProjectRepository
import metrik.project.exception.ProjectNameDuplicateException
import metrik.project.rest.vo.request.ProjectRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus

@ExtendWith(MockKExtension::class)
internal class ProjectApplicationServiceTest {
    @InjectMockKs(overrideValues = true)
    private lateinit var projectApplicationService: ProjectApplicationService

    @MockK
    private lateinit var pipelineApplicationService: PipelineApplicationService

    @RelaxedMockK
    private lateinit var projectRepository: ProjectRepository

    @RelaxedMockK
    private lateinit var pipelineRepository: PipelineRepository

    @RelaxedMockK
    private lateinit var buildRepository: BuildRepository

    private var projectId = "601a2d059deac2220dd0756b"
    private var projectName = "first project"
    private var expectedProject = Project(projectId, projectName)
    private var pipelineId = "501a2d059deac2220dd0756f"
    private var pipelineName = "pipeline name"
    private var pipelineUrl = "http://localhost:8080/some-pipeline"
    private var pipelineUserName = "4km"
    private var pipelineCredential = "4km"
    private var expectedPipeline =
        PipelineConfiguration(
            pipelineId, projectId, pipelineName, pipelineUserName,
            pipelineCredential, pipelineUrl, PipelineType.JENKINS
        )

    @Test
    fun `test create project successfully`() {
        every { projectRepository.existWithGivenName(projectName) } returns false
        every { projectRepository.save(any()) } returns expectedProject
        every { pipelineRepository.saveAll(any()) } returns listOf(expectedPipeline)

        every { pipelineApplicationService.verifyPipelineConfiguration(any()) } just runs

        val pipeline = buildJenkinsPipelineRequest()
        val projectDetailResponse =
            projectApplicationService.createProject(ProjectRequest(projectName, pipeline))

        assertEquals(projectDetailResponse.id, projectId)
        verify(exactly = 1) {
            pipelineApplicationService.verifyPipelineConfiguration(
                withArg {
                    assertEquals(pipeline.url, it.url)
                    assertEquals(pipeline.username, it.username)
                    assertEquals(pipeline.credential, it.credential)
                    assertEquals(PipelineType.JENKINS, it.type)
                }
            )
        }
    }

    @Test
    fun `create project will fail if project name already exist`() {
        every { projectRepository.existWithGivenName(projectName) } returns true

        val exception = assertThrows<ProjectNameDuplicateException> {
            projectApplicationService.createProject(ProjectRequest(projectName, buildJenkinsPipelineRequest()))
        }

        assertEquals(exception.httpStatus, HttpStatus.BAD_REQUEST)
        assertEquals(exception.message, "Project name duplicate")
    }

    @Test
    fun `update project name`() {
        every { projectRepository.findById(projectId) } returns expectedProject
        every { projectRepository.save(expectedProject) } returns expectedProject

        val newProjectName = "new project name"
        val updatedProject = projectApplicationService.updateProjectName(projectId, newProjectName)

        assertEquals(updatedProject.name, newProjectName)
    }

    @Test
    fun `test delete project`() {
        every { projectRepository.findById(projectId) } returns Project(projectId)
        every { pipelineRepository.findByProjectId(projectId) } returns listOf(PipelineConfiguration(pipelineId, projectId))

        projectApplicationService.deleteProject(projectId)

        verify(exactly = 1) { projectRepository.deleteById(projectId) }
        verify(exactly = 1) { pipelineRepository.deleteById(pipelineId) }
        verify(exactly = 1) { buildRepository.clear(pipelineId) }
    }

    @Test
    fun `test get pipeline details`() {
        every { projectRepository.findById(projectId) } returns expectedProject
        every { pipelineRepository.findByProjectId(projectId) } returns listOf(expectedPipeline)

        val projectDetailResponse = projectApplicationService.getProjectDetails(projectId)

        assertEquals(projectDetailResponse.id, projectId)
        assertEquals(projectDetailResponse.name, projectName)
        assertEquals(projectDetailResponse.pipelines[0].id, pipelineId)
        assertEquals(projectDetailResponse.pipelines[0].name, pipelineName)
    }

    @Test
    fun `test get project list`() {
        every { projectRepository.findAll() } returns listOf(expectedProject)

        val projects = projectApplicationService.getProjects()

        assertEquals(projects.size, 1)
        assertEquals(projects[0].id, projectId)
        assertEquals(projects[0].name, projectName)
    }
}
