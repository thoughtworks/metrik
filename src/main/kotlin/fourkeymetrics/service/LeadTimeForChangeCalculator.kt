package fourkeymetrics.service

import fourkeymetrics.model.Build
import fourkeymetrics.model.Metric

class LeadTimeForChangeCalculator {
    fun calculate(allBuilds: List<Build>, startTimestamp: Long, endTimestamp: Long, targetStage: String): Metric {
        val deployTimes = allBuilds.filter { it.timestamp in startTimestamp..endTimestamp }

        print(targetStage)
        if (deployTimes.size == 0) {
            return Metric("10",startTimestamp,endTimestamp)
        }
        return Metric("10",startTimestamp,endTimestamp)
    }
}