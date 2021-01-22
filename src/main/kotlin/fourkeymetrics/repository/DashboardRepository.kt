package fourkeymetrics.repository

import fourkeymetrics.model.Dashboard
import fourkeymetrics.model.PipelineConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.findOne
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Component

@Component
class DashboardRepository {
    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    private val collectionName = "dashboard"

    fun getPipelineConfiguration(dashboardId: String, pipelineId: String): PipelineConfiguration? {
        val query = Query.query(Criteria.where("id").isEqualTo(dashboardId))
        val dashboard: Dashboard? = mongoTemplate.findOne(query, collectionName)
        return dashboard?.pipelineConfigurations?.find { it.id == pipelineId }
    }
}
