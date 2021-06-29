package metrik.metrics.rest

import metrik.project.domain.model.Build
import metrik.project.domain.repository.BuildRepository
import metrik.metrics.exception.BadRequestException
import metrik.metrics.domain.calculator.ChangeFailureRateCalculator
import metrik.metrics.domain.calculator.DeploymentFrequencyCalculator
import metrik.metrics.domain.calculator.LeadTimeForChangeCalculator
import metrik.metrics.domain.calculator.MeanTimeToRestoreCalculator
import metrik.metrics.rest.vo.PipelineStageRequest
import metrik.metrics.domain.model.LEVEL
import metrik.metrics.domain.model.Metrics
import metrik.metrics.domain.model.CalculationPeriod
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
    private lateinit var deploymentFrequencyCalculator: DeploymentFrequencyCalculator

    @Mock
    private lateinit var buildRepository: BuildRepository

    @InjectMocks
    private lateinit var metricsApplicationService: MetricsApplicationService

    @Test
    fun `should return correct metric response`() {
        val pipelineId = "pipelineId"
        val stage = "stage"
        val pipelineStages = listOf(PipelineStageRequest(pipelineId, stage))
        val targetStage = mapOf(Pair(pipelineId, stage))
        val startOfFirstPeriod = 1622649600000L
        val endOfFirstPeriod = 1623859199999L
        val startOfSecondPeriod = 1623859200000L
        val endOfSecondPeriod = 1624987108485L
        val periodLengthInDays = 28
        val expectedBuilds = emptyList<Build>()
        val unit = CalculationPeriod.Fortnightly
        val unit2 = CalculationPeriod.Monthly

        `when`(buildRepository.getAllBuilds(targetStage.keys)).thenReturn(expectedBuilds)

        `when`(deploymentFrequencyCalculator.calculateValue(expectedBuilds, startOfFirstPeriod, endOfSecondPeriod, targetStage))
            .thenReturn(7)
        `when`(deploymentFrequencyCalculator.calculateLevel(7, periodLengthInDays)).thenReturn(LEVEL.ELITE)
        `when`(deploymentFrequencyCalculator.calculateValue(expectedBuilds, startOfFirstPeriod, endOfFirstPeriod, targetStage))
            .thenReturn(3)
        `when`(deploymentFrequencyCalculator.calculateValue(expectedBuilds, startOfSecondPeriod, endOfSecondPeriod, targetStage))
            .thenReturn(4)

        `when`(leadTimeForChangeCalculator.calculateValue(expectedBuilds, startOfFirstPeriod, endOfSecondPeriod, targetStage))
            .thenReturn(2.0)
        `when`(leadTimeForChangeCalculator.calculateLevel(2.0)).thenReturn(LEVEL.LOW)
        `when`(leadTimeForChangeCalculator.calculateValue(expectedBuilds, startOfFirstPeriod, endOfFirstPeriod, targetStage))
            .thenReturn(1.0)
        `when`(leadTimeForChangeCalculator.calculateValue(expectedBuilds, startOfSecondPeriod, endOfSecondPeriod, targetStage))
            .thenReturn(3.0)
        `when`(leadTimeForChangeCalculator.calculateLevel(2.0)).thenReturn(LEVEL.HIGH)

        `when`(meanTimeToRestoreCalculator.calculateValue(expectedBuilds, startOfFirstPeriod, endOfSecondPeriod, targetStage))
            .thenReturn(2.25)
        `when`(meanTimeToRestoreCalculator.calculateLevel(2.25)).thenReturn(LEVEL.HIGH)
        `when`(meanTimeToRestoreCalculator.calculateValue(expectedBuilds, startOfFirstPeriod, endOfFirstPeriod, targetStage))
            .thenReturn(1.8)
        `when`(meanTimeToRestoreCalculator.calculateValue(expectedBuilds, startOfSecondPeriod, endOfSecondPeriod, targetStage))
            .thenReturn(5.7)

        `when`(changeFailureRateCalculator.calculateValue(expectedBuilds, startOfFirstPeriod, endOfSecondPeriod, targetStage))
            .thenReturn(0.5)
        `when`(changeFailureRateCalculator.calculateLevel(0.5)).thenReturn(LEVEL.LOW)
        `when`(changeFailureRateCalculator.calculateValue(expectedBuilds, startOfFirstPeriod, endOfFirstPeriod, targetStage))
            .thenReturn(0.4)
        `when`(changeFailureRateCalculator.calculateValue(expectedBuilds, startOfSecondPeriod, endOfSecondPeriod, targetStage))
            .thenReturn(0.6)
        `when`(changeFailureRateCalculator.calculateLevel(0.5)).thenReturn(LEVEL.HIGH)

        val fourKeyMetricsResponse = metricsApplicationService.calculateFourKeyMetrics(
            pipelineStages, startOfFirstPeriod, endOfSecondPeriod, unit
        )

        val fourKeyMetricsResponse2 = metricsApplicationService.calculateFourKeyMetrics(
            pipelineStages, startOfFirstPeriod, endOfSecondPeriod, unit2
        )

        assertEquals(
            Metrics(3.5, LEVEL.ELITE, startOfFirstPeriod, endOfSecondPeriod),
            fourKeyMetricsResponse.deploymentFrequency.summary
        )

        assertEquals(
            Metrics(7.5, LEVEL.ELITE, startOfFirstPeriod, endOfSecondPeriod),
            fourKeyMetricsResponse2.deploymentFrequency.summary
        )

        assertTrue(
            fourKeyMetricsResponse.deploymentFrequency.details.containsAll(
                listOf(
                    Metrics(3, startOfFirstPeriod, endOfFirstPeriod),
                    Metrics(4, startOfSecondPeriod, endOfSecondPeriod)
                )
            )
        )

        assertEquals(
            Metrics(2.0, LEVEL.HIGH, startOfFirstPeriod, endOfSecondPeriod),
            fourKeyMetricsResponse.leadTimeForChange.summary
        )
        assertTrue(
            fourKeyMetricsResponse.leadTimeForChange.details.containsAll(
                listOf(
                    Metrics(1.0, startOfFirstPeriod, endOfFirstPeriod),
                    Metrics(3.0, startOfSecondPeriod, endOfSecondPeriod)
                )
            )
        )

        assertEquals(
            Metrics(2.25, LEVEL.HIGH, startOfFirstPeriod, endOfSecondPeriod),
            fourKeyMetricsResponse.meanTimeToRestore.summary
        )
        assertTrue(
            fourKeyMetricsResponse.meanTimeToRestore.details.containsAll(
                listOf(
                    Metrics(1.8, startOfFirstPeriod, endOfFirstPeriod),
                    Metrics(5.7, startOfSecondPeriod, endOfSecondPeriod)
                )
            )
        )

        assertEquals(
            Metrics(0.5, LEVEL.HIGH, startOfFirstPeriod, endOfSecondPeriod),
            fourKeyMetricsResponse.changeFailureRate.summary
        )

        assertTrue(
            fourKeyMetricsResponse.changeFailureRate.details.containsAll(
                listOf(
                    Metrics(0.4, startOfFirstPeriod, endOfFirstPeriod),
                    Metrics(0.6, startOfSecondPeriod, endOfSecondPeriod)
                )
            )
        )
    }

    @Test
    fun `should throw bad request given start time later than end time`() {
        assertThrows(
            BadRequestException::class.java
        ) {
            metricsApplicationService.calculateFourKeyMetrics(
                listOf(PipelineStageRequest("pipelineId", "deploy to prod")),
                1,
                1,
                CalculationPeriod.Fortnightly
            )
        }
    }
}