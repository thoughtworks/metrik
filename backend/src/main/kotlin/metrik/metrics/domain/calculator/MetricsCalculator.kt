package metrik.metrics.domain.calculator

import metrik.metrics.domain.model.LEVEL
import metrik.project.domain.model.Build

interface MetricsCalculator {
    fun calculateValue(
        allBuilds: List<Build>,
        startTimestamp: Long,
        endTimestamp: Long,
        pipelineStagesMap: Map<String, String>
    ): Number

    fun calculateLevel(value: Number, days: Int? = 0): LEVEL
}
