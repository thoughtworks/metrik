package fourkeymetrics.model

import fourkeymetrics.resource.metrics.representation.LEVEL

data class Metric(val level: LEVEL, val value: String, val startTimestamp: Long, val endTimestamp: Long)