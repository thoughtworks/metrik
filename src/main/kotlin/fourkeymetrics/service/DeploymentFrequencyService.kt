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

    fun getDeploymentCount(pipelineId: String, targetStage: String, startTimestamp: Long, endTimestamp: Long): Int {
        val allBuilds = buildRepository.getAllBuilds(pipelineId).sortedBy { it.timestamp }

        var validDeploymentCount = 0
        for (index in allBuilds.indices) {
            val currentBuild = allBuilds[index]
            val prevBuild = if (index == 0) {
                null
            } else {
                allBuilds[index - 1]
            }

            if (isInvalidBuild(currentBuild, startTimestamp, endTimestamp, targetStage, prevBuild)) {
                continue
            }

            validDeploymentCount++
        }

        return validDeploymentCount
    }

    private fun isInvalidBuild(currentBuild: Build, startTimestamp: Long, endTimestamp: Long,
                               targetStage: String, prevBuild: Build?): Boolean {
        return !isWithinTimeRange(currentBuild, startTimestamp, endTimestamp) ||
                !isTargetStageSuccess(currentBuild, targetStage) ||
                !isEffectiveDeployment(prevBuild, currentBuild)
    }

    private fun isTargetStageSuccess(build: Build, targetStage: String) =
            build.stages.find { stage -> stage.name == targetStage }?.status == BuildStatus.SUCCESS

    private fun isWithinTimeRange(build: Build, startTimestamp: Long, endTimestamp: Long) =
            build.timestamp in startTimestamp..endTimestamp

    private fun isEffectiveDeployment(previousBuild: Build?, currentBuild: Build) =
            if (previousBuild == null) {
                true
            } else {
                previousBuild.changeSets != currentBuild.changeSets
            }
}
