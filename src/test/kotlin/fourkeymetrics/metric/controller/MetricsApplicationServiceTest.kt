package fourkeymetrics.metric.controller

import fourkeymetrics.datasource.pipeline.builddata.BuildRepository
import fourkeymetrics.metric.calculator.ChangeFailureRateCalculator
import fourkeymetrics.metric.calculator.LeadTimeForChangeCalculator
import fourkeymetrics.metric.model.Build
import fourkeymetrics.metric.model.LEVEL
import fourkeymetrics.metric.model.Metric
import fourkeymetrics.metric.model.MetricUnit
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
internal class MetricsApplicationServiceTest {

    @Mock
    private lateinit var changeFailureRateCalculator: ChangeFailureRateCalculator

    @Mock
    private lateinit var leadTimeForChangeCalculator: LeadTimeForChangeCalculator

    @Mock
    private lateinit var buildRepository: BuildRepository

    @Mock
    private lateinit var dateTimeUtils: DateTimeUtils

    @InjectMocks
    private lateinit var metricsApplicationService: MetricsApplicationService


    @Test
    fun `should return correct metric response`() {

        val pipelineId = "pipelineId"
        val targetStage = "deploy to prod"
        val startTimestamp: Long = 1
        val endTimestamp: Long = 10
        val expectedBuilds = emptyList<Build>()
        val unit = MetricUnit.Fortnightly

        `when`(buildRepository.getAllBuilds(pipelineId)).thenReturn(expectedBuilds)
        `when`(dateTimeUtils.splitTimeRange(startTimestamp, endTimestamp, unit))
            .thenReturn(listOf(Pair(1, 5), Pair(6, 10)))
        `when`(changeFailureRateCalculator.calculateValue(expectedBuilds, 1, 10, targetStage))
            .thenReturn(0.5)
        `when`(changeFailureRateCalculator.calculateLevel(0.5)).thenReturn(LEVEL.LOW)
        `when`(changeFailureRateCalculator.calculateValue(expectedBuilds, 1, 5, targetStage))
            .thenReturn(0.4)
        `when`(changeFailureRateCalculator.calculateValue(expectedBuilds, 6, 10, targetStage))
            .thenReturn(0.6)

        `when`(leadTimeForChangeCalculator.calculateValue(expectedBuilds, 1, 10, targetStage))
            .thenReturn(2.0)
        `when`(leadTimeForChangeCalculator.calculateLevel(2.0)).thenReturn(LEVEL.LOW)
        `when`(leadTimeForChangeCalculator.calculateValue(expectedBuilds, 1, 5, targetStage))
            .thenReturn(1.0)
        `when`(leadTimeForChangeCalculator.calculateValue(expectedBuilds, 6, 10, targetStage))
            .thenReturn(3.0)

        val fourKeyMetricsResponse = metricsApplicationService.retrieve4KeyMetrics(
            pipelineId, targetStage, startTimestamp, endTimestamp, unit
        )

        assertEquals(
            fourKeyMetricsResponse.leadTimeForChange.summary,
            Metric(2.0, LEVEL.LOW, 1, 10)
        )
        assertTrue(
            fourKeyMetricsResponse.leadTimeForChange.details.containsAll(
                listOf(
                    Metric(1.0, 1, 5),
                    Metric(3.0, 6, 10)
                )
            )
        )

        assertEquals(
            fourKeyMetricsResponse.changeFailureRate.summary,
            Metric(0.5, LEVEL.LOW, 1, 10)
        )

        assertTrue(
            fourKeyMetricsResponse.changeFailureRate.details.containsAll(
                listOf(
                    Metric(0.4, 1, 5),
                    Metric(0.6, 6, 10)
                )
            )
        )

    }

}