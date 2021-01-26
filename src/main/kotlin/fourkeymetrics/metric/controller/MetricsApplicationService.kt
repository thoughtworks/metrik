package fourkeymetrics.metric.controller

import fourkeymetrics.datasource.pipeline.builddata.BuildRepository
import fourkeymetrics.exception.BadRequestException
import fourkeymetrics.metric.calculator.ChangeFailureRateCalculator
import fourkeymetrics.metric.calculator.LeadTimeForChangeCalculator
import fourkeymetrics.metric.calculator.MetricCalculator
import fourkeymetrics.metric.controller.vo.FourKeyMetricsResponse
import fourkeymetrics.metric.controller.vo.Metrics
import fourkeymetrics.metric.model.Build
import fourkeymetrics.metric.model.Metric
import fourkeymetrics.metric.model.MetricUnit
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
        unit: MetricUnit
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
        unit: MetricUnit,
        calculator: MetricCalculator
    ): Metrics {
        val valueForWholeRange = calculator.calculateValue(allBuilds, startTimeMillis, endTimeMillis, targetStage)
        val summary = Metric(
            valueForWholeRange,
            calculator.calculateLevel(valueForWholeRange, unit),
            startTimeMillis,
            endTimeMillis
        )
        val details = timeRangeByUnit
            .map {
                val valueForUnitRange =
                    calculator.calculateValue(allBuilds, it.first, it.second, targetStage)
                Metric(valueForUnitRange, it.first, it.second)
            }
        return Metrics(summary, details)
    }
}