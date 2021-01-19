package fourkeymetrics.pipeline

import fourkeymetrics.model.Build

interface Pipeline {
    fun hasPipeline(dashboardId: String, pipelineId: String): Boolean

    fun hasStageInLastBuild(pipelineId: String, targetStage: String): Boolean

    fun fetchAllBuilds(pipelineId: String): List<Build>
}
