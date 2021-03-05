package fourkeymetrics.project.controller

import com.fasterxml.jackson.databind.ObjectMapper
import fourkeymetrics.MockitoHelper.anyObject
import fourkeymetrics.project.buildPipeline
import fourkeymetrics.project.buildPipelineRequest
import fourkeymetrics.project.buildPipelineVerificationRequest
import fourkeymetrics.project.controller.applicationservice.PipelineApplicationService
import fourkeymetrics.project.controller.vo.response.PipelineStagesResponse
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

@WebMvcTest(controllers = [PipelineController::class])
internal class PipelineControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var pipelineApplicationService: PipelineApplicationService

    private val projectId = "projectId"

    @Test
    internal fun `should return OK when verify pipeline successfully`() {
        val pipelineVerificationRequest = buildPipelineVerificationRequest()
        Mockito.doNothing().`when`(pipelineApplicationService).verifyPipelineConfiguration(anyObject())

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/pipeline/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectMapper().writeValueAsString(pipelineVerificationRequest))
        ).andExpect(status().isOk)
    }

    @Test
    internal fun `should return 200 when create pipeline successfully`() {
        val pipelineRequest = buildPipelineRequest()
        val pipeline = buildPipeline()
        val projectId = pipeline.projectId
        `when`(pipelineApplicationService.createPipeline(anyObject())).thenReturn(pipeline)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/project/$projectId/pipeline")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectMapper().writeValueAsString(pipelineRequest))
        )
            .andExpect(status().isCreated)
    }

    @Test
    internal fun `should return 200 when update pipeline successfully`() {
        val pipeline = buildPipeline()
        val pipelineId = pipeline.id
        val projectId = pipeline.projectId
        `when`(pipelineApplicationService.updatePipeline(anyObject())).thenReturn(pipeline)

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/project/${projectId}/pipeline/${pipelineId}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectMapper().writeValueAsString(buildPipelineRequest()))
        )
            .andExpect(status().isOk)
    }

    @Test
    internal fun `should return 200 when get pipeline successfully`() {
        val pipeline = buildPipeline()
        val pipelineId = pipeline.id
        val projectId = pipeline.projectId
        `when`(pipelineApplicationService.getPipeline(projectId, pipelineId)).thenReturn(pipeline)

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/project/${projectId}/pipeline/${pipelineId}")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
    }

    @Test
    internal fun `should return OK when delete pipeline successfully`() {
        val pipelineId = "pipelineId"
        Mockito.doNothing().`when`(pipelineApplicationService).deletePipeline(projectId, pipelineId)

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/project/${projectId}/pipeline/${pipelineId}")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk)
    }

    @Test
    internal fun `should return PipelineStagesResponse when get pipeline stages successfully`() {
        val pipelineId = "pipelineId"
        val pipelineName = "pipelineName"
        val pipelineStagesResponse = PipelineStagesResponse(pipelineId, pipelineName, listOf("some stage"))
        `when`(pipelineApplicationService.getPipelineStages(projectId)).thenReturn(listOf(pipelineStagesResponse))
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/project/$projectId/pipelines-stages")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$[0].pipelineId").value(pipelineId))
            .andExpect(jsonPath("$[0].pipelineName").value(pipelineName))
    }
}