package fourkeymetrics.metrics.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import fourkeymetrics.common.serializer.NumberSerializer


enum class LEVEL {
    ELITE, HIGH, MEDIUM, LOW, INVALID
}

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Metrics(

    @JsonSerialize(using = NumberSerializer::class)
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
