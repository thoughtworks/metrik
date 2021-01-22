package fourkeymetrics.service

import fourkeymetrics.model.Build
import fourkeymetrics.model.BuildStatus
import org.springframework.stereotype.Service

@Service
class ChangeFailureRateCalculator : MetricValueCalculator {

    companion object {
        private val TARGET_STAGE_STATUS_LIST = listOf(BuildStatus.FAILED, BuildStatus.SUCCESS)
    }

    override fun calculate(
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