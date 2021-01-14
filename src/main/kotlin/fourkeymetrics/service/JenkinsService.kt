package fourkeymetrics.service

import org.springframework.stereotype.Service

@Service
class JenkinsService {
    fun hasPipeline(pipelineName: String): Boolean {
        TODO("Not yet implemented")
    }

    fun hasStage(pipelineName: String, targetStage: String): Boolean {
        TODO("Not yet implemented")
    }

    fun hasPipeline(pipelineName: String, branch: String): Boolean {
        TODO("Not yet implemented")
    }
}
