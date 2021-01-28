package fourkeymetrics.dashboard.repository

import fourkeymetrics.common.model.Build
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.find
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Component

@Component
class BuildRepository {
    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    private val collectionName = "build"

    fun getAllBuilds(pipelineId: String): List<Build> {
        val query = Query.query(Criteria.where("pipelineId").isEqualTo(pipelineId))
        return mongoTemplate.find(query, collectionName)
    }

    fun save(builds: List<Build>) {
        builds.parallelStream().forEach {
            val query = Query()
            query.addCriteria(
                Criteria
                    .where("pipelineId").`is`(it.pipelineId)
                    .and("number").`is`(it.number)
            )
            val found = mongoTemplate.findAndReplace(
                query,
                it,
                collectionName
            )
            if (found == null) {
                mongoTemplate.save(it, collectionName)
            }
        }
    }

    fun findByBuildNumber(pipelineId: String, number: Int): Build? {
        val query = Query.query(Criteria.where("pipelineId").isEqualTo(pipelineId).and("number").`is`(number))
        return mongoTemplate.findOne(query, Build::class.java, collectionName)
    }

    fun clear(pipelineId: String) {
        val query = Query.query(Criteria.where("pipelineId").isEqualTo(pipelineId))
        mongoTemplate.remove(query, collectionName)
    }
}
