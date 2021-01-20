package fourkeymetrics.resource.metrics.representation

enum class LEVEL {
    ELITE, HIGH, MEDIUM, LOW
}

data class Metric(val level: LEVEL, val value: String, val from: Long, val to: Long)

data class Metrics(val summary: Metric, val details: List<Metric>)

data class FourKeyMetricsResponse(
    val deploymentFrequency: Metrics,
    val leadTimeForChange: Metrics,
    val timeToRestoreService: Metrics,
    val changeFailureRate: Metrics
)