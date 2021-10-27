package metrik.project.domain.service

import metrik.project.domain.model.Execution
import metrik.project.domain.model.PipelineConfiguration
import metrik.project.rest.vo.response.SyncProgress

interface PipelineService {

    fun syncBuildsProgressively(pipeline: PipelineConfiguration, emitCb: (SyncProgress) -> Unit): List<Execution>

    fun verifyPipelineConfiguration(pipeline: PipelineConfiguration)

    fun getStagesSortedByName(pipelineId: String): List<String>
}
