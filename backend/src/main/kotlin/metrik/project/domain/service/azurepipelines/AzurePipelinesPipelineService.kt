package metrik.project.domain.service.azurepipelines

import metrik.project.domain.model.Execution
import metrik.project.domain.model.PipelineConfiguration
import metrik.project.domain.service.PipelineService
import metrik.project.rest.vo.response.SyncProgress
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service("azurePipelinesPipelineService")
class AzurePipelinesPipelineService : PipelineService {
    private var logger = LoggerFactory.getLogger(javaClass.name)
    override fun syncBuildsProgressively(
        pipeline: PipelineConfiguration,
        emitCb: (SyncProgress) -> Unit
    ): List<Execution> {
        TODO("Not yet implemented")
    }

    override fun verifyPipelineConfiguration(pipeline: PipelineConfiguration) {
        logger.info(
            "Started verification for Azure Pipelines [name: ${pipeline.name}, url: ${pipeline.url}, " +
                    "type: ${pipeline.type}]"
        )

    }

    override fun getStagesSortedByName(pipelineId: String): List<String> {
        TODO("Not yet implemented")
    }
}