package fourkeymetrics.pipeline

import fourkeymetrics.model.Build
import org.springframework.stereotype.Service

@Service
class Jenkins : Pipeline {
    override fun hasPipeline(dashboardId: String, pipelineId: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun hasStageInLastBuild(pipelineId: String, targetStage: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun fetchAllBuilds(pipelineId: String): List<Build> {
        TODO("Not yet implemented")
    }
}
