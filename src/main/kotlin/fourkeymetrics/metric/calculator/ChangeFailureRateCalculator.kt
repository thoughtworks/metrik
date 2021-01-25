package fourkeymetrics.metric.calculator

import fourkeymetrics.metric.model.Build
import fourkeymetrics.metric.model.BuildStatus
import org.springframework.stereotype.Component

@Component
class ChangeFailureRateCalculator : MetricCalculator {

    companion object {
        private val TARGET_STAGE_STATUS_LIST = listOf(BuildStatus.FAILED, BuildStatus.SUCCESS)
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
}