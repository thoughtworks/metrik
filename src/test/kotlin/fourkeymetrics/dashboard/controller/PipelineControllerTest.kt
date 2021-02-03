package fourkeymetrics.dashboard.controller

import com.fasterxml.jackson.databind.ObjectMapper
import fourkeymetrics.dashboard.buildPipelineRequest
import fourkeymetrics.dashboard.buildPipelineResponse
import fourkeymetrics.dashboard.buildPipelineVerificationRequest
import fourkeymetrics.dashboard.controller.applicationservice.PipelineApplicationService
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
        Mockito.doNothing().`when`(pipelineApplicationService).verifyPipeline(pipelineVerificationRequest)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/pipeline/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectMapper().writeValueAsString(pipelineVerificationRequest))
        ).andExpect(status().isOk)
    }

    @Test
    internal fun `should return PipelineResponse when create pipeline successfully`() {
        val pipelineRequest = buildPipelineRequest()
        val pipelineResponse = buildPipelineResponse()
        `when`(pipelineApplicationService.createPipeline(dashboardId, pipelineRequest)).thenReturn(pipelineResponse)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/dashboard/$dashboardId/pipeline")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectMapper().writeValueAsString(pipelineRequest))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(pipelineResponse.id))
            .andExpect(jsonPath("$.name").value(pipelineResponse.name))
            .andExpect(jsonPath("$.url").value(pipelineResponse.url))
            .andExpect(jsonPath("$.username").value(pipelineResponse.username))
            .andExpect(jsonPath("$.credential").value(pipelineResponse.credential))
            .andExpect(jsonPath("$.type").value(pipelineResponse.type.toString()))
    }

    @Test
    internal fun `should return PipelineResponse when update pipeline successfully`() {
        val pipelineRequest = buildPipelineRequest()
        val pipelineResponse = buildPipelineResponse()
        val pipelineId = pipelineResponse.id
        `when`(pipelineApplicationService.updatePipeline(dashboardId, pipelineId, pipelineRequest)).thenReturn(
            pipelineResponse
        )

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/dashboard/${dashboardId}/pipeline/${pipelineId}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectMapper().writeValueAsString(pipelineRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(pipelineResponse.id))
            .andExpect(jsonPath("$.name").value(pipelineResponse.name))
            .andExpect(jsonPath("$.url").value(pipelineResponse.url))
            .andExpect(jsonPath("$.username").value(pipelineResponse.username))
            .andExpect(jsonPath("$.credential").value(pipelineResponse.credential))
            .andExpect(jsonPath("$.type").value(pipelineResponse.type.toString()))
    }

    @Test
    internal fun `should return PipelineResponse when get pipeline successfully`() {
        val pipelineResponse = buildPipelineResponse()
        val pipelineId = pipelineResponse.id
        `when`(pipelineApplicationService.getPipeline(dashboardId, pipelineId)).thenReturn(pipelineResponse)

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/dashboard/${dashboardId}/pipeline/${pipelineId}")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(pipelineResponse.id))
            .andExpect(jsonPath("$.name").value(pipelineResponse.name))
            .andExpect(jsonPath("$.url").value(pipelineResponse.url))
            .andExpect(jsonPath("$.username").value(pipelineResponse.username))
            .andExpect(jsonPath("$.credential").value(pipelineResponse.credential))
            .andExpect(jsonPath("$.type").value(pipelineResponse.type.toString()))
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


}