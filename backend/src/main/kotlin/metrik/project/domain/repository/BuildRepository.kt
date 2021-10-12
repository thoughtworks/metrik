package metrik.project.domain.repository

import metrik.project.domain.model.Execution
import metrik.project.domain.model.Status
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
import java.time.ZonedDateTime

@SuppressWarnings("TooManyFunctions")
@Component
class BuildRepository {
    companion object {
        private const val PROP_PIPELINEID: String = "pipelineId"
        private const val PROP_NUMBER: String = "number"
        private const val CREATED_TIMESTAMP: String = "timestamp"
        private const val BRANCH: String = "branch"
        private const val PROP_RESULT: String = "result"
        private const val twoWeeks: Long = 14
    }

    @Autowired
    private lateinit var mongoTemplate: MongoTemplate
    private var logger = LoggerFactory.getLogger(this.javaClass.name)

    private val collectionName = "build"

    fun save(executions: List<Execution>) {
        logger.info("Saving [${executions.size}] builds into DB")
        executions.parallelStream().forEach {
            save(it)
        }
    }

    fun save(execution: Execution) {
        val query = Query()
        query.addCriteria(
            Criteria
                .where(PROP_PIPELINEID).`is`(execution.pipelineId)
                .and(PROP_NUMBER).`is`(execution.number)
        )
        val found = mongoTemplate.findAndReplace(
            query,
            execution,
            collectionName
        )
        if (found == null) {
            mongoTemplate.save(execution, collectionName)
        }
    }

    fun clear(pipelineId: String) {
        logger.info("Removing all build records under pipeline [$pipelineId]")
        val query = Query.query(Criteria.where(PROP_PIPELINEID).isEqualTo(pipelineId))
        mongoTemplate.remove(query, collectionName)
    }

    fun getAllBuilds(pipelineIds: Collection<String>): List<Execution> {
        val query = Query.query(Criteria.where(PROP_PIPELINEID).`in`(pipelineIds))
        val result = mongoTemplate.find<Execution>(query, collectionName)
        logger.info("Query result size for builds in pipelines [$pipelineIds] is [${result.size}]")
        return result
    }

    fun getAllBuilds(pipelineId: String): List<Execution> {
        val query = Query.query(Criteria.where(PROP_PIPELINEID).isEqualTo(pipelineId))
        val result = mongoTemplate.find<Execution>(query, collectionName)
        logger.info("Query result size for builds in pipeline [$pipelineId] is [${result.size}]")
        return result
    }

    fun getByBuildNumber(pipelineId: String, number: Int): Execution? {
        val query = Query.query(Criteria.where(PROP_PIPELINEID).isEqualTo(pipelineId).and(PROP_NUMBER).`is`(number))
        val result = mongoTemplate.findOne<Execution>(query, collectionName)
        logger.info("Query result for build number [$number] in pipeline [$pipelineId] is [$result]")
        return result
    }

    fun getByBuildStatus(pipelineId: String, status: Status): List<Execution> {
        val query = Query.query(Criteria.where(PROP_PIPELINEID).isEqualTo(pipelineId).and(PROP_RESULT).`is`(status))
        val result = mongoTemplate.find<Execution>(query, collectionName)
        logger.info("Query result size for build status [$status] in pipeline [$pipelineId] is [${result.size}]")
        return result
    }

    fun getMaxBuild(pipelineId: String): Execution? {
        val query = Query
            .query(Criteria.where(PROP_PIPELINEID).`is`(pipelineId))
            .with(Sort.by(Sort.Direction.DESC, PROP_NUMBER))
            .limit(1)
        val result = mongoTemplate.findOne<Execution>(query, collectionName)
        logger.info(
            "Query result the most recent build through build number in pipeline [$pipelineId] is [${result?.number}]"
        )
        return result
    }

    fun getBambooJenkinsBuildNumbersNeedSync(pipelineId: String, maxBuildNumber: Int): List<Int> {
        val mostRecentBuildInDB = getMaxBuild(pipelineId)
        val syncStartIndex = (mostRecentBuildInDB?.number ?: 0) + 1
        val needSyncBuilds = getInProgressBuilds(pipelineId)
        val needSyncBuildNumbers = needSyncBuilds.map { it.number }

        return listOf(needSyncBuildNumbers, syncStartIndex..maxBuildNumber).flatten()
    }

    fun getInProgressBuilds(pipelineId: String, weeks: Long = twoWeeks): List<Execution> =
        getByBuildStatus(pipelineId, Status.IN_PROGRESS)
            .filter { it.timestamp > ZonedDateTime.now().minusDays(weeks).toEpochSecond() }

    fun getLatestDeployTimestamp(pipelineId: String): Long {
        val query = Query
            .query(Criteria.where(PROP_PIPELINEID).`is`(pipelineId))
        val result = mongoTemplate.find<Execution>(query, collectionName)
        val latestDeployTimestamp =
            result.flatMap { it.stages }.filter { it.status != Status.IN_PROGRESS }
                .maxByOrNull { it.startTimeMillis }?.startTimeMillis
        logger.debug(
            "Query result the most recent deploy timestamp in pipeline [$pipelineId] is [${latestDeployTimestamp}]"
        )
        val earliestBuildTimestamp = result.minByOrNull { it.timestamp }?.timestamp ?: 0

        return latestDeployTimestamp ?: earliestBuildTimestamp
    }

    fun getLatestBuild(pipelineId: String): Execution? {
        val query = Query
            .query(Criteria.where(PROP_PIPELINEID).`is`(pipelineId))
            .with(Sort.by(Sort.Direction.DESC, CREATED_TIMESTAMP))
            .limit(1)
        val result = mongoTemplate.findOne<Execution>(query, collectionName)
        logger.debug(
            "Query result the most recent build through timestamp in pipeline [$pipelineId] is [${result?.number}]"
        )
        return result
    }

    fun getPreviousBuild(pipelineId: String, timestamp: Long, branch: String): Execution? {
        val query = Query
            .query(
                Criteria.where(PROP_PIPELINEID)
                    .isEqualTo(pipelineId)
                    .and(CREATED_TIMESTAMP)
                    .lt(timestamp)
                    .and(BRANCH)
                    .isEqualTo(branch)
            )
            .with(Sort.by(Sort.Direction.DESC, CREATED_TIMESTAMP))
            .limit(1)
        val result = mongoTemplate.findOne<Execution>(query, collectionName)
        logger.debug(
            "Query result the most recent build through timestamp in pipeline [$pipelineId] is [${result?.number}]"
        )
        return result
    }
}
