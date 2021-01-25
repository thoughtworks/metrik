package fourkeymetrics.metric.calculator

import fourkeymetrics.metric.model.Build


interface MetricCalculator {
    fun calculateValue(allBuilds: List<Build>, startTimestamp: Long, endTimestamp: Long, targetStage: String): Double

}