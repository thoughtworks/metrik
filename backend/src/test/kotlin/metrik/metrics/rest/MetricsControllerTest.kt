package metrik.metrics.rest

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import metrik.metrics.domain.model.CalculationPeriod
import metrik.metrics.domain.model.LEVEL
import metrik.metrics.domain.model.Metrics
import metrik.metrics.rest.vo.FourKeyMetricsResponse
import metrik.metrics.rest.vo.MetricsInfo
import metrik.metrics.rest.vo.MetricsQueryRequest
import metrik.metrics.rest.vo.PipelineStageRequest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath

@WebMvcTest(controllers = [MetricsController::class])
internal class MetricsControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var metricsApplicationService: MetricsApplicationService

    @Test
    fun `should return metrics response when call api`() {
        val pipelineA = PipelineStageRequest("pipelineA", "stageA")
        val pipelineB = PipelineStageRequest("pipelineB", "stageB")
        val startTime = 1609459200000L
        val endTime = 1611964800000L
        val metricUnit = CalculationPeriod.Fortnightly
        val level = LEVEL.ELITE
        val value = 10.2
        val requestBody = MetricsQueryRequest(startTime, endTime, metricUnit, listOf(pipelineA, pipelineB))

        val metric = Metrics(value, startTime, endTime)
        val summary = Metrics(value, level, startTime, endTime)
        val metrics = MetricsInfo(summary, listOf(metric, metric))
        val expectedResponse = FourKeyMetricsResponse(
            deploymentFrequency = metrics,
            leadTimeForChange = metrics,
            changeFailureRate = metrics,
            meanTimeToRestore = metrics
        )
        every {
            metricsApplicationService.calculateFourKeyMetrics(
                requestBody.pipelineStages,
                requestBody.startTime,
                requestBody.endTime,
                requestBody.unit,
            )
        } returns expectedResponse

        mockMvc.perform(
            post("/api/pipeline/metrics")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectMapper().writeValueAsString(requestBody))
        )
            .andExpect(jsonPath("$.leadTimeForChange.summary.level").value("ELITE"))
            .andExpect(jsonPath("$.leadTimeForChange.summary.value").value(value))
            .andExpect(jsonPath("$.leadTimeForChange.summary.startTimestamp").value(startTime))
            .andExpect(jsonPath("$.leadTimeForChange.summary.endTimestamp").value(endTime))
            .andExpect(jsonPath("$.leadTimeForChange.details.length()").value(2))
            .andExpect(jsonPath("$.leadTimeForChange.details[0].startTimestamp").value(startTime))
            .andExpect(jsonPath("$.leadTimeForChange.details[0].endTimestamp").value(endTime))
            .andExpect(jsonPath("$.leadTimeForChange.details[0].value").value(value))
            .andExpect(jsonPath("$.changeFailureRate").isNotEmpty)
            .andExpect(jsonPath("$.meanTimeToRestore").isNotEmpty)
    }

    @Test
    fun `should return metrics response when call get api`() {
        val pipelineId = "test pipeline Id"
        val targetStage = "Deploy to UAT"
        val startTime = 1609459200000L
        val endTime = 1611964800000L
        val metricUnit = CalculationPeriod.Fortnightly
        val level = LEVEL.ELITE
        val value = 10.2

        val metric = Metrics(value, startTime, endTime)
        val summary = Metrics(value, level, startTime, endTime)
        val metrics = MetricsInfo(summary, listOf(metric, metric))
        val expectedResponse = FourKeyMetricsResponse(
            deploymentFrequency = metrics,
            leadTimeForChange = metrics,
            changeFailureRate = metrics,
            meanTimeToRestore = metrics
        )
        every {
            metricsApplicationService.calculateFourKeyMetrics(
                listOf(PipelineStageRequest(pipelineId, targetStage)),
                startTime,
                endTime,
                metricUnit
            )
        } returns expectedResponse

        mockMvc.perform(
            get("/api/pipeline/metrics")
                .param("pipelineId", pipelineId)
                .param("targetStage", targetStage)
                .param("startTime", startTime.toString())
                .param("endTime", endTime.toString())
                .param("unit", metricUnit.toString())
        )
            .andExpect(jsonPath("$.leadTimeForChange.summary.level").value("ELITE"))
            .andExpect(jsonPath("$.leadTimeForChange.summary.value").value(value))
            .andExpect(jsonPath("$.leadTimeForChange.summary.startTimestamp").value(startTime))
            .andExpect(jsonPath("$.leadTimeForChange.summary.endTimestamp").value(endTime))
            .andExpect(jsonPath("$.leadTimeForChange.details.length()").value(2))
            .andExpect(jsonPath("$.leadTimeForChange.details[0].startTimestamp").value(startTime))
            .andExpect(jsonPath("$.leadTimeForChange.details[0].endTimestamp").value(endTime))
            .andExpect(jsonPath("$.leadTimeForChange.details[0].value").value(value))
            .andExpect(jsonPath("$.changeFailureRate").isNotEmpty)
            .andExpect(jsonPath("$.meanTimeToRestore").isNotEmpty)
    }
}