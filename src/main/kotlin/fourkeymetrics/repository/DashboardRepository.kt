package fourkeymetrics.repository

import fourkeymetrics.model.Dashboard
import fourkeymetrics.model.PipelineConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.findById
import org.springframework.stereotype.Component

@Component
class DashboardRepository {
    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    private val collectionName = "dashboard"

    fun getPipelineConfiguration(dashboardId: String, pipelineId: String): PipelineConfiguration? {
        return mongoTemplate.findById<Dashboard>(dashboardId, collectionName)
            ?.pipelineConfigurations?.find { it.id == pipelineId}
    }
}
