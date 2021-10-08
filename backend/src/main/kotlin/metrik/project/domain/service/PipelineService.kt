package metrik.project.domain.service

import metrik.project.domain.model.Execution
import metrik.project.domain.model.Pipeline
import metrik.project.rest.vo.response.SyncProgress

interface PipelineService {

    fun syncBuildsProgressively(pipeline: Pipeline, emitCb: (SyncProgress) -> Unit): List<Execution>

    fun verifyPipelineConfiguration(pipeline: Pipeline)

    fun getStagesSortedByName(pipelineId: String): List<String>
}
