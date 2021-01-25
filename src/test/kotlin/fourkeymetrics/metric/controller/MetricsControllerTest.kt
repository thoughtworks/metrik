package fourkeymetrics.metric.controller

import fourkeymetrics.metric.model.LEVEL
import fourkeymetrics.metric.model.MetricUnit
import fourkeymetrics.metric.controller.vo.FourKeyMetricsResponse
import fourkeymetrics.metric.model.Metric
import fourkeymetrics.metric.controller.vo.Metrics
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
        val dashboardId = "test dashboard Id"
        val pipelineId = "test pipeline Id"
        val targetStage = "Deploy to UAT"
        val startTime = 1609459200000L
        val endTime = 1611964800000L
        val metricUnit = MetricUnit.Fortnightly
        val level = LEVEL.ELITE
        val value = 10.2

        val metric = Metric(value, startTime, endTime)
        val summary = Metric(value, level, startTime, endTime)
        val metrics = Metrics(summary, listOf(metric, metric))
        val expectedResponse = FourKeyMetricsResponse(
            leadTimeForChange = metrics,
            changeFailureRate = metrics
        )
        `when`(
            metricsApplicationService.retrieve4KeyMetrics(
                dashboardId,
                pipelineId,
                targetStage,
                startTime,
                endTime,
                metricUnit
            )
        ).thenReturn(expectedResponse)

        mockMvc.perform(
            get("/api/metrics")
                .param("dashboardId", dashboardId)
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