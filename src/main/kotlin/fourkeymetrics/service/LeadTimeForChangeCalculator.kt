package fourkeymetrics.service

import fourkeymetrics.model.Build
import fourkeymetrics.model.BuildStatus

class LeadTimeForChangeCalculator {

    fun calculate(allBuilds: List<Build>, startTimestamp: Long, endTimestamp: Long, targetStage: String): Double {

        val buildOrderByTimestampAscending = allBuilds.sortedWith(compareBy { it.timestamp })

        val lastSuccessfulDeploymentBuild = findLastBuildWithSuccessfulDeployment(
            buildOrderByTimestampAscending,
            startTimestamp,
            endTimestamp,
            targetStage
        ) ?: return NO_VALUE

        val firstSuccessfulDeploymentBuild = findFirstBuildWithSuccessfulDeployment(
            buildOrderByTimestampAscending,
            startTimestamp,
            targetStage
        )

        val targetBuilds: List<Build> = filterBuildsByTimeRange(
            lastSuccessfulDeploymentBuild,
            firstSuccessfulDeploymentBuild,
            buildOrderByTimestampAscending
        )

        val deploymentStage = lastSuccessfulDeploymentBuild.stages.find {
            it.name == targetStage && it.status == BuildStatus.SUCCESS
        }
        val deployTimestamp = deploymentStage!!.startTimeMillis + deploymentStage.durationMillis

        val average = targetBuilds.flatMap { it.changeSets }.map { deployTimestamp - it.timestamp }.average()

        if (average.isNaN()){
            return NO_VALUE
        }
        return average
    }

    private fun filterBuildsByTimeRange(
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

    private fun findLastBuildWithSuccessfulDeployment(
        builds: List<Build>,
        startTimestamp: Long,
        endTimestamp: Long,
        targetStage: String
    ): Build? {
        return builds.findLast {
            val deployStage = it.stages.find {
                it.startTimeMillis + it.durationMillis in startTimestamp..endTimestamp
                        && it.name == targetStage
                        && it.status == BuildStatus.SUCCESS
            }
            deployStage != null
        }
    }

    private fun findFirstBuildWithSuccessfulDeployment(
        builds: List<Build>,
        startTimestamp: Long,
        targetStage: String
    ): Build? {
        return builds.find {
            val deployStage = it.stages.find {
                it.startTimeMillis + it.durationMillis < startTimestamp
                        && it.name == targetStage
                        && it.status == BuildStatus.SUCCESS
            }
            deployStage != null
        }
    }

    companion object {
        private const val NO_VALUE: Double = 0.0
        private const val EARLIEST_TIMESTAMP: Long = 0
    }
}