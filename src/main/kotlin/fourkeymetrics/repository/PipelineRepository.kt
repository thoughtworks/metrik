package fourkeymetrics.repository

import fourkeymetrics.model.PipelineConfiguration
import org.springframework.stereotype.Component

@Component
class PipelineRepository {
    fun getPipeline(dashboardId: String, pipelineId: String): PipelineConfiguration? {
        TODO()
    }
}
