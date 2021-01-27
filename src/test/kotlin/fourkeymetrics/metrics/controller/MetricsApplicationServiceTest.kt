package fourkeymetrics.metrics.controller

import fourkeymetrics.common.model.Build
import fourkeymetrics.dashboard.repository.BuildRepository
import fourkeymetrics.exception.BadRequestException
import fourkeymetrics.metrics.calculator.ChangeFailureRateCalculator
import fourkeymetrics.metrics.calculator.LeadTimeForChangeCalculator
import fourkeymetrics.metrics.calculator.MeanTimeToRestoreCalculator
import fourkeymetrics.metrics.model.LEVEL
import fourkeymetrics.metrics.model.Metrics
import fourkeymetrics.metrics.model.MetricsUnit
import org.junit.jupiter.api.Assertions.*
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
    private lateinit var meanTimeToRestoreCalculator: MeanTimeToRestoreCalculator

    @Mock
    private lateinit var buildRepository: BuildRepository

    @Mock
    private lateinit var timeRangeSplitter: TimeRangeSplitter

    @InjectMocks
    private lateinit var metricsApplicationService: MetricsApplicationService


    @Test
    fun `should return correct metric response`() {

        val pipelineId = "pipelineId"
        val targetStage = "deploy to prod"
        val startTimestamp: Long = 1
        val endTimestamp: Long = 10
        val expectedBuilds = emptyList<Build>()
        val unit = MetricsUnit.Fortnightly

        `when`(buildRepository.getAllBuilds(pipelineId)).thenReturn(expectedBuilds)
        `when`(timeRangeSplitter.split(startTimestamp, endTimestamp, unit))
            .thenReturn(listOf(Pair(1, 5), Pair(6, 10)))

        `when`(leadTimeForChangeCalculator.calculateValue(expectedBuilds, 1, 10, targetStage))
            .thenReturn(2.0)
        `when`(leadTimeForChangeCalculator.calculateLevel(2.0, unit)).thenReturn(LEVEL.LOW)
        `when`(leadTimeForChangeCalculator.calculateValue(expectedBuilds, 1, 5, targetStage))
            .thenReturn(1.0)
        `when`(leadTimeForChangeCalculator.calculateValue(expectedBuilds, 6, 10, targetStage))
            .thenReturn(3.0)
        `when`(leadTimeForChangeCalculator.calculateLevel(2.0, unit)).thenReturn(LEVEL.HIGH)

        `when`(meanTimeToRestoreCalculator.calculateValue(expectedBuilds, 1, 10, targetStage))
            .thenReturn(2.25)
        `when`(meanTimeToRestoreCalculator.calculateLevel(2.25, unit)).thenReturn(LEVEL.HIGH)
        `when`(meanTimeToRestoreCalculator.calculateValue(expectedBuilds, 1, 5, targetStage))
            .thenReturn(1.8)
        `when`(meanTimeToRestoreCalculator.calculateValue(expectedBuilds, 6, 10, targetStage))
            .thenReturn(5.7)

        `when`(changeFailureRateCalculator.calculateValue(expectedBuilds, 1, 10, targetStage))
            .thenReturn(0.5)
        `when`(changeFailureRateCalculator.calculateLevel(0.5, unit)).thenReturn(LEVEL.LOW)
        `when`(changeFailureRateCalculator.calculateValue(expectedBuilds, 1, 5, targetStage))
            .thenReturn(0.4)
        `when`(changeFailureRateCalculator.calculateValue(expectedBuilds, 6, 10, targetStage))
            .thenReturn(0.6)
        `when`(changeFailureRateCalculator.calculateLevel(0.5, unit)).thenReturn(LEVEL.HIGH)
        val fourKeyMetricsResponse = metricsApplicationService.retrieve4KeyMetrics(
            pipelineId, targetStage, startTimestamp, endTimestamp, unit
        )

        assertEquals(
            fourKeyMetricsResponse.leadTimeForChange.summary,
            Metrics(2.0, LEVEL.HIGH, 1, 10)
        )
        assertTrue(
            fourKeyMetricsResponse.leadTimeForChange.details.containsAll(
                listOf(
                    Metrics(1.0, 1, 5),
                    Metrics(3.0, 6, 10)
                )
            )
        )

        assertEquals(
            fourKeyMetricsResponse.meanTimeToRestore.summary,
            Metrics(2.25, LEVEL.HIGH, 1, 10)
        )
        assertTrue(
            fourKeyMetricsResponse.meanTimeToRestore.details.containsAll(
                listOf(
                    Metrics(1.8, 1, 5),
                    Metrics(5.7, 6, 10)
                )
            )
        )

        assertEquals(
            fourKeyMetricsResponse.changeFailureRate.summary,
            Metrics(0.5, LEVEL.HIGH, 1, 10)
        )

        assertTrue(
            fourKeyMetricsResponse.changeFailureRate.details.containsAll(
                listOf(
                    Metrics(0.4, 1, 5),
                    Metrics(0.6, 6, 10)
                )
            )
        )
    }

    @Test
    fun `should throw bad request given start time later than end time`() {
        assertThrows(BadRequestException::class.java
        ) {
            metricsApplicationService.retrieve4KeyMetrics(
                "pipelineId",
                "deploy to prod",
                1,
                1,
                MetricsUnit.Fortnightly
            )
        }
    }
}