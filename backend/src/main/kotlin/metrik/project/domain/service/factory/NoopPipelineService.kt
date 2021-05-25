package metrik.project.domain.service.factory

import metrik.project.domain.model.Build
import metrik.project.rest.vo.response.SyncProgress
import metrik.project.domain.model.Pipeline
import metrik.project.domain.service.PipelineService
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
