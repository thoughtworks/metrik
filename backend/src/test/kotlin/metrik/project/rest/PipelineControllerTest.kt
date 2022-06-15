package metrik.project.rest

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.justRun
import metrik.project.TestFixture.buildBambooPipelineRequest
import metrik.project.TestFixture.buildBambooPipelineVerificationRequest
import metrik.project.TestFixture.buildJenkinsPipelineRequest
import metrik.project.TestFixture.buildJenkinsPipelineVerificationRequest
import metrik.project.TestFixture.buildPipeline
import metrik.project.domain.model.PipelineType
import metrik.project.rest.applicationservice.PipelineApplicationService
import metrik.project.rest.vo.response.PipelineStagesResponse
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(controllers = [PipelineController::class])
internal class PipelineControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var pipelineApplicationService: PipelineApplicationService

    private val objectMapper = jacksonObjectMapper()

    private val projectId = "projectId"

    @Test
    fun `should return OK when verify Jenkins pipeline successfully`() {
        val pipelineVerificationRequest = buildJenkinsPipelineVerificationRequest()
        justRun { pipelineApplicationService.verifyPipelineConfiguration(any()) }

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/pipeline/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pipelineVerificationRequest))
        ).andExpect(status().isOk)
    }

    @Test
    fun `should return OK when verify Bamboo pipeline successfully`() {
        val pipelineVerificationRequest = buildBambooPipelineVerificationRequest()
        justRun { pipelineApplicationService.verifyPipelineConfiguration(any()) }

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/pipeline/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pipelineVerificationRequest))
        ).andExpect(status().isOk)
    }

    @Test
    fun `should return 200 when create Jenkins pipeline successfully`() {
        val pipelineRequest = buildJenkinsPipelineRequest()
        val pipeline = buildPipeline()
        val projectId = pipeline.projectId
        every { pipelineApplicationService.createPipeline(any()) } returns pipeline

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/project/$projectId/pipeline")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pipelineRequest))
        )
            .andExpect(status().isCreated)
    }

    @Test
    fun `should return 200 when create Bamboo pipeline successfully`() {
        val pipelineRequest = buildBambooPipelineRequest()
        val pipeline = buildPipeline()
        val projectId = pipeline.projectId
        every { pipelineApplicationService.createPipeline(any()) } returns pipeline

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/project/$projectId/pipeline")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pipelineRequest))
        )
            .andExpect(status().isCreated)
    }

    @Test
    fun `should return 200 when update Jenkins pipeline successfully`() {
        val pipeline = buildPipeline()
        val pipelineId = pipeline.id
        val projectId = pipeline.projectId
        every { pipelineApplicationService.updatePipeline(any()) } returns pipeline

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/project/$projectId/pipeline/$pipelineId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(buildJenkinsPipelineRequest()))
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `should return 200 when update Bamboo pipeline successfully`() {
        val pipeline = buildPipeline(type = PipelineType.BAMBOO)
        val pipelineId = pipeline.id
        val projectId = pipeline.projectId
        every { pipelineApplicationService.updatePipeline(any()) } returns pipeline

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/project/$projectId/pipeline/$pipelineId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(buildJenkinsPipelineRequest()))
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `should return 200 when get pipeline successfully`() {
        val pipeline = buildPipeline()
        val pipelineId = pipeline.id
        val projectId = pipeline.projectId
        every { pipelineApplicationService.getPipeline(projectId, pipelineId) } returns pipeline

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/project/$projectId/pipeline/$pipelineId")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `should return OK when delete pipeline successfully`() {
        val pipelineId = "pipelineId"
        justRun { pipelineApplicationService.deletePipeline(projectId, pipelineId) }

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/project/$projectId/pipeline/$pipelineId")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk)
    }

    @Test
    fun `should return PipelineStagesResponse when get pipeline stages successfully`() {
        val pipelineId = "pipelineId"
        val pipelineName = "pipelineName"
        val pipelineStagesResponse = PipelineStagesResponse(pipelineId, pipelineName, listOf("some stage"))
        every { pipelineApplicationService.getPipelineStages(projectId) } returns listOf(pipelineStagesResponse)
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/project/$projectId/pipelines-stages")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$[0].pipelineId").value(pipelineId))
            .andExpect(jsonPath("$[0].pipelineName").value(pipelineName))
    }
}
