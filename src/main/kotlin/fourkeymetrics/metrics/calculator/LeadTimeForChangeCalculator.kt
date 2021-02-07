package fourkeymetrics.metrics.calculator

import fourkeymetrics.common.model.Build
import fourkeymetrics.common.model.StageStatus
import fourkeymetrics.metrics.model.LEVEL
import org.springframework.stereotype.Component

@Component
class LeadTimeForChangeCalculator : MetricsCalculator {

    companion object {
        private const val EARLIEST_TIMESTAMP: Long = 0
        private const val MILLI_SECONDS_FOR_ONE_DAY = 24 * 60 * 60 * 1000
    }


    override fun calculateValue(
        allBuilds: List<Build>,
        startTimestamp: Long,
        endTimestamp: Long,
        pipelineStagesMap: Map<String, String>
    ): Number {
        return allBuilds.groupBy { it.pipelineId }.flatMap {
            getLeadTimeForChangeList(it.value, startTimestamp, endTimestamp, pipelineStagesMap[it.key])
        }.average() / MILLI_SECONDS_FOR_ONE_DAY
    }

    private fun getLeadTimeForChangeList(
        allBuilds: List<Build>, startTimestamp: Long, endTimestamp: Long, targetStage: String?
    ): List<Long> {
        if (targetStage == null) return emptyList()
        val buildOrderByTimestampAscending = allBuilds.sortedBy { it.timestamp }
        val lastSuccessfulDeploymentBuild = buildOrderByTimestampAscending.findLast {
            it.containsGivenDeploymentInGivenTimeRange(
                targetStage,
                StageStatus.SUCCESS, startTimestamp,
                endTimestamp
            )
        } ?: return emptyList()
        val firstSuccessfulDeploymentBuild = buildOrderByTimestampAscending.findLast {
            it.containsGivenDeploymentBeforeGivenTimestamp(
                targetStage,
                StageStatus.SUCCESS, startTimestamp
            )
        }
        val buildsInScope = filterBuildsBetweenGivenRange(
            lastSuccessfulDeploymentBuild,
            firstSuccessfulDeploymentBuild,
            buildOrderByTimestampAscending
        )

        return calculateCommitLeadTimeList(buildsInScope, targetStage)
    }

    override fun calculateLevel(value: Number, days: Int?): LEVEL {
        val leadTime: Double = value.toDouble()

        return when {
            leadTime >= 30 -> {
                LEVEL.LOW
            }
            leadTime >= 7 -> {
                LEVEL.MEDIUM
            }
            leadTime >= 1 -> {
                LEVEL.HIGH
            }
            leadTime >= 0 -> {
                LEVEL.ELITE
            }
            else -> {
                LEVEL.INVALID
            }
        }
    }

    private fun calculateCommitLeadTimeList(
        buildsInScope: List<Build>,
        targetStage: String
    ): List<Long> {
        val buildsGroupedByEachDeployment: MutableMap<DeploymentTimestamp, List<Build>> = mutableMapOf()

        var buildIndex = 0
        var fromIndex = 0

        while (buildIndex in buildsInScope.indices) {
            val deployStage = buildsInScope[buildIndex].findGivenStage(targetStage, StageStatus.SUCCESS)
            if (deployStage != null) {
                val buildsInOneDeployment: List<Build> = buildsInScope.subList(fromIndex, buildIndex + 1)
                buildsGroupedByEachDeployment[DeploymentTimestamp(deployStage.getStageDoneTime())] =
                    buildsInOneDeployment
                fromIndex = buildIndex + 1
            }
            buildIndex++
        }

        return buildsGroupedByEachDeployment.flatMap { element ->
            element.value.flatMap { build -> build.changeSets }.map { commit -> element.key.value - commit.timestamp }
        }
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