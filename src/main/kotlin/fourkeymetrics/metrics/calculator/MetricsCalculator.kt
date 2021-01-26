package fourkeymetrics.metrics.calculator

import fourkeymetrics.common.model.Build
import fourkeymetrics.metrics.model.LEVEL
import fourkeymetrics.metrics.model.MetricsUnit


interface MetricsCalculator {
    fun calculateValue(allBuilds: List<Build>, startTimestamp: Long, endTimestamp: Long, targetStage: String): Double

    fun calculateLevel(value: Double, unit: MetricsUnit? = null): LEVEL

}