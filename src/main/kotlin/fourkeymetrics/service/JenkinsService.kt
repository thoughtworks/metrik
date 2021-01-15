package fourkeymetrics.service

import fourkeymetrics.model.Build
import org.springframework.stereotype.Service

@Service
class JenkinsService : PipelineService {
    override fun hasPipeline(pipelineID: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun hasStage(pipelineID: String, targetStage: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun getAllBuilds(pipelineID: String): List<Build> {
        TODO("Not yet implemented")
    }
}
