package fourkeymetrics.service

import fourkeymetrics.model.Build

interface PipelineService {
    fun hasPipeline(pipelineName: String): Boolean

    fun hasStage(pipelineName: String, targetStage: String): Boolean

    fun getBuildsByTimeRange(pipelineID: String, startTime: Long, endTime: Long): List<Build>
}