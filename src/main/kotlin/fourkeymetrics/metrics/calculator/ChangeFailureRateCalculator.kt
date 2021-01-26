package fourkeymetrics.metrics.calculator

import fourkeymetrics.common.model.Build
import fourkeymetrics.common.model.BuildStatus
import fourkeymetrics.metrics.model.LEVEL
import fourkeymetrics.metrics.model.MetricsUnit
import org.springframework.stereotype.Component

@Component
class ChangeFailureRateCalculator : MetricsCalculator {

    companion object {
        private val TARGET_STAGE_STATUS_LIST = listOf(BuildStatus.FAILED, BuildStatus.SUCCESS)
        private const val LEVEL_ELITE_UPPER_LIMIT = 0.15
        private const val LEVEL_HIGH_UPPER_LIMIT = 0.3
        private const val LEVEL_MEDIUM_UPPER_LIMIT = 0.45
    }

    override fun calculateValue(
            allBuilds: List<Build>,
            startTimestamp: Long,
            endTimestamp: Long,
            targetStage: String
    ): Double {
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
            statusCountMap.getOrDefault(BuildStatus.FAILED, 0).toDouble() / totalCount
        } else {
            0.0
        }
    }

    override fun calculateLevel(value: Double, unit: MetricsUnit?): LEVEL {
        return when {
            value < LEVEL_ELITE_UPPER_LIMIT -> LEVEL.ELITE
            LEVEL_ELITE_UPPER_LIMIT <= value && value < LEVEL_HIGH_UPPER_LIMIT -> LEVEL.HIGH
            LEVEL_HIGH_UPPER_LIMIT <= value && value < LEVEL_MEDIUM_UPPER_LIMIT -> LEVEL.MEDIUM
            else -> LEVEL.LOW
        }
    }
}