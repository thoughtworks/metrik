package fourkeymetrics.metrics.controller

import fourkeymetrics.datasource.dashboard.repository.BuildRepository
import fourkeymetrics.exception.BadRequestException
import fourkeymetrics.metrics.calculator.ChangeFailureRateCalculator
import fourkeymetrics.metrics.calculator.LeadTimeForChangeCalculator
import fourkeymetrics.metrics.calculator.MetricsCalculator
import fourkeymetrics.metrics.controller.vo.FourKeyMetricsResponse
import fourkeymetrics.metrics.controller.vo.MetricsInfo
import fourkeymetrics.metrics.model.Build
import fourkeymetrics.metrics.model.Metrics
import fourkeymetrics.metrics.model.MetricsUnit
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class MetricsApplicationService {

    @Autowired
    private lateinit var changeFailureRateCalculator: ChangeFailureRateCalculator

    @Autowired
    private lateinit var leadTimeForChangeCalculator: LeadTimeForChangeCalculator

    @Autowired
    private lateinit var buildRepository: BuildRepository

    @Autowired
    private lateinit var dateTimeUtils: DateTimeUtils

    fun retrieve4KeyMetrics(
        pipelineId: String,
        targetStage: String,
        startTimestamp: Long,
        endTimestamp: Long,
        unit: MetricsUnit
    ): FourKeyMetricsResponse {
        validateTime(startTimestamp, endTimestamp)
        val allBuilds = buildRepository.getAllBuilds(pipelineId)
        val timeRangeByUnit: List<Pair<Long, Long>> = dateTimeUtils.splitTimeRange(startTimestamp, endTimestamp, unit)

        return FourKeyMetricsResponse(
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
            calculator.calculateLevel(valueForWholeRange, unit),
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
}