package fourkeymetrics.project.service

import fourkeymetrics.common.model.Build
import fourkeymetrics.project.controller.applicationservice.SyncProgress
import fourkeymetrics.project.model.Pipeline

interface PipelineService {

    fun syncBuilds(pipeline: Pipeline): List<Build>

    fun syncBuildsProgressively(pipeline: Pipeline, emitCb: (SyncProgress) -> Unit): List<Build>

    fun verifyPipelineConfiguration(pipeline: Pipeline)

    fun getStagesSortedByName(pipelineId: String): List<String>

}
