package fourkeymetrics.pipeline

import fourkeymetrics.model.Build
import org.springframework.stereotype.Service

@Service
class Jenkins : Pipeline() {
    override fun fetchAllBuilds(pipelineId: String): List<Build> {
        TODO("Not yet implemented")
    }
}
