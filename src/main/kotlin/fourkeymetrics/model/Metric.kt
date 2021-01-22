package fourkeymetrics.model

enum class LEVEL {
    ELITE, HIGH, MEDIUM, LOW
}

data class Metric(val value: Double, val level: LEVEL?, val startTimestamp: Long, val endTimestamp: Long) {
    constructor(value: Double, startTimestamp: Long, endTimestamp: Long) : this(
        value,
        null,
        startTimestamp,
        endTimestamp
    )
}
