package metrik.metrics.domain.calculator

import metrik.metrics.domain.model.LEVEL
import metrik.project.domain.model.Build
import metrik.project.domain.model.Status
import org.springframework.stereotype.Component

@Component
class DeploymentFrequencyCalculator : MetricsCalculator {

    companion object {
        private const val ONE_WEEK: Int = 7
        private const val ONE_MONTH: Int = 30
    }

    override fun calculateValue(
        allBuilds: List<Build>,
        startTimestamp: Long,
        endTimestamp: Long,
        pipelineStagesMap: Map<String, String>
    ): Number {
        return pipelineStagesMap.map { entry ->
            allBuilds.filter {
                it.pipelineId == entry.key && !isInvalidBuild(
                    it,
                    startTimestamp,
                    endTimestamp,
                    entry.value
                )
            }.size
        }.sum()
    }

    override fun calculateLevel(value: Number, days: Int?): LEVEL {
        val deploymentCount = value.toDouble()

        val deploymentFrequency = deploymentCount / days!!

        return when {
            deploymentFrequency <= 1.0 / ONE_MONTH -> LEVEL.LOW
            deploymentFrequency <= 1.0 / ONE_WEEK -> LEVEL.MEDIUM
            deploymentFrequency <= 1.0 -> LEVEL.HIGH
            else -> LEVEL.ELITE
        }
    }

    private fun isInvalidBuild(
        currentBuild: Build,
        startTimestamp: Long,
        endTimestamp: Long,
        targetStage: String
    ): Boolean {
        return !isTargetStageWithinTimeRange(currentBuild, startTimestamp, endTimestamp, targetStage) ||
            !isTargetStageSuccess(currentBuild, targetStage)
    }

    private fun isTargetStageSuccess(build: Build, targetStage: String) =
        build.stages.find { stage -> stage.name == targetStage }?.status == Status.SUCCESS

    private fun isTargetStageWithinTimeRange(
        build: Build,
        startTimestamp: Long,
        endTimestamp: Long,
        targetStage: String
    ): Boolean {
        val stage = build.stages.find { it.name == targetStage }
        val deploymentFinishTimestamp = stage?.getStageDoneTime()

        return deploymentFinishTimestamp in startTimestamp..endTimestamp
    }
}
