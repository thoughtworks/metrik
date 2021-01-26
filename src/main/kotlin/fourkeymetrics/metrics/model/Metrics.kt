package fourkeymetrics.metrics.model

enum class LEVEL {
    ELITE, HIGH, MEDIUM, LOW, INVALID
}

data class Metrics(val value: Double, val level: LEVEL?, val startTimestamp: Long, val endTimestamp: Long) {
    constructor(value: Double, startTimestamp: Long, endTimestamp: Long) : this(
        value,
        null,
        startTimestamp,
        endTimestamp
    )
}
