package fourkeymetrics.metric.controller.vo

import fourkeymetrics.metric.model.Metric

data class Metrics(val summary: Metric, val details: List<Metric>)

data class FourKeyMetricsResponse(
//    val deploymentFrequency: Metrics,
    val leadTimeForChange: Metrics,
//    val timeToRestoreService: Metrics,
    val changeFailureRate: Metrics
)