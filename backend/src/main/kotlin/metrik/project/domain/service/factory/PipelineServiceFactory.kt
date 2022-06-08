package metrik.project.domain.service.factory

import metrik.project.domain.model.PipelineType
import metrik.project.domain.service.PipelineService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class PipelineServiceFactory(
    private val jenkinsPipelineService: PipelineService,
    private val bambooPipelineService: PipelineService,
    private val githubActionsPipelineService: PipelineService,
    private val bambooDeploymentPipelineService: PipelineService,
    private val noopPipelineService: PipelineService
) {
    fun getService(pipelineType: PipelineType): PipelineService {
        return when (pipelineType) {
            PipelineType.JENKINS -> this.jenkinsPipelineService
            PipelineType.BAMBOO -> this.bambooPipelineService
            PipelineType.GITHUB_ACTIONS -> this.githubActionsPipelineService
            PipelineType.BAMBOO_DEPLOYMENT -> this.bambooDeploymentPipelineService
            else -> this.noopPipelineService
        }
    }
}
