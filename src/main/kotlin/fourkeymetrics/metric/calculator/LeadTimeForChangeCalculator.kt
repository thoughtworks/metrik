package fourkeymetrics.metric.calculator

import fourkeymetrics.metric.model.Build
import fourkeymetrics.metric.model.BuildStatus
import fourkeymetrics.metric.model.LEVEL
import fourkeymetrics.metric.model.MetricUnit
import org.springframework.stereotype.Component

@Component
class LeadTimeForChangeCalculator : MetricCalculator {

    companion object {
        private const val NO_VALUE: Double = Double.NaN
        private const val EARLIEST_TIMESTAMP: Long = 0
        private const val MILLI_SECONDS_FOR_ONE_DAY = 24 * 60 * 60 * 1000
    }

    override fun calculateValue(
        allBuilds: List<Build>, startTimestamp: Long, endTimestamp: Long, targetStage: String
    ): Double {
        val buildOrderByTimestampAscending = allBuilds.sortedBy { it.timestamp }
        val lastSuccessfulDeploymentBuild = buildOrderByTimestampAscending.findLast {
            it.containsGivenDeploymentInGivenTimeRange(
                targetStage,
                BuildStatus.SUCCESS, startTimestamp,
                endTimestamp
            )
        } ?: return NO_VALUE
        val firstSuccessfulDeploymentBuild = buildOrderByTimestampAscending.findLast {
            it.containsGivenDeploymentBeforeGivenTimestamp(
                targetStage,
                BuildStatus.SUCCESS, startTimestamp
            )
        }
        val buildsInScope = filterBuildsBetweenGivenRange(
            lastSuccessfulDeploymentBuild,
            firstSuccessfulDeploymentBuild,
            buildOrderByTimestampAscending
        )

        return calculateMLT(buildsInScope, targetStage)
    }

    override fun calculateLevel(value: Double, unit: MetricUnit?): LEVEL {
        val days: Double = value / MILLI_SECONDS_FOR_ONE_DAY

        return when {
            days >= 30 -> {
                LEVEL.LOW
            }
            days >= 7 -> {
                LEVEL.MEDIUM
            }
            days >= 1 -> {
                LEVEL.HIGH
            }
            days >= 0 -> {
                LEVEL.ELITE
            }
            else -> {
                LEVEL.INVALID
            }
        }
    }

    private fun calculateMLT(
        buildsInScope: List<Build>,
        targetStage: String
    ): Double {
        val buildsGroupedByEachDeployment: MutableMap<DeploymentTimestamp, List<Build>> = mutableMapOf()

        var buildIndex = 0
        var fromIndex = 0

        while (buildIndex in buildsInScope.indices) {
            val deployStage = buildsInScope[buildIndex].findGivenStage(targetStage, BuildStatus.SUCCESS)
            if (deployStage != null) {
                val buildsInOneDeployment: List<Build> = buildsInScope.subList(fromIndex, buildIndex + 1)
                buildsGroupedByEachDeployment[DeploymentTimestamp(deployStage.getStageDoneTime())] =
                    buildsInOneDeployment
                fromIndex = buildIndex + 1
            }
            buildIndex++
        }

        val commitLeadTimeList = buildsGroupedByEachDeployment.flatMap { element ->
            element.value.flatMap { build -> build.changeSets }.map { commit -> element.key.value - commit.timestamp }
        }

        return commitLeadTimeList.average()
    }

    private fun filterBuildsBetweenGivenRange(
        lastSuccessfulDeploymentBuild: Build,
        firstSuccessfulDeploymentBuild: Build?,
        buildOrderByTimestampAscending: List<Build>
    ): List<Build> {
        val buildRangeEndTimestamp = lastSuccessfulDeploymentBuild.timestamp
        val buildRangeStartTimestamp = firstSuccessfulDeploymentBuild?.timestamp ?: EARLIEST_TIMESTAMP

        return buildOrderByTimestampAscending.filter {
            it.timestamp in (buildRangeStartTimestamp + 1)..buildRangeEndTimestamp
        }
    }
}

data class DeploymentTimestamp(val value: Long)