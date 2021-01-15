package fourkeymetrics.service

import fourkeymetrics.model.Build

interface PipelineService {
    fun hasPipeline(pipelineID: String): Boolean

    fun hasStage(pipelineID: String, targetStage: String): Boolean

    fun getAllBuilds(pipelineID: String): List<Build>
}
