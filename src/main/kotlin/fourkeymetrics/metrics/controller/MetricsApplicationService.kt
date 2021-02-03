package fourkeymetrics.metrics.controller

import fourkeymetrics.common.model.Build
import fourkeymetrics.dashboard.repository.BuildRepository
import fourkeymetrics.exception.BadRequestException
import fourkeymetrics.metrics.calculator.*
import fourkeymetrics.metrics.controller.vo.FourKeyMetricsResponse
import fourkeymetrics.metrics.controller.vo.MetricsInfo
import fourkeymetrics.metrics.model.Metrics
import fourkeymetrics.metrics.model.MetricsUnit
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.util.*
import java.util.stream.Collectors

@Service
class MetricsApplicationService {


    companion object {
        private const val FORTNIGHT_DURATION: Int = 14
        private const val MONTH_DURATION: Int = 30
        private const val DELIMITER = "::"
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
        pipelineWithStages: List<String>,
        startTimestamp: Long,
        endTimestamp: Long,
        unit: MetricsUnit
    ): FourKeyMetricsResponse {
        validateTime(startTimestamp, endTimestamp)
        val pipelineStagesMap = parsePipelineStagesMap(pipelineWithStages)
        val allBuilds = buildRepository.getAllBuilds(pipelineStagesMap.keys)
        val timeRangeByUnit: List<Pair<Long, Long>> = timeRangeSplitter.split(startTimestamp, endTimestamp, unit)

        return FourKeyMetricsResponse(
            generateDeployFrequencyMetrics(
                allBuilds,
                startTimestamp,
                endTimestamp,
                pipelineStagesMap,
                timeRangeByUnit,
                unit,
                deploymentFrequencyCalculator,
            ),
            generateMetrics(
                allBuilds,
                startTimestamp,
                endTimestamp,
                pipelineStagesMap,
                timeRangeByUnit,
                leadTimeForChangeCalculator,
            ),
            generateMetrics(
                allBuilds,
                startTimestamp,
                endTimestamp,
                pipelineStagesMap,
                timeRangeByUnit,
                meanTimeToRestoreCalculator,
            ),
            generateMetrics(
                allBuilds,
                startTimestamp,
                endTimestamp,
                pipelineStagesMap,
                timeRangeByUnit,
                changeFailureRateCalculator,
            )
        )
    }

    private fun parsePipelineStagesMap(pipelineWithStages: List<String>) =
        pipelineWithStages.stream().map { it.split(DELIMITER, limit = 2) }.peek {
            if (it.size < 2) {
                throw BadRequestException("Pipeline and stages are not matching")
            }
        }.collect(Collectors.toMap({ it[0] }, { it[1] }))

    private fun validateTime(startTimestamp: Long, endTimestamp: Long) {
        if (startTimestamp >= endTimestamp) {
            throw BadRequestException("start time should be earlier than end time")
        }
    }

    private fun generateMetrics(
        allBuilds: List<Build>,
        startTimeMillis: Long,
        endTimeMillis: Long,
        pipelineStagesMap: Map<String, String>,
        timeRangeByUnit: List<Pair<Long, Long>>,
        calculator: MetricsCalculator
    ): MetricsInfo {
        val valueForWholeRange = calculator.calculateValue(allBuilds, startTimeMillis, endTimeMillis, pipelineStagesMap)
        val summary = Metrics(
            valueForWholeRange,
            calculator.calculateLevel(valueForWholeRange),
            startTimeMillis,
            endTimeMillis
        )
        val details = timeRangeByUnit
            .map {
                val valueForUnitRange =
                    calculator.calculateValue(allBuilds, it.first, it.second, pipelineStagesMap)
                Metrics(valueForUnitRange, it.first, it.second)
            }
        return MetricsInfo(summary, details)
    }

    private fun generateDeployFrequencyMetrics(
        allBuilds: List<Build>,
        startTimeMillis: Long,
        endTimeMillis: Long,
        pipelineStagesMap: Map<String, String>,
        timeRangeByUnit: List<Pair<Long, Long>>,
        unit: MetricsUnit,
        calculator: DeploymentFrequencyCalculator
    ): MetricsInfo {
        val days = getDuration(startTimeMillis, endTimeMillis)
        val deploymentCount = calculator.calculateValue(
            allBuilds, startTimeMillis, endTimeMillis,
            pipelineStagesMap
        )
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
                    calculator.calculateValue(allBuilds, it.first, it.second, pipelineStagesMap)
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
