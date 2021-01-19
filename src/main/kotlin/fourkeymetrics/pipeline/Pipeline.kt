package fourkeymetrics.pipeline

import fourkeymetrics.model.Build

interface Pipeline {
    fun hasPipeline(dashboardId: String, pipelineID: String): Boolean

    fun hasStageInLastBuild(pipelineID: String, targetStage: String): Boolean

    fun fetchAllBuilds(pipelineID: String): List<Build>
}
