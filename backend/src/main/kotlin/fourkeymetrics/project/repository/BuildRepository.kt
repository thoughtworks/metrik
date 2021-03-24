package fourkeymetrics.project.repository

import fourkeymetrics.common.model.Build
import org.slf4j.LoggerFactory
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
    private var logger = LoggerFactory.getLogger(this.javaClass.name)

    private val collectionName = "build"

    fun getAllBuilds(pipelineId: String): List<Build> {
        val query = Query.query(Criteria.where("pipelineId").isEqualTo(pipelineId))
        val result = mongoTemplate.find<Build>(query, collectionName)
        logger.debug("Query result size for builds in pipeline [$pipelineId] is [${result.size}]")
        return result
    }

    fun getAllBuilds(pipelineIds: Collection<String>): List<Build> {
        val query = Query.query(Criteria.where("pipelineId").`in`(pipelineIds))
        val result = mongoTemplate.find<Build>(query, collectionName)
        logger.debug("Query result size for builds in pipelines [$pipelineIds] is [${result.size}]")
        return result
    }

    fun save(builds: List<Build>) {
        logger.debug("Saving [${builds.size}] builds into DB")
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
        val result = mongoTemplate.findOne(query, Build::class.java, collectionName)
        logger.debug("Query result for build number [$number] in pipeline [$pipelineId] is [${result}]")
        return result
    }

    fun clear(pipelineId: String) {
        logger.debug("Removing all build records under pipeline [$pipelineId]")
        val query = Query.query(Criteria.where("pipelineId").isEqualTo(pipelineId))
        mongoTemplate.remove(query, collectionName)
    }
}
