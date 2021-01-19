package fourkeymetrics.pipeline

import fourkeymetrics.model.Build
import org.springframework.stereotype.Service

@Service
class Jenkins : Pipeline {
    override fun hasPipeline(pipelineID: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun hasStageInLastBuild(pipelineID: String, targetStage: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun fetchAllBuilds(pipelineID: String): List<Build> {
        TODO("Not yet implemented")
    }
}
