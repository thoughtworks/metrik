package fourkeymetrics.service

import fourkeymetrics.model.Build
import fourkeymetrics.model.BuildStatus
import fourkeymetrics.repository.BuildRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class DeploymentFrequencyService {
    @Autowired
    private lateinit var buildRepository: BuildRepository

    fun getDeploymentCount(pipelineId: String, targetStage: String, startTime: Long, endTime: Long): Int {
        val allBuilds = buildRepository.getAllBuilds(pipelineId).sortedBy { it.timestamp }

        var validDeploymentCount = 0
        for (index in allBuilds.indices) {
            val currentBuild = allBuilds[index]
            val prevBuild = if (index == 0) {
                null
            } else {
                allBuilds[index - 1]
            }

            if (isInvalidBuild(currentBuild, startTime, endTime, targetStage, prevBuild)) {
                continue
            }

            validDeploymentCount++
        }

        return validDeploymentCount
    }

    private fun isInvalidBuild(currentBuild: Build, startTime: Long, endTime: Long,
                               targetStage: String, prevBuild: Build?): Boolean {
        return !isWithinTimeRange(currentBuild, startTime, endTime) ||
                !isTargetStageSuccess(currentBuild, targetStage) ||
                !isEffectiveDeployment(prevBuild, currentBuild)
    }

    private fun isTargetStageSuccess(build: Build, targetStage: String) =
            build.stages.find { stage -> stage.name == targetStage }?.status == BuildStatus.SUCCESS

    private fun isWithinTimeRange(build: Build, startTime: Long, endTime: Long) =
            build.timestamp in startTime..endTime

    private fun isEffectiveDeployment(previousBuild: Build?, currentBuild: Build) =
            if (previousBuild == null) {
                true
            } else {
                previousBuild.changeSets != currentBuild.changeSets
            }
}
