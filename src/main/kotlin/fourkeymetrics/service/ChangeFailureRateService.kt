package fourkeymetrics.service

import fourkeymetrics.model.BuildStatus
import fourkeymetrics.model.Stage
import fourkeymetrics.repository.BuildRepository
import fourkeymetrics.utils.getLocalDateTimeFromTimestampMillis
import fourkeymetrics.utils.getLocalDateTimeFromTimestampSec
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ChangeFailureRateService {
    @Autowired
    private lateinit var buildRepository: BuildRepository

    companion object {
        val TARGET_STAGE_STATUS_LIST = listOf(BuildStatus.FAILED, BuildStatus.SUCCESS)
    }

    fun getChangeFailureRate(
        pipelineId: String,
        targetStage: String,
        startTimestamp: Long,
        endTimestamp: Long
    ): Float {
        val allBuilds = buildRepository.getAllBuilds(pipelineId)
        val startTime = getLocalDateTimeFromTimestampSec(startTimestamp)
        val endTime = getLocalDateTimeFromTimestampSec(endTimestamp)
        val statusCountMap = allBuilds.asSequence()
            .flatMap {
                it.stages.asSequence() }
            .filter {
                val stageStartTime = getLocalDateTimeFromTimestampMillis(it.startTimeMillis)
                !startTime.isAfter(stageStartTime) && !endTime.isBefore(stageStartTime)
            }
            .filter {
                this.filterStagesByName(it, targetStage)
            }
            .groupingBy { it.status }
            .eachCount()

        val totalCount = statusCountMap.values.sum()
        return if (totalCount > 0) {
            statusCountMap.getOrDefault(BuildStatus.FAILED, 0).toFloat() / totalCount
        } else {
            0F
        }
    }

    internal fun filterStagesByName(stage: Stage, targetStage: String): Boolean {
        return stage.name == targetStage && stage.status in TARGET_STAGE_STATUS_LIST
    }
}