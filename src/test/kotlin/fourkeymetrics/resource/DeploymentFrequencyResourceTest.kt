package fourkeymetrics.resource

import fourkeymetrics.service.DeploymentFrequencyService
import fourkeymetrics.pipeline.Pipeline
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@WebMvcTest(controllers = [DeploymentFrequencyResource::class])
internal class DeploymentFrequencyResourceTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var deploymentFrequencyService: DeploymentFrequencyService

    @MockBean
    private lateinit var pipeline: Pipeline

    @Test
    internal fun `should get deployment count given time range and pipeline information`() {
        val dashboardId = "dashboard ID"
        val pipelineId = "test pipeline ID"
        val targetStage = "UAT"
        val startTimestamp = 1609459200L
        val endTimestamp = 1611964800L

        `when`(pipeline.hasPipeline(dashboardId, pipelineId)).thenReturn(true)
        `when`(deploymentFrequencyService.getDeploymentCount(targetStage, pipelineId, startTimestamp, endTimestamp))
            .thenReturn(30)
        `when`(pipeline.hasStageInTimeRange(pipelineId, targetStage, startTimestamp, endTimestamp)).thenReturn(true)


        mockMvc.perform(get("/api/deployment-frequency")
            .param("dashboardId", dashboardId)
            .param("pipelineId", pipelineId)
            .param("targetStage", targetStage)
            .param("startTimestamp", startTimestamp.toString())
            .param("endTimestamp", endTimestamp.toString()))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.deploymentCount").value(30))
    }

    @Test
    internal fun `should return bad request given start time is after end time when get deployment count`() {
        val dashboardId = "dashboard ID"
        val pipelineId = "test pipeline ID"
        val targetStage = "UAT"
        val startTimestamp = 1611964800L
        val endTimestamp = 1609459200L

        `when`(pipeline.hasPipeline(dashboardId, pipelineId)).thenReturn(true)
        `when`(pipeline.hasStageInTimeRange(pipelineId, targetStage, startTimestamp, endTimestamp)).thenReturn(true)

        mockMvc.perform(get("/api/deployment-frequency")
            .param("dashboardId", dashboardId)
            .param("pipelineId", pipelineId)
            .param("targetStage", targetStage)
            .param("startTimestamp", startTimestamp.toString())
            .param("endTimestamp", endTimestamp.toString()))
            .andExpect(status().isBadRequest)
    }

    @Test
    internal fun `should return bad request given pipeline ID does not exist when get deployment count`() {
        val dashboardId = "dashboard ID"
        val pipelineId = "invalid pipeline ID"
        val targetStage = "UAT"
        val startTimestamp = 1609459200L
        val endTimestamp = 1611964800L

        `when`(pipeline.hasPipeline(dashboardId, pipelineId)).thenReturn(false)
        `when`(pipeline.hasStageInTimeRange(pipelineId, targetStage, startTimestamp, endTimestamp)).thenReturn(true)

        mockMvc.perform(get("/api/deployment-frequency")
            .param("dashboardId", dashboardId)
            .param("pipelineId", pipelineId)
            .param("targetStage", targetStage)
            .param("startTimestamp", startTimestamp.toString())
            .param("endTimestamp", endTimestamp.toString()))
            .andExpect(status().isBadRequest)
    }

    @Test
    internal fun `should return bad request given Stage name does not exist when get deployment count`() {
        val dashboardId = "dashboard ID"
        val pipelineId = "pipeline ID"
        val targetStage = "invalid stage"
        val startTimestamp = 1609459200L
        val endTimestamp = 1611964800L

        `when`(pipeline.hasPipeline(dashboardId, pipelineId)).thenReturn(true)
        `when`(pipeline.hasStageInTimeRange(pipelineId, targetStage, startTimestamp, endTimestamp)).thenReturn(false)

        mockMvc.perform(get("/api/deployment-frequency")
            .param("dashboardId", dashboardId)
            .param("pipelineId", pipelineId)
            .param("targetStage", targetStage)
            .param("startTimestamp", startTimestamp.toString())
            .param("endTimestamp", endTimestamp.toString()))
            .andExpect(status().isBadRequest)
    }
}