package metrik.project.service.factory

import metrik.project.model.PipelineType
import metrik.project.service.PipelineService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class PipelineServiceFactory(
    @Autowired private val jenkinsPipelineService: PipelineService,
    @Autowired private val bambooPipelineService: PipelineService,
    @Autowired private val noopPipelineService: PipelineService
) {
    fun getService(pipelineType: PipelineType): PipelineService {
        return when (pipelineType) {
            PipelineType.JENKINS -> this.jenkinsPipelineService
            PipelineType.BAMBOO -> this.bambooPipelineService
            else -> this.noopPipelineService
        }
    }
}
