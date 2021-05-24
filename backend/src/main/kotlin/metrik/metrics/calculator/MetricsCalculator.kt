package metrik.metrics.calculator

import metrik.common.model.Build
import metrik.metrics.model.LEVEL


interface MetricsCalculator {
    fun calculateValue(
        allBuilds: List<Build>,
        startTimestamp: Long,
        endTimestamp: Long,
        pipelineStagesMap: Map<String, String>
    ): Number

    fun calculateLevel(value: Number, days: Int? = 0): LEVEL

}