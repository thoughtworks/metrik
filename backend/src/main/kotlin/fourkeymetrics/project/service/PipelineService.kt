package fourkeymetrics.project.service

import fourkeymetrics.common.model.Build
import fourkeymetrics.common.model.Status
import fourkeymetrics.project.controller.applicationservice.SyncProgress
import fourkeymetrics.project.model.Pipeline

interface PipelineService {

     fun syncBuilds(pipelineId: String): List<Build>

     fun syncBuildsProgressively(pipelineId: String, emitCb: (SyncProgress) -> Unit): List<Build>

     fun verifyPipelineConfiguration(pipeline: Pipeline)

     fun getStagesSortedByName(pipelineId: String): List<String>

    fun mapStageStatus(statusInPipeline: String?): Status

    fun mapBuildStatus(statusInPipeline: String?): Status
}
