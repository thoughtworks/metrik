package fourkeymetrics.resource.metrics

import fourkeymetrics.model.MetricUnit
import fourkeymetrics.resource.MetricsResource
import fourkeymetrics.resource.metrics.representation.FourKeyMetricsResponse
import fourkeymetrics.resource.metrics.representation.LEVEL
import fourkeymetrics.model.Metric
import fourkeymetrics.resource.metrics.representation.MetricWithLevel
import fourkeymetrics.resource.metrics.representation.Metrics
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath

@WebMvcTest(controllers = [MetricsResource::class])
internal class MetricsResourceTest {
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
        val metricWithLevel = MetricWithLevel(level, metric)
        val metrics = Metrics(metricWithLevel, listOf(metric, metric))
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
            .andExpect(jsonPath("$.leadTimeForChange.summary.metric.value").value(value))
            .andExpect(jsonPath("$.leadTimeForChange.summary.metric.startTimestamp").value(startTime))
            .andExpect(jsonPath("$.leadTimeForChange.summary.metric.endTimestamp").value(endTime))
            .andExpect(jsonPath("$.leadTimeForChange.details.length()").value(2))
            .andExpect(jsonPath("$.leadTimeForChange.details[0].startTimestamp").value(startTime))
            .andExpect(jsonPath("$.leadTimeForChange.details[0].endTimestamp").value(endTime))
            .andExpect(jsonPath("$.leadTimeForChange.details[0].value").value(value))
            .andExpect(jsonPath("$.changeFailureRate").isNotEmpty)
    }
}