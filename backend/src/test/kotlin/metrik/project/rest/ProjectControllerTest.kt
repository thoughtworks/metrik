package metrik.project.rest

import com.fasterxml.jackson.databind.ObjectMapper
import metrik.MockitoHelper.anyObject
import metrik.project.buildJenkinsPipelineRequest
import metrik.project.rest.applicationservice.ProjectApplicationService
import metrik.project.rest.vo.request.ProjectRequest
import metrik.project.rest.vo.request.BambooPipelineRequest
import metrik.project.rest.vo.response.PipelineResponse
import metrik.project.rest.vo.response.ProjectDetailResponse
import metrik.project.rest.vo.response.ProjectResponse
import metrik.project.rest.vo.response.ProjectSummaryResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(controllers = [ProjectController::class])
internal class ProjectControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var projectApplicationService: ProjectApplicationService

    private val projectId = "projectId"
    private val pipelineId = "pipelineId"
    private val projectName = "projectName"
    private val pipelineName = "pipelineName"
    private lateinit var projectDetailsResponse: ProjectDetailResponse

    @BeforeEach
    fun setUp() {
        projectDetailsResponse = ProjectDetailResponse(
            projectId,
            projectName,
            pipelines = listOf(PipelineResponse(pipelineId, pipelineName))
        )
    }

    @Test
    fun `should get projects successfully `() {
        `when`(projectApplicationService.getProjects()).thenReturn(
            listOf(
                ProjectResponse(
                    projectId,
                    projectName
                )
            )
        )

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/project")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$[0].id").value(projectId))
            .andExpect(jsonPath("$[0].name").value(projectName))
    }

    @Test
    fun `should get project details successfully `() {

        `when`(projectApplicationService.getProjectDetails(projectId)).thenReturn(projectDetailsResponse)

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/project/${projectId}")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(projectId))
            .andExpect(jsonPath("$.name").value(projectName))
            .andExpect(jsonPath("$.pipelines[0].id").value(pipelineId))
            .andExpect(jsonPath("$.pipelines[0].name").value(pipelineName))
    }

    @Test
    fun `should create project and pipeline `() {
        val projectRequest = ProjectRequest(projectName, buildJenkinsPipelineRequest())
        `when`(projectApplicationService.createProject(anyObject())).thenReturn(
            ProjectSummaryResponse(
                "fake-id",
                "fake-name"
            )
        )

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/project")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectMapper().writeValueAsString(projectRequest))
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value("fake-id"))
            .andExpect(jsonPath("$.name").value("fake-name"))
    }

    @Test
    fun `should create project and bamboo pipeline`() {
        val projectRequest = ProjectRequest(
            projectName, BambooPipelineRequest(
                name = "pipeline",
                credential = "credential",
                url = "url"
            )
        )
        `when`(projectApplicationService.createProject(anyObject())).thenReturn(
            ProjectSummaryResponse(
                "fake-id",
                "fake-name"
            )
        )

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/project")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectMapper().writeValueAsString(projectRequest))
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value("fake-id"))
            .andExpect(jsonPath("$.name").value("fake-name"))
    }

    @Test
    fun `should update project name `() {
        val projectNewName = "projectNewName"
        `when`(projectApplicationService.updateProjectName(projectId, projectNewName)).thenReturn(
            ProjectResponse(projectId, projectNewName)
        )

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/project/${projectId}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(projectNewName)
        ).andExpect(status().isOk)
    }

    @Test
    fun `should delete project `() {
        Mockito.doNothing().`when`(projectApplicationService).deleteProject(projectId)
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/project/$projectId")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk)
    }
}
