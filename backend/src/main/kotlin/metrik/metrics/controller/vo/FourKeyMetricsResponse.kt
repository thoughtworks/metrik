package metrik.metrics.controller.vo

import metrik.metrics.model.Metrics

data class MetricsInfo(val summary: Metrics, val details: List<Metrics>)

data class FourKeyMetricsResponse(
    val deploymentFrequency: MetricsInfo,
    val leadTimeForChange: MetricsInfo,
    val meanTimeToRestore: MetricsInfo,
    val changeFailureRate: MetricsInfo
)