package fourkeymetrics.service

import fourkeymetrics.model.Build

interface MetricValueCalculator {
    fun calculate(allBuilds: List<Build>, startTimestamp: Long, endTimestamp: Long, targetStage: String): Double
}