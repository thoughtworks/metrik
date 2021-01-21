package fourkeymetrics.service

import fourkeymetrics.model.BuildStatus
import fourkeymetrics.repository.BuildRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ChangeFailureRateCalculator {
    @Autowired
    private lateinit var buildRepository: BuildRepository

    companion object {
        val TARGET_STAGE_STATUS_LIST = listOf(BuildStatus.FAILED, BuildStatus.SUCCESS)
    }

    fun getChangeFailureRate(
        pipelineId: String,
        targetStage: String,
        startTimestampMillis: Long,
        endTimestampMillis: Long
    ): Float {
        val allBuilds = buildRepository.getAllBuilds(pipelineId)
        val statusCountMap = allBuilds.asSequence()
            .flatMap {
                it.stages.asSequence()
            }
            .filter {
                val stageEndTime = it.startTimeMillis + it.durationMillis + it.pauseDurationMillis
                stageEndTime in startTimestampMillis..endTimestampMillis
            }
            .filter { it.name == targetStage }
            .filter { it.status in TARGET_STAGE_STATUS_LIST }
            .groupingBy { it.status }
            .eachCount()

        val totalCount = statusCountMap.values.sum()
        return if (totalCount > 0) {
            statusCountMap.getOrDefault(BuildStatus.FAILED, 0).toFloat() / totalCount
        } else {
            0F
        }
    }
}