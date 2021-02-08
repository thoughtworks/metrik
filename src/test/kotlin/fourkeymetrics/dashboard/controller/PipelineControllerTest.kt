package fourkeymetrics.dashboard.controller

import com.fasterxml.jackson.databind.ObjectMapper
import fourkeymetrics.MockitoHelper.anyObject
import fourkeymetrics.dashboard.buildPipeline
import fourkeymetrics.dashboard.buildPipelineRequest
import fourkeymetrics.dashboard.buildPipelineVerificationRequest
import fourkeymetrics.dashboard.controller.applicationservice.PipelineApplicationService
import fourkeymetrics.dashboard.controller.vo.response.PipelineStagesResponse
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

    private val dashboardId = "dashboardId"

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
    internal fun `should return PipelineResponse when create pipeline successfully`() {
        val pipelineRequest = buildPipelineRequest()
        val pipeline = buildPipeline()
        val dashboardId = pipeline.dashboardId
        `when`(pipelineApplicationService.createPipeline(anyObject())).thenReturn(pipeline)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/dashboard/$dashboardId/pipeline")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectMapper().writeValueAsString(pipelineRequest))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(pipeline.id))
            .andExpect(jsonPath("$.name").value(pipeline.name))
            .andExpect(jsonPath("$.url").value(pipeline.url))
            .andExpect(jsonPath("$.username").value(pipeline.username))
            .andExpect(jsonPath("$.credential").value(pipeline.credential))
            .andExpect(jsonPath("$.type").value(pipeline.type.toString()))
    }

    @Test
    internal fun `should return PipelineResponse when update pipeline successfully`() {
        val pipeline = buildPipeline()
        val pipelineId = pipeline.id
        val dashboardId = pipeline.dashboardId
        `when`(pipelineApplicationService.updatePipeline(anyObject())).thenReturn(pipeline)

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/dashboard/${dashboardId}/pipeline/${pipelineId}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectMapper().writeValueAsString(buildPipelineRequest()))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(pipeline.id))
            .andExpect(jsonPath("$.name").value(pipeline.name))
            .andExpect(jsonPath("$.url").value(pipeline.url))
            .andExpect(jsonPath("$.username").value(pipeline.username))
            .andExpect(jsonPath("$.credential").value(pipeline.credential))
            .andExpect(jsonPath("$.type").value(pipeline.type.toString()))
    }

    @Test
    internal fun `should return PipelineResponse when get pipeline successfully`() {
        val pipeline = buildPipeline()
        val pipelineId = pipeline.id
        val dashboardId = pipeline.dashboardId
        `when`(pipelineApplicationService.getPipeline(dashboardId, pipelineId)).thenReturn(pipeline)

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/dashboard/${dashboardId}/pipeline/${pipelineId}")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(pipeline.id))
            .andExpect(jsonPath("$.name").value(pipeline.name))
            .andExpect(jsonPath("$.url").value(pipeline.url))
            .andExpect(jsonPath("$.username").value(pipeline.username))
            .andExpect(jsonPath("$.credential").value(pipeline.credential))
            .andExpect(jsonPath("$.type").value(pipeline.type.toString()))
    }

    @Test
    internal fun `should return OK when delete pipeline successfully`() {
        val pipelineId = "pipelineId"
        Mockito.doNothing().`when`(pipelineApplicationService).deletePipeline(dashboardId, pipelineId)

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/dashboard/${dashboardId}/pipeline/${pipelineId}")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk)
    }

    @Test
    internal fun `should return PipelineStagesResponse when get pipeline stages successfully`() {
        val pipelineId = "pipelineId"
        val pipelineName = "pipelineName"
        val pipelineStagesResponse = PipelineStagesResponse(pipelineId, pipelineName, listOf("some stage"))
        `when`(pipelineApplicationService.getPipelineStages(dashboardId)).thenReturn(listOf(pipelineStagesResponse))
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/dashboard/$dashboardId/pipelines-stages")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$[0].pipelineId").value(pipelineId))
            .andExpect(jsonPath("$[0].pipelineName").value(pipelineName))
    }
}