package fourkeymetrics.metrics.calculator

import fourkeymetrics.common.model.Build
import fourkeymetrics.common.model.StageStatus
import fourkeymetrics.metrics.model.LEVEL
import org.springframework.stereotype.Component

@Component
class ChangeFailureRateCalculator : MetricsCalculator {

    companion object {
        private val TARGET_STAGE_STATUS_LIST = listOf(StageStatus.FAILED, StageStatus.SUCCESS)
        private const val INVALID_VALUE = Double.NaN
        private const val LEVEL_ELITE_UPPER_LIMIT = 0.15
        private const val LEVEL_HIGH_UPPER_LIMIT = 0.3
        private const val LEVEL_MEDIUM_UPPER_LIMIT = 0.45
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
            statusCountMap.getOrDefault(StageStatus.FAILED, 0).toDouble() / totalCount
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

    private fun mergeMap(mapA: Map<StageStatus, Int>, mapB: Map<StageStatus, Int>): MutableMap<StageStatus, Int> {
        val result = mutableMapOf<StageStatus, Int>()
        TARGET_STAGE_STATUS_LIST.forEach { result[it] = mapA.getOrDefault(it, 0) + mapB.getOrDefault(it, 0) }
        return result
    }

    private fun getStatusCountMap(
        allBuilds: List<Build>,
        startTimestamp: Long,
        endTimestamp: Long,
        targetStage: String?
    ): Map<StageStatus, Int> {
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