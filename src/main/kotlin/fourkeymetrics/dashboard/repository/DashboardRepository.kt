package fourkeymetrics.dashboard.repository

import fourkeymetrics.dashboard.model.Dashboard
import fourkeymetrics.dashboard.model.Pipeline
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.find
import org.springframework.data.mongodb.core.findOne
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Component

@Component
class DashboardRepository {
    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    private val collectionName = "dashboard"

    fun getPipelineConfiguration(dashboardId: String, pipelineId: String): Pipeline? {
        val query = Query.query(Criteria.where("id").isEqualTo(dashboardId))
        val dashboard: Dashboard? = mongoTemplate.findOne(query, collectionName)
        return dashboard?.pipelines?.find { it.id == pipelineId }
    }

    fun getDashBoardDetailByName(name: String): List<Dashboard> {
        val query = Query()
        query.addCriteria(Criteria.where("name").`is`(name))
        return mongoTemplate.find<Dashboard>(query)

    }

    fun save(dashboard: Dashboard): Dashboard {
        return mongoTemplate.save(dashboard)
    }

    fun updatePipeline(
        dashboardId: String,
        pipelineId: String,
        pipeline: Pipeline
    ): Pipeline {
        val query = Query()
        query.addCriteria(
            Criteria.where("_id").`is`(dashboardId)
        ).addCriteria(
            Criteria.where("pipelineConfigurations").elemMatch(Criteria.where("_id").`is`(pipelineId))
        )
        val setPipeline = Update().set("pipelineConfigurations.$", pipeline)
        mongoTemplate.updateFirst(query, setPipeline, "dashboard")
        return pipeline
    }
}
