package fourkeymetrics.metric.calculator

import fourkeymetrics.metric.model.*
import org.springframework.stereotype.Component

@Component
class MeanTimeToRestoreCalculator : MetricCalculator {

    override fun calculateValue(allBuilds: List<Build>, startTimestamp: Long,
                                endTimestamp: Long, targetStage: String): Double {
        val buildOrderByTimestampAscending = allBuilds.sortedBy { it.timestamp }
        TODO("Not yet implemented")
    }

    override fun calculateLevel(value: Double, unit: MetricUnit?): LEVEL {
        TODO("Not yet implemented")
    }

    fun findSelectedStages(allBuilds: List<Build>, startTimestamp: Long, endTimestamp: Long,
                           targetStage: String): List<Stage> {
        val targetStages = allBuilds.flatMap { build -> build.stages }.filter { stage -> stage.name == targetStage }
        val lastSuccessfulDeploymentBuildBeforeGivenTime = allBuilds.findLast {
            it.containsGivenDeploymentBeforeGivenTimestamp(
                targetStage,
                BuildStatus.SUCCESS,
                startTimestamp
            )
        } ?: return targetStages.filter { it.getStageDoneTime() <= endTimestamp }

        val lastSuccessfulDeploymentTimestamp = lastSuccessfulDeploymentBuildBeforeGivenTime
            .stages.find { it.name == targetStage }!!.getStageDoneTime()

        return targetStages.filter {
            it.getStageDoneTime() in
                (lastSuccessfulDeploymentTimestamp.plus(1)).rangeTo(endTimestamp)
        }
    }

    fun generateRestoredBuildPairs(selectedStages: List<Stage>): List<Pair<Stage, Stage>> {
        val restoredStagePairs: MutableList<Pair<Stage, Stage>> = emptyList<Pair<Stage, Stage>>().toMutableList()
        var index = 0
        while (index < selectedStages.size) {
            if (selectedStages[index].status == BuildStatus.FAILED) {
                var successfulStageIndex = index + 1
                while (successfulStageIndex < selectedStages.size) {
                    if (selectedStages[successfulStageIndex].status == BuildStatus.SUCCESS) {
                        restoredStagePairs.add(Pair(selectedStages[index], selectedStages[successfulStageIndex]))
                        index = successfulStageIndex
                        break
                    }
                    successfulStageIndex++
                }
            }
            index++
        }
        return restoredStagePairs
    }
}