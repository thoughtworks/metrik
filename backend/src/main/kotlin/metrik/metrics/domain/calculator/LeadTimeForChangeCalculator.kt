package metrik.metrics.domain.calculator

import metrik.metrics.domain.model.LEVEL
import metrik.project.domain.model.Execution
import metrik.project.domain.model.Status
import org.springframework.stereotype.Component

@Component
class LeadTimeForChangeCalculator : MetricsCalculator {

    companion object {
        private const val EARLIEST_TIMESTAMP: Long = 0
        private const val MILLI_SECONDS_FOR_ONE_DAY = 24 * 60 * 60 * 1000
    }

    override fun calculateValue(
        allExecutions: List<Execution>,
        startTimestamp: Long,
        endTimestamp: Long,
        pipelineStagesMap: Map<String, String>
    ): Number {
        return allExecutions.groupBy { it.pipelineId }.flatMap {
            getLeadTimeForChangeList(it.value, startTimestamp, endTimestamp, pipelineStagesMap[it.key])
        }.average() / MILLI_SECONDS_FOR_ONE_DAY
    }

    private fun getLeadTimeForChangeList(
        allExecutions: List<Execution>,
        startTimestamp: Long,
        endTimestamp: Long,
        targetStage: String?
    ): List<Long> {
        if (targetStage == null) return emptyList()
        val buildOrderByTimestampAscending = allExecutions.sortedBy { it.timestamp }
        val lastSuccessfulDeploymentBuild = buildOrderByTimestampAscending.findLast {
            it.containsGivenDeploymentInGivenTimeRange(
                targetStage,
                Status.SUCCESS, startTimestamp,
                endTimestamp
            )
        } ?: return emptyList()
        val firstSuccessfulDeploymentBuild = buildOrderByTimestampAscending.findLast {
            it.containsGivenDeploymentBeforeGivenTimestamp(
                targetStage,
                Status.SUCCESS, startTimestamp
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
        buildsInScope: List<Execution>,
        targetStage: String
    ): List<Long> {
        val buildsGroupedByEachDeployment: MutableMap<DeploymentTimestamp, List<Execution>> = mutableMapOf()

        var buildIndex = 0
        var fromIndex = 0

        while (buildIndex in buildsInScope.indices) {
            val deployStage = buildsInScope[buildIndex].findGivenStage(targetStage, Status.SUCCESS)
            if (deployStage != null) {
                val buildsInOneDeployment: List<Execution> = buildsInScope.subList(fromIndex, buildIndex + 1)
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
        lastSuccessfulDeploymentExecution: Execution,
        firstSuccessfulDeploymentExecution: Execution?,
        executionOrderByTimestampAscending: List<Execution>
    ): List<Execution> {
        val buildRangeEndTimestamp = lastSuccessfulDeploymentExecution.timestamp
        val buildRangeStartTimestamp = firstSuccessfulDeploymentExecution?.timestamp ?: EARLIEST_TIMESTAMP

        return executionOrderByTimestampAscending.filter {
            it.timestamp in (buildRangeStartTimestamp + 1)..buildRangeEndTimestamp
        }
    }
}

data class DeploymentTimestamp(val value: Long)
