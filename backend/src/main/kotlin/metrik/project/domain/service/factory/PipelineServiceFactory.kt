package metrik.project.domain.service.factory

import metrik.project.domain.model.PipelineType
import metrik.project.domain.service.PipelineService
import metrik.project.domain.service.buddy.BuddyPipelineService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class PipelineServiceFactory(
    @Autowired private val jenkinsPipelineService: PipelineService,
    @Autowired private val bambooPipelineService: PipelineService,
    @Autowired private val githubActionsPipelineService: PipelineService,
    @Autowired private val bambooDeploymentPipelineService: PipelineService,
    @Autowired private val buddyPipelineService: BuddyPipelineService,
    @Autowired private val noopPipelineService: PipelineService
) {
    fun getService(pipelineType: PipelineType): PipelineService {
        return when (pipelineType) {
            PipelineType.JENKINS -> this.jenkinsPipelineService
            PipelineType.BAMBOO -> this.bambooPipelineService
            PipelineType.GITHUB_ACTIONS -> this.githubActionsPipelineService
            PipelineType.BAMBOO_DEPLOYMENT -> this.bambooDeploymentPipelineService
            PipelineType.BUDDY -> this.buddyPipelineService
            else -> this.noopPipelineService
        }
    }
}
