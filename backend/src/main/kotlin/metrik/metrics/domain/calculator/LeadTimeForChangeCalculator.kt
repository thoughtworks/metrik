package metrik.metrics.domain.calculator

import metrik.metrics.domain.model.LEVEL
import metrik.project.domain.model.Execution
import metrik.project.domain.model.Status
import org.springframework.stereotype.Component

private const val LOW = 30
private const val MEDIUM = 7
private const val HIGH = 1
private const val ELITE = 0

@Component
class LeadTimeForChangeCalculator : MetricsCalculator {

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
            leadTime >= LOW -> {
                LEVEL.LOW
            }
            leadTime >= MEDIUM -> {
                LEVEL.MEDIUM
            }
            leadTime >= HIGH -> {
                LEVEL.HIGH
            }
            leadTime >= ELITE -> {
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

        var buildIndex = ELITE
        var fromIndex = ELITE

        while (buildIndex in buildsInScope.indices) {
            val deployStage = buildsInScope[buildIndex].findGivenStage(targetStage, Status.SUCCESS)
            if (deployStage != null) {
                val buildsInOneDeployment: List<Execution> = buildsInScope.subList(fromIndex, buildIndex + HIGH)
                buildsGroupedByEachDeployment[DeploymentTimestamp(deployStage.getStageDoneTime())] =
                    buildsInOneDeployment
                fromIndex = buildIndex + HIGH
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
            it.timestamp in (buildRangeStartTimestamp + HIGH)..buildRangeEndTimestamp
        }
    }

    companion object {
        private const val EARLIEST_TIMESTAMP: Long = 0
        private const val MILLI_SECONDS_FOR_ONE_DAY = 24 * 60 * 60 * 1000
    }
}

data class DeploymentTimestamp(val value: Long)
