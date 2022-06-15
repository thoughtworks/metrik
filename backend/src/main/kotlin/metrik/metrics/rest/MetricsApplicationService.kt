package metrik.metrics.rest

import metrik.metrics.domain.calculator.ChangeFailureRateCalculator
import metrik.metrics.domain.calculator.DeploymentFrequencyCalculator
import metrik.metrics.domain.calculator.LeadTimeForChangeCalculator
import metrik.metrics.domain.calculator.MeanTimeToRestoreCalculator
import metrik.metrics.domain.calculator.MetricsCalculator
import metrik.metrics.domain.model.CalculationPeriod
import metrik.metrics.domain.model.Metrics
import metrik.metrics.exception.BadRequestException
import metrik.metrics.rest.vo.FourKeyMetricsResponse
import metrik.metrics.rest.vo.MetricsInfo
import metrik.metrics.rest.vo.PipelineStageRequest
import metrik.project.domain.model.Execution
import metrik.project.domain.repository.BuildRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.util.TimeZone

@Service
class MetricsApplicationService {
    @Autowired
    private lateinit var deploymentFrequencyCalculator: DeploymentFrequencyCalculator

    @Autowired
    private lateinit var changeFailureRateCalculator: ChangeFailureRateCalculator

    @Autowired
    private lateinit var leadTimeForChangeCalculator: LeadTimeForChangeCalculator

    @Autowired
    private lateinit var meanTimeToRestoreCalculator: MeanTimeToRestoreCalculator

    @Autowired
    private lateinit var buildRepository: BuildRepository

    @Cacheable("4km_cache")
    fun calculateFourKeyMetrics(
        pipelineWithStages: List<PipelineStageRequest>,
        startTimestamp: Long,
        endTimestamp: Long,
        period: CalculationPeriod
    ): FourKeyMetricsResponse {
        validateTime(startTimestamp, endTimestamp)
        val pipelineStageMap = pipelineWithStages.map { Pair(it.pipelineId, it.stage) }.toMap()
        val allBuilds = buildRepository.getAllBuilds(pipelineStageMap.keys)
        val timeRangeByUnit: List<Pair<Long, Long>> = TimeRangeSplitter.split(startTimestamp, endTimestamp, period)

        return FourKeyMetricsResponse(
            generateDeployFrequencyMetrics(
                allBuilds,
                startTimestamp,
                endTimestamp,
                pipelineStageMap,
                timeRangeByUnit,
                period,
                deploymentFrequencyCalculator,
            ),
            generateMetrics(
                allBuilds,
                startTimestamp,
                endTimestamp,
                pipelineStageMap,
                timeRangeByUnit,
                leadTimeForChangeCalculator,
            ),
            generateMetrics(
                allBuilds,
                startTimestamp,
                endTimestamp,
                pipelineStageMap,
                timeRangeByUnit,
                meanTimeToRestoreCalculator,
            ),
            generateMetrics(
                allBuilds,
                startTimestamp,
                endTimestamp,
                pipelineStageMap,
                timeRangeByUnit,
                changeFailureRateCalculator,
            )
        )
    }

    private fun validateTime(startTimestamp: Long, endTimestamp: Long) {
        if (startTimestamp >= endTimestamp) {
            throw BadRequestException("start time should be earlier than end time")
        }
    }
    @Suppress("LongParameterList")
    private fun generateMetrics(
        allExecutions: List<Execution>,
        startTimeMillis: Long,
        endTimeMillis: Long,
        pipelineStagesMap: Map<String, String>,
        timeRangeByUnit: List<Pair<Long, Long>>,
        calculator: MetricsCalculator
    ): MetricsInfo {
        val valueForWholeRange = calculator.calculateValue(
            allExecutions, startTimeMillis, endTimeMillis, pipelineStagesMap
        )
        val summary = Metrics(
            valueForWholeRange,
            calculator.calculateLevel(valueForWholeRange),
            startTimeMillis,
            endTimeMillis
        )
        val details = timeRangeByUnit
            .map {
                val valueForUnitRange =
                    calculator.calculateValue(allExecutions, it.first, it.second, pipelineStagesMap)
                Metrics(valueForUnitRange, it.first, it.second)
            }
        return MetricsInfo(summary, details)
    }
    @Suppress("LongParameterList")
    private fun generateDeployFrequencyMetrics(
        allExecutions: List<Execution>,
        startTimeMillis: Long,
        endTimeMillis: Long,
        pipelineStagesMap: Map<String, String>,
        timeRangeByUnit: List<Pair<Long, Long>>,
        period: CalculationPeriod,
        calculator: DeploymentFrequencyCalculator
    ): MetricsInfo {
        val days = getDuration(startTimeMillis, endTimeMillis)
        val deploymentCount = calculator.calculateValue(
            allExecutions, startTimeMillis, endTimeMillis,
            pipelineStagesMap
        )

        val deploymentCountPerUnit = deploymentCount.toDouble().div(days).times(period.timeInDays)

        val summary = Metrics(
            deploymentCountPerUnit,
            calculator.calculateLevel(deploymentCount, days),
            startTimeMillis,
            endTimeMillis
        )
        val details = timeRangeByUnit
            .map {
                val valueForUnitRange =
                    calculator.calculateValue(allExecutions, it.first, it.second, pipelineStagesMap)
                Metrics(valueForUnitRange, it.first, it.second)
            }
        return MetricsInfo(summary, details)
    }

    private fun getDuration(startTimestamp: Long, endTimestamp: Long): Int {
        val getTimeZone = TimeZone.getDefault().toZoneId()
        val startDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(startTimestamp), getTimeZone)
        val endDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(endTimestamp), getTimeZone)

        return Duration.between(startDateTime, endDateTime).toDays().toInt() + 1
    }
}
