package metrik.metrics.rest

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import metrik.metrics.domain.calculator.ChangeFailureRateCalculator
import metrik.metrics.domain.calculator.DeploymentFrequencyCalculator
import metrik.metrics.domain.calculator.LeadTimeForChangeCalculator
import metrik.metrics.domain.calculator.MeanTimeToRestoreCalculator
import metrik.metrics.domain.model.CalculationPeriod
import metrik.metrics.domain.model.LEVEL
import metrik.metrics.domain.model.Metrics
import metrik.metrics.exception.BadRequestException
import metrik.metrics.rest.vo.PipelineStageRequest
import metrik.project.domain.model.Execution
import metrik.project.domain.repository.BuildRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class MetricsApplicationServiceTest {
    @MockK
    private lateinit var changeFailureRateCalculator: ChangeFailureRateCalculator
    @MockK
    private lateinit var leadTimeForChangeCalculator: LeadTimeForChangeCalculator
    @MockK
    private lateinit var meanTimeToRestoreCalculator: MeanTimeToRestoreCalculator
    @MockK
    private lateinit var deploymentFrequencyCalculator: DeploymentFrequencyCalculator
    @MockK
    private lateinit var buildRepository: BuildRepository

    @InjectMockKs(overrideValues = true)
    private lateinit var metricsApplicationService: MetricsApplicationService

    private val pipelineId = "pipelineId"
    private val stage = "stage"
    private val pipelineStages = listOf(PipelineStageRequest(pipelineId, stage))
    private val targetStage = mapOf(Pair(pipelineId, stage))
    private val startOfFirstPeriod = 1622649600000L
    private val endOfFirstPeriod = 1623859199999L
    private val startOfSecondPeriod = 1623859200000L
    private val endOfSecondPeriod = 1624987108485L
    private val periodLengthInDays = 28
    private val expectedExecutions = emptyList<Execution>()
    private val unit = CalculationPeriod.Fortnightly
    private val unit2 = CalculationPeriod.Monthly

    @BeforeEach
    fun setUp() {
        every { buildRepository.getAllBuilds(targetStage.keys) } returns expectedExecutions
        every {
            deploymentFrequencyCalculator.calculateValue(
                expectedExecutions,
                startOfFirstPeriod,
                endOfSecondPeriod,
                targetStage
            )
        } returns 7
        every { deploymentFrequencyCalculator.calculateLevel(7, periodLengthInDays) } returns LEVEL.ELITE
        every {
            deploymentFrequencyCalculator.calculateValue(
                expectedExecutions,
                startOfFirstPeriod,
                endOfFirstPeriod,
                targetStage
            )
        } returns 3
        every {
            deploymentFrequencyCalculator.calculateValue(
                expectedExecutions,
                startOfSecondPeriod,
                endOfSecondPeriod,
                targetStage
            )
        } returns 4
        every {
            leadTimeForChangeCalculator.calculateValue(
                expectedExecutions,
                startOfFirstPeriod,
                endOfSecondPeriod,
                targetStage
            )
        } returns 2.0
        every { leadTimeForChangeCalculator.calculateLevel(2.0) } returns LEVEL.LOW
        every {
            leadTimeForChangeCalculator.calculateValue(
                expectedExecutions,
                startOfFirstPeriod,
                endOfFirstPeriod,
                targetStage
            )
        } returns 1.0
        every {
            leadTimeForChangeCalculator.calculateValue(
                expectedExecutions,
                startOfSecondPeriod,
                endOfSecondPeriod,
                targetStage
            )
        } returns 3.0
        every { leadTimeForChangeCalculator.calculateLevel(2.0) } returns LEVEL.HIGH
        every {
            meanTimeToRestoreCalculator.calculateValue(
                expectedExecutions,
                startOfFirstPeriod,
                endOfSecondPeriod,
                targetStage
            )
        } returns 2.25
        every { meanTimeToRestoreCalculator.calculateLevel(2.25) } returns LEVEL.HIGH
        every {
            meanTimeToRestoreCalculator.calculateValue(
                expectedExecutions,
                startOfFirstPeriod,
                endOfFirstPeriod,
                targetStage
            )
        } returns 1.8
        every {
            meanTimeToRestoreCalculator.calculateValue(
                expectedExecutions,
                startOfSecondPeriod,
                endOfSecondPeriod,
                targetStage
            )
        } returns 5.7
        every {
            changeFailureRateCalculator.calculateValue(
                expectedExecutions,
                startOfFirstPeriod,
                endOfSecondPeriod,
                targetStage
            )
        } returns 0.5
        every { changeFailureRateCalculator.calculateLevel(0.5) } returns LEVEL.LOW
        every {
            changeFailureRateCalculator.calculateValue(
                expectedExecutions,
                startOfFirstPeriod,
                endOfFirstPeriod,
                targetStage
            )
        } returns 0.4
        every {
            changeFailureRateCalculator.calculateValue(
                expectedExecutions,
                startOfSecondPeriod,
                endOfSecondPeriod,
                targetStage
            )
        } returns 0.6
        every { changeFailureRateCalculator.calculateLevel(0.5) } returns LEVEL.HIGH
    }

    @Test
    fun `should return correct metric response`() {
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
