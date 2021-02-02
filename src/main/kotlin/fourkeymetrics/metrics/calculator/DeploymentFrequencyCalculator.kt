package fourkeymetrics.metrics.calculator

import fourkeymetrics.common.model.Build
import fourkeymetrics.common.model.StageStatus
import fourkeymetrics.metrics.model.LEVEL
import org.springframework.stereotype.Component

@Component
class DeploymentFrequencyCalculator : MetricsCalculator {

    companion object {
        private const val ONE_WEEK: Int = 7
        private const val ONE_MONTH: Int = 30
    }

    override fun calculateValue(allBuilds: List<Build>, startTimestamp: Long, endTimestamp: Long,
                                targetStage: String): Number {
        var validDeploymentCount = 0
        for (index in allBuilds.indices) {
            val currentBuild = allBuilds[index]
            if (isInvalidBuild(currentBuild, startTimestamp, endTimestamp, targetStage)) {
                continue
            }
            validDeploymentCount++
        }
        return validDeploymentCount
    }

    override fun calculateLevel(value: Number, days: Int?): LEVEL {
        val deploymentCount = value.toDouble()

        if (days == null || days == 0) {
            return LEVEL.INVALID
        }
        val deploymentFrequency = deploymentCount/days

        return when {
            deploymentFrequency <= 1.0 / ONE_MONTH -> LEVEL.LOW
            deploymentFrequency <= 1.0 / ONE_WEEK -> LEVEL.MEDIUM
            deploymentFrequency <= 1.0 -> LEVEL.HIGH
            else -> LEVEL.ELITE
        }
    }

    private fun isInvalidBuild(currentBuild: Build, startTimestamp: Long,
                               endTimestamp: Long, targetStage: String): Boolean {
        return !isTargetStageWithinTimeRange(currentBuild, startTimestamp, endTimestamp, targetStage) ||
            !isTargetStageSuccess(currentBuild, targetStage)
    }

    private fun isTargetStageSuccess(build: Build, targetStage: String) =
        build.stages.find { stage -> stage.name == targetStage }?.status == StageStatus.SUCCESS

    private fun isTargetStageWithinTimeRange(build: Build, startTimestamp: Long,
                                             endTimestamp: Long, targetStage: String): Boolean {
        val stage = build.stages.find { it.name == targetStage }
        val deploymentFinishTimestamp = stage?.getStageDoneTime()

        return deploymentFinishTimestamp in startTimestamp..endTimestamp
    }


}