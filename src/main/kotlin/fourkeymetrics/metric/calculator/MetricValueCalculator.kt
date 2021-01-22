package fourkeymetrics.metric.calculator

import fourkeymetrics.metric.model.Build


interface MetricValueCalculator {
    fun calculate(allBuilds: List<Build>, startTimestamp: Long, endTimestamp: Long, targetStage: String): Double

}