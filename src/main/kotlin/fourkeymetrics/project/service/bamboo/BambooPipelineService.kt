package fourkeymetrics.project.service.bamboo

import fourkeymetrics.common.model.Build
import fourkeymetrics.common.model.Status
import fourkeymetrics.project.model.Pipeline
import fourkeymetrics.project.service.PipelineService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class BambooPipelineService() : PipelineService() {
    override fun syncBuilds(pipelineId: String): List<Build> {
        TODO("Not yet implemented")
    }

    override fun verifyPipelineConfiguration(pipeline: Pipeline) {
        TODO("Not yet implemented")
    }

    override fun mapStageStatus(statusInPipeline: String?): Status {
        TODO("Not yet implemented")
    }

    override fun mapBuildStatus(statusInPipeline: String?): Status {
        TODO("Not yet implemented")
    }

}
