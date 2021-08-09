package metrik.project.domain.service.githubactions

import metrik.project.domain.model.Build
import metrik.project.domain.model.Pipeline
import metrik.project.domain.repository.BuildRepository
import metrik.project.domain.service.PipelineService
import metrik.project.rest.vo.response.SyncProgress
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service("githubActionsPipelineService")
class GithubActionsPipelineService(
    @Autowired
    private var restTemplate: RestTemplate,
    @Autowired
    private var buildRepository: BuildRepository
) : PipelineService{
    override fun syncBuildsProgressively(pipeline: Pipeline, emitCb: (SyncProgress) -> Unit): List<Build> {
        TODO("Not yet implemented")
    }

    override fun verifyPipelineConfiguration(pipeline: Pipeline) {
        TODO("Not yet implemented")
    }

    override fun getStagesSortedByName(pipelineId: String): List<String> {
        TODO("Not yet implemented")
    }


}