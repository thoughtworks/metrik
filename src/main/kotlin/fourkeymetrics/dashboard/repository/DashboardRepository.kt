package fourkeymetrics.dashboard.repository

import fourkeymetrics.dashboard.model.Dashboard
import fourkeymetrics.dashboard.model.Pipeline
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.find
import org.springframework.data.mongodb.core.findById
import org.springframework.data.mongodb.core.findOne
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Component

@Component
class DashboardRepository {
    companion object {
        private const val COLLECTION_NAME = "dashboard"
    }

    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    fun getPipelineConfiguration(dashboardId: String, pipelineId: String): Pipeline? {
        val query = Query.query(Criteria.where("id").isEqualTo(dashboardId))
        val dashboard: Dashboard? = mongoTemplate.findOne(query, COLLECTION_NAME)
        return dashboard?.pipelines?.find { it.id == pipelineId }
    }

    fun deletePipeline(dashboardId: String, pipelineId: String) {
        val queryDashboard = Query().addCriteria(Criteria.where("_id").`is`(dashboardId))
        val queryToBeUpdatedPipelines =
            Update().pull("pipelines", Query().addCriteria(Criteria.where("_id").`is`(pipelineId)))

        mongoTemplate.updateFirst(queryDashboard, queryToBeUpdatedPipelines, COLLECTION_NAME)
    }

    fun getPipelineConfiguration(dashboardId: String): List<Pipeline> {
        val query = Query.query(Criteria.where("id").isEqualTo(dashboardId))
        val dashboard: Dashboard? = mongoTemplate.findOne(query, COLLECTION_NAME)
        return dashboard?.pipelines ?: emptyList()
    }

    fun getDashBoardDetailByName(name: String): List<Dashboard> {
        val query = Query()
        query.addCriteria(Criteria.where("name").`is`(name))
        return mongoTemplate.find<Dashboard>(query)
    }

    fun getDashBoardDetailById(id: String): Dashboard? {
        return mongoTemplate.findById(id, COLLECTION_NAME)
    }

    fun save(dashboard: Dashboard): Dashboard {
        return mongoTemplate.save(dashboard)
    }

    fun updatePipeline(dashboardId: String, pipelineId: String, pipeline: Pipeline): Pipeline {
        val query = Query()
            .addCriteria(Criteria.where("_id").`is`(dashboardId))
            .addCriteria(Criteria.where("pipelines").elemMatch(Criteria.where("_id").`is`(pipelineId)))

        val setPipeline = Update().set("pipelines.$", pipeline)
        mongoTemplate.updateFirst(query, setPipeline, "dashboard")
        return pipeline
    }

    fun getLastSyncRecord(dashboardId: String): Long? {
        val dashboardInDB: Dashboard? = mongoTemplate.findById(dashboardId, COLLECTION_NAME)

        return dashboardInDB?.synchronizationTimestamp
    }

    fun updateSynchronizationTime(dashboardId: String, synchronizationTimestamp: Long): Long? {
        val query = Query(Criteria.where("_id").`is`(dashboardId))
        val update = Update().set("synchronizationTimestamp", synchronizationTimestamp)
        mongoTemplate.updateFirst(query, update, COLLECTION_NAME)
        return mongoTemplate.findOne(query, Dashboard::class.java, COLLECTION_NAME)?.synchronizationTimestamp
    }

    fun deleteDashboard(dashboardId: String) {
        val query = Query().addCriteria(Criteria.where("_id").`is`(dashboardId))
        mongoTemplate.remove(query, COLLECTION_NAME)
    }

    fun getPipelinesByDashboardId(dashboardId: String): List<Pipeline> {
        val dashboard: Dashboard? = getDashBoardDetailById(dashboardId)

        return dashboard?.pipelines?: emptyList()
    }
}
