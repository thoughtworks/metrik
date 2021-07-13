package metrik.project.rest

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import metrik.MockitoHelper.anyObject
import metrik.project.buildBambooPipelineRequest
import metrik.project.buildBambooPipelineVerificationRequest
import metrik.project.buildJenkinsPipelineRequest
import metrik.project.buildJenkinsPipelineVerificationRequest
import metrik.project.buildPipeline
import metrik.project.domain.model.PipelineType
import metrik.project.rest.applicationservice.PipelineApplicationService
import metrik.project.rest.vo.response.PipelineStagesResponse
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

    private val objectMapper = jacksonObjectMapper()

    private val projectId = "projectId"

    @Test
    internal fun `should return OK when verify Jenkins pipeline successfully`() {
        val pipelineVerificationRequest = buildJenkinsPipelineVerificationRequest()
        Mockito.doNothing().`when`(pipelineApplicationService).verifyPipelineConfiguration(anyObject())

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/pipeline/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pipelineVerificationRequest))
        ).andExpect(status().isOk)
    }

    @Test
    internal fun `should return OK when verify Bamboo pipeline successfully`() {
        val pipelineVerificationRequest = buildBambooPipelineVerificationRequest()
        Mockito.doNothing().`when`(pipelineApplicationService).verifyPipelineConfiguration(anyObject())

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/pipeline/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pipelineVerificationRequest))
        ).andExpect(status().isOk)
    }


    @Test
    internal fun `should return 200 when create Jenkins pipeline successfully`() {
        val pipelineRequest = buildJenkinsPipelineRequest()
        val pipeline = buildPipeline()
        val projectId = pipeline.projectId
        `when`(pipelineApplicationService.createPipeline(anyObject())).thenReturn(pipeline)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/project/$projectId/pipeline")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pipelineRequest))
        )
            .andExpect(status().isCreated)
    }


    @Test
    internal fun `should return 200 when create Bamboo pipeline successfully`() {
        val pipelineRequest = buildBambooPipelineRequest()
        val pipeline = buildPipeline()
        val projectId = pipeline.projectId
        `when`(pipelineApplicationService.createPipeline(anyObject())).thenReturn(pipeline)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/project/$projectId/pipeline")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pipelineRequest))
        )
            .andExpect(status().isCreated)
    }


    @Test
    internal fun `should return 200 when update Jenkins pipeline successfully`() {
        val pipeline = buildPipeline()
        val pipelineId = pipeline.id
        val projectId = pipeline.projectId
        `when`(pipelineApplicationService.updatePipeline(anyObject())).thenReturn(pipeline)

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/project/${projectId}/pipeline/${pipelineId}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(buildJenkinsPipelineRequest()))
        )
            .andExpect(status().isOk)
    }

    @Test
    internal fun `should return 200 when update Bamboo pipeline successfully`() {
        val pipeline = buildPipeline(type = PipelineType.BAMBOO)
        val pipelineId = pipeline.id
        val projectId = pipeline.projectId
        `when`(pipelineApplicationService.updatePipeline(anyObject())).thenReturn(pipeline)

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/project/${projectId}/pipeline/${pipelineId}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(buildJenkinsPipelineRequest()))
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