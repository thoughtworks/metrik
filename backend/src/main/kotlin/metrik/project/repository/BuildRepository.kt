package metrik.project.repository

import metrik.common.model.Build
import metrik.common.model.Status
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.find
import org.springframework.data.mongodb.core.findOne
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Component

@Component
class BuildRepository {
    companion object {
        private const val PROP_PIPELINEID: String = "pipelineId"
        private const val PROP_NUMBER: String = "number"
        private const val PROP_RESULT: String = "result"
    }

    @Autowired
    private lateinit var mongoTemplate: MongoTemplate
    private var logger = LoggerFactory.getLogger(this.javaClass.name)

    private val collectionName = "build"

    fun save(builds: List<Build>) {
        logger.info("Saving [${builds.size}] builds into DB")
        builds.parallelStream().forEach {
            save(it)
        }
    }

    fun save(build: Build) {
        val query = Query()
        query.addCriteria(
            Criteria
                .where(PROP_PIPELINEID).`is`(build.pipelineId)
                .and(PROP_NUMBER).`is`(build.number)
        )
        val found = mongoTemplate.findAndReplace(
            query,
            build,
            collectionName
        )
        if (found == null) {
            mongoTemplate.save(build, collectionName)
        }
    }

    fun clear(pipelineId: String) {
        logger.info("Removing all build records under pipeline [$pipelineId]")
        val query = Query.query(Criteria.where(PROP_PIPELINEID).isEqualTo(pipelineId))
        mongoTemplate.remove(query, collectionName)
    }

    fun getAllBuilds(pipelineIds: Collection<String>): List<Build> {
        val query = Query.query(Criteria.where(PROP_PIPELINEID).`in`(pipelineIds))
        val result = mongoTemplate.find<Build>(query, collectionName)
        logger.info("Query result size for builds in pipelines [$pipelineIds] is [${result.size}]")
        return result
    }

    fun getAllBuilds(pipelineId: String): List<Build> {
        val query = Query.query(Criteria.where(PROP_PIPELINEID).isEqualTo(pipelineId))
        val result = mongoTemplate.find<Build>(query, collectionName)
        logger.info("Query result size for builds in pipeline [$pipelineId] is [${result.size}]")
        return result
    }

    fun getByBuildNumber(pipelineId: String, number: Int): Build? {
        val query = Query.query(Criteria.where(PROP_PIPELINEID).isEqualTo(pipelineId).and(PROP_NUMBER).`is`(number))
        val result = mongoTemplate.findOne<Build>(query, collectionName)
        logger.info("Query result for build number [$number] in pipeline [$pipelineId] is [$result]")
        return result
    }

    fun getByBuildStatus(pipelineId: String, status: Status): List<Build> {
        val query = Query.query(Criteria.where(PROP_PIPELINEID).isEqualTo(pipelineId).and(PROP_RESULT).`is`(status))
        val result = mongoTemplate.find<Build>(query, collectionName)
        logger.info("Query result size for build status [$status] in pipeline [$pipelineId] is [${result.size}]")
        return result
    }

    fun getMaxBuild(pipelineId: String): Build? {
        val query = Query
            .query(Criteria.where(PROP_PIPELINEID).`is`(pipelineId))
            .with(Sort.by(Sort.Direction.DESC, PROP_NUMBER))
            .limit(1)
        val result = mongoTemplate.findOne<Build>(query, collectionName)
        logger.info("Query result the most recent build in pipeline [$pipelineId] is [${result?.number}]")
        return result
    }

    fun getBuildNumbersNeedSync(pipelineId: String, maxBuildNumber: Int): List<Int> {
        val mostRecentBuildInDB = getMaxBuild(pipelineId)
        val syncStartIndex = (mostRecentBuildInDB?.number ?: 0) + 1
        val needSyncBuilds = getByBuildStatus(pipelineId, Status.IN_PROGRESS)
        val needSyncBuildNumbers = needSyncBuilds.map { it.number }

        return listOf(needSyncBuildNumbers, syncStartIndex..maxBuildNumber).flatten()
    }
}
