package metrik.metrics.domain.calculator

import metrik.metrics.domain.model.LEVEL
import metrik.project.domain.model.Build
import metrik.project.domain.model.Status
import org.springframework.stereotype.Component

@Component
class ChangeFailureRateCalculator : MetricsCalculator {

    companion object {
        private val TARGET_STAGE_STATUS_LIST = listOf(Status.FAILED, Status.SUCCESS)
        private const val INVALID_VALUE = Double.NaN
        private const val PERCENTAGE_FACTOR = 100.0

        private const val LEVEL_ELITE_UPPER_LIMIT = 0.15 * PERCENTAGE_FACTOR
        private const val LEVEL_HIGH_UPPER_LIMIT = 0.3 * PERCENTAGE_FACTOR
        private const val LEVEL_MEDIUM_UPPER_LIMIT = 0.45 * PERCENTAGE_FACTOR
    }

    override fun calculateValue(
        allBuilds: List<Build>,
        startTimestamp: Long,
        endTimestamp: Long,
        pipelineStagesMap: Map<String, String>
    ): Number {
        val statusCountMap = allBuilds.groupBy { it.pipelineId }
            .map { getStatusCountMap(it.value, startTimestamp, endTimestamp, pipelineStagesMap[it.key]) }
            .stream().reduce(mutableMapOf(), this::mergeMap)

        val totalCount = statusCountMap.values.sum()
        return if (totalCount > 0) {
            statusCountMap.getOrDefault(Status.FAILED, 0).toDouble() / totalCount * PERCENTAGE_FACTOR
        } else {
            INVALID_VALUE
        }
    }

    override fun calculateLevel(value: Number, days: Int?): LEVEL {
        val changeFailureRate = value.toDouble()
        return when {
            changeFailureRate < LEVEL_ELITE_UPPER_LIMIT -> LEVEL.ELITE
            LEVEL_ELITE_UPPER_LIMIT <= changeFailureRate && changeFailureRate < LEVEL_HIGH_UPPER_LIMIT -> LEVEL.HIGH
            LEVEL_HIGH_UPPER_LIMIT <= changeFailureRate && changeFailureRate < LEVEL_MEDIUM_UPPER_LIMIT -> LEVEL.MEDIUM
            LEVEL_MEDIUM_UPPER_LIMIT <= changeFailureRate -> LEVEL.LOW
            else -> LEVEL.INVALID
        }
    }

    private fun mergeMap(mapA: Map<Status, Int>, mapB: Map<Status, Int>): MutableMap<Status, Int> {
        val result = mutableMapOf<Status, Int>()
        TARGET_STAGE_STATUS_LIST.forEach { result[it] = mapA.getOrDefault(it, 0) + mapB.getOrDefault(it, 0) }
        return result
    }

    private fun getStatusCountMap(
        allBuilds: List<Build>,
        startTimestamp: Long,
        endTimestamp: Long,
        targetStage: String?
    ): Map<Status, Int> {
        if (targetStage == null) {
            return emptyMap()
        }

        return allBuilds.asSequence()
            .flatMap {
                it.stages.asSequence()
            }
            .filter {
                it.getStageDoneTime() in startTimestamp..endTimestamp
            }
            .filter { it.name == targetStage }
            .filter { it.status in TARGET_STAGE_STATUS_LIST }
            .groupingBy { it.status }
            .eachCount()
    }
}