package fourkeymetrics.dashboard.repository

import fourkeymetrics.dashboard.exception.DashboardNotFoundException
import fourkeymetrics.dashboard.model.Dashboard
import fourkeymetrics.dashboard.model.Dashboard1
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.find
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Component

@Component
class DashboardRepository1 {

    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    fun dashboardWithGivenNameExist(name: String): Boolean {
        val query = Query().addCriteria(Criteria.where("name").`is`(name))
        return !mongoTemplate.find<Dashboard1>(query).isEmpty()
    }

    fun findById(id: String): Dashboard1 {
        return mongoTemplate.findById(id, Dashboard1::class.java) ?: throw DashboardNotFoundException()
    }

    fun save(dashboard: Dashboard1): Dashboard1 {
        return mongoTemplate.save(dashboard)
    }

    fun findAll(): List<Dashboard1> {
        return mongoTemplate.findAll(Dashboard1::class.java)
    }

    fun deleteById(dashboardId: String) {
        val query = Query().addCriteria(Criteria.where("id").isEqualTo(dashboardId))
        mongoTemplate.remove(query, Dashboard::class.java)
    }

    fun updateSynchronizationTime(dashboardId: String, synchronizationTimestamp: Long): Long? {
        val query = Query(Criteria.where("_id").`is`(dashboardId))
        val update = Update().set("synchronizationTimestamp", synchronizationTimestamp)
        mongoTemplate.updateFirst(query, update, Dashboard1::class.java)
        return mongoTemplate.findOne(query, Dashboard::class.java)?.synchronizationTimestamp
    }

}
