package fourkeymetrics.metrics.controller

import fourkeymetrics.metrics.model.LEVEL
import fourkeymetrics.metrics.model.MetricsUnit
import fourkeymetrics.metrics.controller.vo.FourKeyMetricsResponse
import fourkeymetrics.metrics.model.Metrics
import fourkeymetrics.metrics.controller.vo.MetricsInfo
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath

@WebMvcTest(controllers = [MetricsController::class])
internal class MetricsControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var metricsApplicationService: MetricsApplicationService

    @Test
    internal fun `should return metrics response when call api`() {
        val pipelineId = "test pipeline Id"
        val targetStage = "Deploy to UAT"
        val startTime = 1609459200000L
        val endTime = 1611964800000L
        val metricUnit = MetricsUnit.Fortnightly
        val level = LEVEL.ELITE
        val value = 10.2

        val metric = Metrics(value, startTime, endTime)
        val summary = Metrics(value, level, startTime, endTime)
        val metrics = MetricsInfo(summary, listOf(metric, metric))
        val expectedResponse = FourKeyMetricsResponse(
            leadTimeForChange = metrics,
            changeFailureRate = metrics
        )
        `when`(
            metricsApplicationService.retrieve4KeyMetrics(
                pipelineId,
                targetStage,
                startTime,
                endTime,
                metricUnit
            )
        ).thenReturn(expectedResponse)

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
    }
}