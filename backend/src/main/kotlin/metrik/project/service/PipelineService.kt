package metrik.project.service

import metrik.common.model.Build
import metrik.project.controller.applicationservice.SyncProgress
import metrik.project.model.Pipeline

interface PipelineService {

    fun syncBuildsProgressively(pipeline: Pipeline, emitCb: (SyncProgress) -> Unit): List<Build>

    fun verifyPipelineConfiguration(pipeline: Pipeline)

    fun getStagesSortedByName(pipelineId: String): List<String>

}
