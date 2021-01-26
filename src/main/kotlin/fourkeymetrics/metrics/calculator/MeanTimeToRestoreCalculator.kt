package fourkeymetrics.metrics.calculator

import fourkeymetrics.metrics.model.Build
import fourkeymetrics.metrics.model.BuildStatus
import fourkeymetrics.metrics.model.LEVEL
import fourkeymetrics.metrics.model.MetricsUnit
import org.springframework.stereotype.Component

@Component
class MeanTimeToRestoreCalculator : MetricsCalculator {

    override fun calculateValue(allBuilds: List<Build>, startTimestamp: Long,
                                endTimestamp: Long, targetStage: String): Double {
        val buildOrderByTimestampAscending = allBuilds.sortedBy { it.timestamp }
        TODO("Not yet implemented")
    }

    override fun calculateLevel(value: Double, unit: MetricsUnit?): LEVEL {
        TODO("Not yet implemented")
    }

    fun findSelectedBuilds(allBuilds: List<Build>, startTimestamp: Long, endTimestamp: Long,
                           targetStage: String): List<Build> {
        val firstSuccessfulDeploymentBuild = allBuilds.findLast {
            it.containsGivenDeploymentBeforeGivenTimestamp(targetStage, BuildStatus.SUCCESS,
                startTimestamp
            )
        }

        if (firstSuccessfulDeploymentBuild == null) {
            return allBuilds.filter {
                it.stages.find { stage -> stage.name == targetStage }?.getStageDoneTime()!! <= endTimestamp
            }
        }

        val timestamp = firstSuccessfulDeploymentBuild.stages.find {
            stage -> stage.name == targetStage }?.getStageDoneTime()
        return allBuilds.filter {
            it.stages.find { stage -> stage.name == targetStage }!!.getStageDoneTime() in
                (timestamp?.plus(1))?.rangeTo(endTimestamp) ?: emptyList<Build>()
        }
    }
}