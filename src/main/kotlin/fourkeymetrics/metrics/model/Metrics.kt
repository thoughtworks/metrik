package fourkeymetrics.metrics.model

import com.fasterxml.jackson.annotation.JsonInclude


enum class LEVEL {
    ELITE, HIGH, MEDIUM, LOW, INVALID
}

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Metrics(
    val value: Number,
    val level: LEVEL?,
    val startTimestamp: Long,
    val endTimestamp: Long
) {
    constructor(value: Number, startTimestamp: Long, endTimestamp: Long) : this(
        value,
        null,
        startTimestamp,
        endTimestamp
    )
}
