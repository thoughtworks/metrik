package fourkeymetrics.dashboard.controller

import com.fasterxml.jackson.databind.ObjectMapper
import fourkeymetrics.dashboard.controller.applicationservice.PipelineApplicationService
import fourkeymetrics.dashboard.controller.vo.request.PipelineRequest
import fourkeymetrics.dashboard.controller.vo.request.PipelineVerificationRequest
import fourkeymetrics.dashboard.controller.vo.response.PipelineResponse
import fourkeymetrics.dashboard.model.PipelineType
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
    private val pipelineId = "pipelineId"

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
        Mockito.doNothing().`when`(pipelineApplicationService).deletePipeline(dashboardId, pipelineId)

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/dashboard/${dashboardId}/pipeline/${pipelineId}")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk)
    }

    private fun buildPipelineResponse(): PipelineResponse {
        val pipelineResponse = PipelineResponse(
            id = pipelineId,
            name = "name",
            username = "username",
            credential = "credential",
            url = "url"
        )
        return pipelineResponse.copy()
    }

    private fun buildPipelineRequest() =
        PipelineRequest(name = "pipeline", username = "username", credential = "credential", url = "url").copy()

    private fun buildPipelineVerificationRequest() = PipelineVerificationRequest(
        "url",
        "username",
        "credential",
        PipelineType.JENKINS
    ).copy()
}