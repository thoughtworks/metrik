package fourkeymetrics.metric.controller

import fourkeymetrics.metric.model.LEVEL
import fourkeymetrics.metric.model.Metric
import fourkeymetrics.metric.model.MetricUnit
import fourkeymetrics.datasource.pipeline.configuration.repository.pipeline.BuildRepository
import fourkeymetrics.metric.controller.vo.FourKeyMetricsResponse
import fourkeymetrics.metric.controller.vo.Metrics
import fourkeymetrics.metric.calculator.ChangeFailureRateCalculator
import fourkeymetrics.metric.calculator.LeadTimeForChangeCalculator
import fourkeymetrics.metric.calculator.MetricCalculator
import fourkeymetrics.metric.model.Build
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
        dashboardId: String,
        pipelineId: String,
        targetStage: String,
        startTimestamp: Long,
        endTimestamp: Long,
        unit: MetricUnit
    ): FourKeyMetricsResponse {
        val allBuilds = buildRepository.getAllBuilds(pipelineId)
        val timeRangeByUnit: List<Pair<Long, Long>> = dateTimeUtils.splitTimeRange(startTimestamp, endTimestamp, unit)

        return FourKeyMetricsResponse(
            generateMetrics(
                allBuilds,
                startTimestamp,
                endTimestamp,
                targetStage,
                timeRangeByUnit,
                leadTimeForChangeCalculator
            ),
            generateMetrics(
                allBuilds,
                startTimestamp,
                endTimestamp,
                targetStage,
                timeRangeByUnit,
                changeFailureRateCalculator
            )
        )
    }

    private fun generateMetrics(
        allBuilds: List<Build>,
        startTimeMillis: Long,
        endTimeMillis: Long,
        targetStage: String,
        timeRangeByUnit: List<Pair<Long, Long>>,
        calculator: MetricCalculator
    ): Metrics {
        val valueForWholeRange = calculator.calculateValue(allBuilds, startTimeMillis, endTimeMillis, targetStage)
        val summary = Metric(valueForWholeRange, LEVEL.LOW, startTimeMillis, endTimeMillis)
        val details = timeRangeByUnit
            .map {
                val valueForUnitRange =
                    calculator.calculateValue(allBuilds, it.first, it.second, targetStage)
                Metric(valueForUnitRange, it.first, it.second)
            }
        return Metrics(summary, details)
    }
}