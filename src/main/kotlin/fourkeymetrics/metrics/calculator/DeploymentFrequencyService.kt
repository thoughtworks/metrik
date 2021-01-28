package fourkeymetrics.metrics.calculator

import fourkeymetrics.common.model.Build
import fourkeymetrics.common.model.StageStatus
import fourkeymetrics.dashboard.repository.BuildRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class DeploymentFrequencyService {
    @Autowired
    private lateinit var buildRepository: BuildRepository

    fun getDeploymentCount(pipelineId: String, targetStage: String, startTimestamp: Long, endTimestamp: Long): Int {
        val allBuilds = buildRepository.getAllBuilds(pipelineId).sortedBy { it.timestamp }

        var validDeploymentCount = 0
        for (index in allBuilds.indices) {
            val currentBuild = allBuilds[index]

            if (isInvalidBuild(currentBuild, startTimestamp, endTimestamp, targetStage)) {
                continue
            }

            validDeploymentCount++
        }

        return validDeploymentCount
    }

    private fun isInvalidBuild(currentBuild: Build, startTimestamp: Long,
                               endTimestamp: Long, targetStage: String): Boolean {
        return !isTargetStageWithinTimeRange(currentBuild, startTimestamp, endTimestamp, targetStage) ||
                !isTargetStageSuccess(currentBuild, targetStage)
    }

    private fun isTargetStageSuccess(build: Build, targetStage: String) =
            build.stages.find { stage -> stage.name == targetStage }?.status == StageStatus.SUCCESS

    private fun isTargetStageWithinTimeRange(build: Build, startTimestamp: Long,
                                             endTimestamp: Long, targetStage: String): Boolean {
        val stage = build.stages.find { it.name == targetStage }
        val deploymentFinishTimestamp = stage?.startTimeMillis?.plus(stage.durationMillis)
                ?.plus(stage.pauseDurationMillis)

        return deploymentFinishTimestamp in startTimestamp..endTimestamp
    }
}
