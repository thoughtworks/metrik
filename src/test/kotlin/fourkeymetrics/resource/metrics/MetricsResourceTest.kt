package fourkeymetrics.resource.metrics

import fourkeymetrics.model.MetricUnit
import fourkeymetrics.resource.MetricsResource
import fourkeymetrics.resource.metrics.representation.FourKeyMetricsResponse
import fourkeymetrics.resource.metrics.representation.LEVEL
import fourkeymetrics.resource.metrics.representation.Metric
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
        val startTime = 1609459200L
        val endTime = 1611964800L
        val metricUnit = MetricUnit.Fortnightly
        val level = LEVEL.ELITE
        val value = "10.2"

        val metric = Metric(level, value, startTime, endTime)
        val metrics = Metrics(metric, listOf(metric, metric))
        val expectedResponse = FourKeyMetricsResponse(
            deploymentFrequency = metrics,
            leadTimeForChange = metrics,
            timeToRestoreService = metrics,
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
            .andExpect(jsonPath("$.deploymentFrequency.summary.level").value("ELITE"))
            .andExpect(jsonPath("$.deploymentFrequency.summary.value").value(value))
            .andExpect(jsonPath("$.deploymentFrequency.summary.from").value(startTime))
            .andExpect(jsonPath("$.deploymentFrequency.summary.to").value(endTime))
            .andExpect(jsonPath("$.deploymentFrequency.details.length()").value(2))
            .andExpect(jsonPath("$.deploymentFrequency.details[0].from").value(startTime))
            .andExpect(jsonPath("$.deploymentFrequency.details[0].to").value(endTime))
            .andExpect(jsonPath("$.deploymentFrequency.details[0].value").value(value))
            .andExpect(jsonPath("$.deploymentFrequency.details[0].level").value("ELITE"))
            .andExpect(jsonPath("$.leadTimeForChange").isNotEmpty)
            .andExpect(jsonPath("$.timeToRestoreService").isNotEmpty)
            .andExpect(jsonPath("$.changeFailureRate").isNotEmpty)
    }
}