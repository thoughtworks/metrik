package fourkeymetrics.metrics.controller

import fourkeymetrics.dashboard.repository.BuildRepository
import fourkeymetrics.exception.BadRequestException
import fourkeymetrics.metrics.calculator.ChangeFailureRateCalculator
import fourkeymetrics.metrics.calculator.LeadTimeForChangeCalculator
import fourkeymetrics.metrics.calculator.MetricsCalculator
import fourkeymetrics.metrics.controller.vo.FourKeyMetricsResponse
import fourkeymetrics.metrics.controller.vo.MetricsInfo
import fourkeymetrics.common.model.Build
import fourkeymetrics.metrics.calculator.DeploymentFrequencyCalculator
import fourkeymetrics.metrics.calculator.MeanTimeToRestoreCalculator
import fourkeymetrics.metrics.model.Metrics
import fourkeymetrics.metrics.model.MetricsUnit
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.util.*

@Service
class MetricsApplicationService {


    companion object {
        private const val FORTNIGHT_DURATION: Int = 14
        private const val MONTH_DURATION: Int = 30
    }


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

    @Autowired
    private lateinit var timeRangeSplitter: TimeRangeSplitter

    fun retrieve4KeyMetrics(
        pipelineId: String,
        targetStage: String,
        startTimestamp: Long,
        endTimestamp: Long,
        unit: MetricsUnit
    ): FourKeyMetricsResponse {
        validateTime(startTimestamp, endTimestamp)
        val allBuilds = buildRepository.getAllBuilds(pipelineId)
        val timeRangeByUnit: List<Pair<Long, Long>> = timeRangeSplitter.split(startTimestamp, endTimestamp, unit)

        return FourKeyMetricsResponse(
            generateDeployFrequencyMetrics(
                allBuilds,
                startTimestamp,
                endTimestamp,
                targetStage,
                timeRangeByUnit,
                unit,
                deploymentFrequencyCalculator,
            ),
            generateMetrics(
                allBuilds,
                startTimestamp,
                endTimestamp,
                targetStage,
                timeRangeByUnit,
                unit,
                leadTimeForChangeCalculator,
            ),
            generateMetrics(
                allBuilds,
                startTimestamp,
                endTimestamp,
                targetStage,
                timeRangeByUnit,
                unit,
                meanTimeToRestoreCalculator,
            ),
            generateMetrics(
                allBuilds,
                startTimestamp,
                endTimestamp,
                targetStage,
                timeRangeByUnit,
                unit,
                changeFailureRateCalculator,
            )
        )
    }

    private fun validateTime(startTimestamp: Long, endTimestamp: Long) {
        if (startTimestamp >= endTimestamp) {
            throw BadRequestException("start time should be earlier than end time")
        }
    }

    private fun generateMetrics(
        allBuilds: List<Build>,
        startTimeMillis: Long,
        endTimeMillis: Long,
        targetStage: String,
        timeRangeByUnit: List<Pair<Long, Long>>,
        unit: MetricsUnit,
        calculator: MetricsCalculator
    ): MetricsInfo {
        val valueForWholeRange = calculator.calculateValue(allBuilds, startTimeMillis, endTimeMillis, targetStage)
        val summary = Metrics(
            valueForWholeRange,
            calculator.calculateLevel(valueForWholeRange),
            startTimeMillis,
            endTimeMillis
        )
        val details = timeRangeByUnit
            .map {
                val valueForUnitRange =
                    calculator.calculateValue(allBuilds, it.first, it.second, targetStage)
                Metrics(valueForUnitRange, it.first, it.second)
            }
        return MetricsInfo(summary, details)
    }

    private fun generateDeployFrequencyMetrics(
        allBuilds: List<Build>,
        startTimeMillis: Long,
        endTimeMillis: Long,
        targetStage: String,
        timeRangeByUnit: List<Pair<Long, Long>>,
        unit: MetricsUnit,
        calculator: DeploymentFrequencyCalculator
    ): MetricsInfo {
        val days = getDuration(startTimeMillis, endTimeMillis)
        val deploymentCount = calculator.calculateValue(allBuilds, startTimeMillis, endTimeMillis,
            targetStage)
        val factor = if (unit == MetricsUnit.Fortnightly) FORTNIGHT_DURATION else MONTH_DURATION

        val deploymentCountPerUnit = deploymentCount.toDouble().div(days).times(factor)

        val summary = Metrics(
            deploymentCountPerUnit,
            calculator.calculateLevel(deploymentCount, days),
            startTimeMillis,
            endTimeMillis
        )
        val details = timeRangeByUnit
            .map {
                val valueForUnitRange =
                    calculator.calculateValue(allBuilds, it.first, it.second, targetStage)
                Metrics(valueForUnitRange, it.first, it.second)
            }
        return MetricsInfo(summary, details)
    }

    private fun getDuration(startTimestamp: Long, endTimestamp: Long): Int {
        val getTimeZone = TimeZone.getDefault().toZoneId()
        val startDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(startTimestamp), getTimeZone)
        val endDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(endTimestamp), getTimeZone)

        return Duration.between(startDateTime, endDateTime).toDays().toInt()+1
    }
}
