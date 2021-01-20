package fourkeymetrics.resource.metrics.representation

import fourkeymetrics.model.Metric

enum class LEVEL {
    ELITE, HIGH, MEDIUM, LOW
}

data class Metrics(val summary: Metric, val details: List<Metric>)

data class FourKeyMetricsResponse(
    val deploymentFrequency: Metrics,
    val leadTimeForChange: Metrics,
    val timeToRestoreService: Metrics,
    val changeFailureRate: Metrics
)