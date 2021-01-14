package fourkeymetrics.service

import fourkeymetrics.model.Build
import fourkeymetrics.model.BuildStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class DeploymentFrequencyService {
    @Autowired
    private lateinit var pipelineService: PipelineService

    //    TODO: add effective calculation
    fun getDeploymentCount(pipelineID: String, targetStage: String, startTime: Long, endTime: Long): Int {
        val builds = pipelineService.getBuildsByTimeRange(pipelineID, startTime, endTime)

        return builds.filter {
            isWithinTimeRange(it, startTime, endTime)
                    && isBuildSuccess(it)
                    && isTargetStageSuccess(it, targetStage)
        }.count()
    }

    private fun isTargetStageSuccess(build: Build, targetStage: String) =
            build.stages.find { stage -> stage.name == targetStage }?.status == BuildStatus.SUCCESS

    private fun isBuildSuccess(build: Build) = build.status == BuildStatus.SUCCESS

    private fun isWithinTimeRange(build: Build, startTime: Long, endTime: Long) =
            build.timestamp in startTime..endTime
}