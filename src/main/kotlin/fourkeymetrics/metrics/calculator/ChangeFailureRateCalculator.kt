package fourkeymetrics.metrics.calculator

import fourkeymetrics.common.model.Build
import fourkeymetrics.common.model.StageStatus
import fourkeymetrics.metrics.model.LEVEL
import fourkeymetrics.metrics.model.MetricsUnit
import org.springframework.stereotype.Component

@Component
class ChangeFailureRateCalculator : MetricsCalculator {

    companion object {
        private val TARGET_STAGE_STATUS_LIST = listOf(StageStatus.FAILED, StageStatus.SUCCESS)
        private val INVALID_VALUE = Double.NaN
        private const val LEVEL_ELITE_UPPER_LIMIT = 0.15
        private const val LEVEL_HIGH_UPPER_LIMIT = 0.3
        private const val LEVEL_MEDIUM_UPPER_LIMIT = 0.45
    }

    override fun calculateValue(
        allBuilds: List<Build>,
        startTimestamp: Long,
        endTimestamp: Long,
        targetStage: String
    ): Number {
        val statusCountMap = allBuilds.asSequence()
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

        val totalCount = statusCountMap.values.sum()
        return if (totalCount > 0) {
            statusCountMap.getOrDefault(StageStatus.FAILED, 0).toDouble() / totalCount
        } else {
            INVALID_VALUE
        }
    }

    override fun calculateLevel(value: Number, unit: MetricsUnit?): LEVEL {
        val changeFailureRate = value.toDouble()
        return when {
            changeFailureRate < LEVEL_ELITE_UPPER_LIMIT -> LEVEL.ELITE
            LEVEL_ELITE_UPPER_LIMIT <= changeFailureRate && changeFailureRate < LEVEL_HIGH_UPPER_LIMIT -> LEVEL.HIGH
            LEVEL_HIGH_UPPER_LIMIT <= changeFailureRate && changeFailureRate < LEVEL_MEDIUM_UPPER_LIMIT -> LEVEL.MEDIUM
            LEVEL_MEDIUM_UPPER_LIMIT <= changeFailureRate -> LEVEL.LOW
            else -> LEVEL.INVALID
        }
    }
}