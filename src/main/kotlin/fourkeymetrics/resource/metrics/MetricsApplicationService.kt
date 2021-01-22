package fourkeymetrics.resource.metrics

import fourkeymetrics.model.Build
import fourkeymetrics.model.Metric
import fourkeymetrics.model.MetricUnit
import fourkeymetrics.repository.BuildRepository
import fourkeymetrics.resource.metrics.representation.FourKeyMetricsResponse
import fourkeymetrics.resource.metrics.representation.LEVEL
import fourkeymetrics.resource.metrics.representation.MetricWithLevel
import fourkeymetrics.resource.metrics.representation.Metrics
import fourkeymetrics.service.ChangeFailureRateCalculator
import fourkeymetrics.service.LeadTimeForChangeCalculator
import fourkeymetrics.service.MetricValueCalculator
import fourkeymetrics.utils.DateTimeUtils
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
        calculator: MetricValueCalculator
    ): Metrics {
        val valueForWholeRange = calculator.calculate(allBuilds, startTimeMillis, endTimeMillis, targetStage)
        val summary = MetricWithLevel(
            LEVEL.LOW,
            Metric(valueForWholeRange, startTimeMillis, endTimeMillis)
        )
        val details = timeRangeByUnit
            .map {
                val valueForUnitRange =
                    calculator.calculate(allBuilds, it.first, it.second, targetStage)
                Metric(valueForUnitRange, it.first, it.second)
            }
        return Metrics(summary, details)
    }
}