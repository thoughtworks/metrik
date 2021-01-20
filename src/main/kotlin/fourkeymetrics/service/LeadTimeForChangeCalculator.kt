package fourkeymetrics.service

import fourkeymetrics.model.Build

class LeadTimeForChangeCalculator {
    fun calculate(allBuilds: List<Build>, startTimestamp: Long, endTimestamp: Long, targetStage: String): Double {
        val targetBuilds: List<Build> = allBuilds.filter { it.timestamp in startTimestamp..endTimestamp }

        if (targetBuilds.isEmpty()) {
            return 0.0
        }

        for (build in targetBuilds) {
            val targetedStage = build.stages.find { it.name == targetStage }

            if (targetedStage != null) {
                val deploymentTimeStamp = targetedStage.startTimeMillis

                var commitTime: Long = 0
                for (commit in build.changeSets) {
                    commitTime += deploymentTimeStamp - commit.timestamp
                }
                val leadTimeForChange = commitTime.toDouble() / build.changeSets.size

                return leadTimeForChange
            }
        }

        return 0.0
    }
}