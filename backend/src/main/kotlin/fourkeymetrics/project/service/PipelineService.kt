package fourkeymetrics.project.service

import fourkeymetrics.common.model.Build
import fourkeymetrics.common.model.Status
import fourkeymetrics.project.controller.applicationservice.SyncProgress
import fourkeymetrics.project.model.Pipeline
import fourkeymetrics.project.repository.BuildRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
abstract class PipelineService {
    @Autowired
    private lateinit var buildRepository: BuildRepository

    abstract fun syncBuilds(pipelineId: String): List<Build>

    abstract fun syncBuildsProgressively(pipelineId: String, emitCb: (SyncProgress) -> Unit): List<Build>

    abstract fun verifyPipelineConfiguration(pipeline: Pipeline)

    protected abstract fun mapStageStatus(statusInPipeline: String?): Status

    protected abstract fun mapBuildStatus(statusInPipeline: String?): Status

    fun getStagesSortedByName(pipelineId: String): List<String> {
        return buildRepository.getAllBuilds(pipelineId)
            .flatMap { it.stages }
            .map { it.name }
            .distinct()
            .sortedBy { it.toUpperCase() }
            .toList()
    }
}
