package metrik.metrics.rest.vo

import metrik.metrics.domain.model.Metrics

data class MetricsInfo(val summary: Metrics, val details: List<Metrics>)

data class FourKeyMetricsResponse(
    val deploymentFrequency: MetricsInfo,
    val leadTimeForChange: MetricsInfo,
    val meanTimeToRestore: MetricsInfo,
    val changeFailureRate: MetricsInfo
)
