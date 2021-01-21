package fourkeymetrics.service

import fourkeymetrics.model.Build
import fourkeymetrics.model.BuildStatus

class LeadTimeForChangeCalculator {

    fun calculate(allBuilds: List<Build>, startTimestamp: Long, endTimestamp: Long, targetStage: String): Double {

        val buildOrderByTimestampAscending = allBuilds.sortedBy{ it.timestamp }

        val lastSuccessfulDeploymentBuild = buildOrderByTimestampAscending.findLast {
            it.containsGivenDeploymentInGivenTimeRange(
                targetStage,
                BuildStatus.SUCCESS, startTimestamp,
                endTimestamp
            )
        } ?: return NO_VALUE

        val firstSuccessfulDeploymentBuild = buildOrderByTimestampAscending.find {
            it.containsGivenDeploymentBeforeGivenTimestamp(
                targetStage,
                BuildStatus.SUCCESS, startTimestamp
            )
        }

        val targetBuilds: List<Build> = filterBuildsBetweenGivenRange(
            lastSuccessfulDeploymentBuild,
            firstSuccessfulDeploymentBuild,
            buildOrderByTimestampAscending
        )

        val deploymentStage = lastSuccessfulDeploymentBuild.findGivenStage(targetStage, BuildStatus.SUCCESS)

        val deployDoneTimestamp = deploymentStage!!.getStageDoneTime()

        val average = targetBuilds.flatMap { it.changeSets }.map { deployDoneTimestamp - it.timestamp }.average()

        if (average.isNaN()) {
            return NO_VALUE
        }
        return average
    }

    private fun filterBuildsBetweenGivenRange(
        lastSuccessfulDeploymentBuild: Build,
        firstSuccessfulDeploymentBuild: Build?,
        buildOrderByTimestampAscending: List<Build>
    ): List<Build> {
        val buildRangeEndTimestamp = lastSuccessfulDeploymentBuild.timestamp
        val buildRangeStartTimestamp = firstSuccessfulDeploymentBuild?.timestamp ?: EARLIEST_TIMESTAMP

        val targetBuilds: List<Build> =
            buildOrderByTimestampAscending.filter {
                it.timestamp > buildRangeStartTimestamp && it.timestamp <= buildRangeEndTimestamp
            }
        return targetBuilds
    }

    companion object {
        private const val NO_VALUE: Double = 0.0
        private const val EARLIEST_TIMESTAMP: Long = 0
    }
}