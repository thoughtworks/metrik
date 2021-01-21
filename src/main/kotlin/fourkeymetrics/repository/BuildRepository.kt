package fourkeymetrics.repository

import fourkeymetrics.model.Build
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.find
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Component

@Component
class BuildRepository {
    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    private val collectionName = "build"

    fun getAllBuilds(pipelineId: String): List<Build> {
        return mongoTemplate.findAll(Build::class.java, collectionName)
    }

    fun getBuildsByTimePeriod(pipelineId: String, startTimestamp: Long, endTimestamp: Long): List<Build> {
        val query = Query.query(Criteria.where("timestamp").gte(startTimestamp).lte(endTimestamp))
        return mongoTemplate.find(query, collectionName)
    }

    fun save(builds: List<Build>) {
        builds.parallelStream().forEach { mongoTemplate.save(it, collectionName) }
    }
}
