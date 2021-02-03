package fourkeymetrics.metrics.calculator

import fourkeymetrics.common.model.Build
import fourkeymetrics.metrics.model.LEVEL


interface MetricsCalculator {
    fun calculateValue(
        allBuilds: List<Build>,
        startTimestamp: Long,
        endTimestamp: Long,
        pipelineStagesMap: Map<String, String>
    ): Number

    fun calculateLevel(value: Number, days: Int? = 0): LEVEL

}