package fourkeymetrics.resource

import fourkeymetrics.service.DeploymentFrequencyService
import fourkeymetrics.service.PipelineService
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
    private lateinit var pipelineService: PipelineService

    @Test
    internal fun `should get deployment count given time range and pipeline information`() {
        val pipelineName = "test pipeline name"
        val branch = "master"
        val targetStage = "UAT"
        val startTime = 1609459200L
        val endTime = 1611964800L

        `when`(pipelineService.hasPipeline(pipelineName)).thenReturn(true)
        `when`(pipelineService.hasPipeline(pipelineName, branch)).thenReturn(true)
        `when`(deploymentFrequencyService.getDeploymentCount(pipelineName, targetStage, startTime, endTime))
                .thenReturn(30)
        `when`(pipelineService.hasStage(pipelineName, targetStage)).thenReturn(true)


        mockMvc.perform(get("/api/deployment-frequency")
                .param("pipelineName", pipelineName)
                .param("branch", branch)
                .param("targetStage", targetStage)
                .param("startTime", startTime.toString())
                .param("endTime", endTime.toString()))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.deploymentCount").value(30))
    }

    @Test
    internal fun `should return bad request given start time is after end time when get deployment count`() {
        val pipelineName = "test pipeline name"
        val targetStage = "UAT"
        val startTime = 1611964800L
        val endTime = 1609459200L

        `when`(pipelineService.hasPipeline(pipelineName)).thenReturn(true)
        `when`(pipelineService.hasStage(pipelineName, targetStage)).thenReturn(true)

        mockMvc.perform(get("/api/deployment-frequency")
                .param("pipelineName", pipelineName)
                .param("targetStage", targetStage)
                .param("startTime", startTime.toString())
                .param("endTime", endTime.toString()))
                .andExpect(status().isBadRequest)
    }

    @Test
    internal fun `should return bad request given pipeline name does not exist when get deployment count`() {
        val pipelineName = "invalid pipeline name"
        val targetStage = "UAT"
        val startTime = 1609459200L
        val endTime = 1611964800L

        `when`(pipelineService.hasPipeline(pipelineName)).thenReturn(false)
        `when`(pipelineService.hasStage(pipelineName, targetStage)).thenReturn(true)

        mockMvc.perform(get("/api/deployment-frequency")
                .param("pipelineName", pipelineName)
                .param("targetStage", targetStage)
                .param("startTime", startTime.toString())
                .param("endTime", endTime.toString()))
                .andExpect(status().isBadRequest)
    }

    @Test
    internal fun `should return bad request given branch does not exist when get deployment count`() {
        val pipelineName = "pipeline name"
        val branch = "invalid branch"
        val targetStage = "UAT"
        val startTime = 1609459200L
        val endTime = 1611964800L

        `when`(pipelineService.hasPipeline(pipelineName)).thenReturn(true)
        `when`(pipelineService.hasPipeline(pipelineName, branch)).thenReturn(false)
        `when`(pipelineService.hasStage(pipelineName, targetStage)).thenReturn(true)

        mockMvc.perform(get("/api/deployment-frequency")
                .param("pipelineName", pipelineName)
                .param("branch", branch)
                .param("targetStage", targetStage)
                .param("startTime", startTime.toString())
                .param("endTime", endTime.toString()))
                .andExpect(status().isBadRequest)
    }

    @Test
    internal fun `should return bad request given Stage name does not exist when get deployment count`() {
        val pipelineName = "pipeline name"
        val branch = "master"
        val targetStage = "invalid stage"
        val startTime = 1609459200L
        val endTime = 1611964800L

        `when`(pipelineService.hasPipeline(pipelineName, branch)).thenReturn(true)
        `when`(pipelineService.hasStage(pipelineName, targetStage)).thenReturn(false)

        mockMvc.perform(get("/api/deployment-frequency")
                .param("pipelineName", pipelineName)
                .param("branch", branch)
                .param("targetStage", targetStage)
                .param("startTime", startTime.toString())
                .param("endTime", endTime.toString()))
                .andExpect(status().isBadRequest)
    }
}