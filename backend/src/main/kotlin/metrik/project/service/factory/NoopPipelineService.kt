package metrik.project.service.factory

import metrik.common.model.Build
import metrik.project.controller.applicationservice.SyncProgress
import metrik.project.model.Pipeline
import metrik.project.service.PipelineService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service("noopPipelineService")
class NoopPipelineService : PipelineService {
    private var logger = LoggerFactory.getLogger(this.javaClass.name)

    override fun syncBuildsProgressively(pipeline: Pipeline, emitCb: (SyncProgress) -> Unit): List<Build> {
        logger.info("Noop implementation")
        return emptyList()
    }

    override fun verifyPipelineConfiguration(pipeline: Pipeline) {
        logger.info("Noop implementation")
    }

    override fun getStagesSortedByName(pipelineId: String): List<String> {
        logger.info("Noop implementation")
        return emptyList()
    }
}
