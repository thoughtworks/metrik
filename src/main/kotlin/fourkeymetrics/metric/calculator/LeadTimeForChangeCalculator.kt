package fourkeymetrics.metric.calculator

import fourkeymetrics.metric.model.Build
import fourkeymetrics.metric.model.BuildStatus
import org.springframework.stereotype.Component

@Component
class LeadTimeForChangeCalculator : MetricValueCalculator {

    companion object {
        private const val NO_VALUE: Double = 0.0
        private const val EARLIEST_TIMESTAMP: Long = 0
    }

    override fun calculate(
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

    private fun calculateMLT(
        buildsInScope: List<Build>,
        targetStage: String
    ): Double {
        val buildsGroupedByEachDeployment: MutableMap<DeploymentTimestamp, List<Build>> = mutableMapOf()

        var buildIndex = 0
        var fromIndex = 0

        while (buildIndex < buildsInScope.size) {
            val deployStage = buildsInScope.get(buildIndex).findGivenStage(targetStage, BuildStatus.SUCCESS)
            if (deployStage != null) {
                val buildsInOneDeployment = buildsInScope.subList(fromIndex, buildIndex + 1)
                buildsGroupedByEachDeployment[DeploymentTimestamp(deployStage.getStageDoneTime())] =
                    buildsInOneDeployment
                fromIndex = buildIndex + 1
            }
            buildIndex++
        }

        val commitLeadTimeList = buildsGroupedByEachDeployment.flatMap { element ->
            element.value.flatMap { build -> build.changeSets }.map { commit -> element.key.value - commit.timestamp }
        }
        val averageValue = commitLeadTimeList.average()


        if (averageValue.isNaN()) {
            return NO_VALUE
        }

        return averageValue
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
}

data class DeploymentTimestamp(val value: Long)