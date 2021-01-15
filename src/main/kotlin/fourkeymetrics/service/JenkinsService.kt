package fourkeymetrics.service

import fourkeymetrics.model.Build
import org.springframework.stereotype.Service

@Service
class JenkinsService : PipelineService {
    override fun hasPipeline(pipelineName: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun hasStage(pipelineName: String, targetStage: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun getBuildsByTimeRange(pipelineID: String, startTime: Long, endTime: Long): List<Build> {
        TODO("Not yet implemented")
    }
}
