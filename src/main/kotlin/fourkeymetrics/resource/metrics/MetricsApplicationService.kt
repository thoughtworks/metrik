package fourkeymetrics.resource.metrics

//import fourkeymetrics.model.Metric
import fourkeymetrics.model.MetricUnit
import fourkeymetrics.resource.metrics.representation.FourKeyMetricsResponse
//import fourkeymetrics.utils.splitTimeRangeFortnightly
//import fourkeymetrics.utils.splitTimeRangeMonthly
import org.springframework.stereotype.Service

@Service
class MetricsApplicationService {

    fun retrieve4KeyMetrics(
        dashboardId: String,
        pipelineId: String,
        targetStage: String,
        startTimeMillis: Long,
        endTimeMillis: Long,
        unit: MetricUnit
    ): FourKeyMetricsResponse {
        TODO()
//        val timeRangeList = when (unit) {
//            MetricUnit.Fortnightly -> splitTimeRangeFortnightly(startTimeMillis, endTimeMillis)
//            MetricUnit.Monthly -> splitTimeRangeMonthly(startTimeMillis, endTimeMillis)
//        }
//        timeRangeList
//            .asSequence()
//            .map { Metric() }
    }
}