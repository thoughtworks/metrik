package metrik.project.domain.repository

import metrik.project.domain.model.PipelineConfiguration
import metrik.project.exception.PipelineNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Component

@Component
class PipelineRepository {
    @Autowired
    private lateinit var mongoTemplate: MongoTemplate
    private var logger = LoggerFactory.getLogger(this.javaClass.name)

    fun findById(pipelineId: String): PipelineConfiguration {
        val query = Query().addCriteria(
            Criteria.where("id").isEqualTo(pipelineId)
        )

        val result = mongoTemplate.findOne(query, PipelineConfiguration::class.java)
        logger.info("Query result for pipeline with ID [$pipelineId] is [$result]")

        return result ?: throw PipelineNotFoundException()
    }

    fun findByIdAndProjectId(pipelineId: String, projectId: String): PipelineConfiguration {
        val query = Query().addCriteria(
            Criteria.where("id").isEqualTo(pipelineId).and("projectId").isEqualTo(projectId)
        )
        val result = mongoTemplate.findOne(query, PipelineConfiguration::class.java)

        logger.info(
            "Query result for pipeline with project ID [$projectId] and ID [$pipelineId] is " +
                "[name: ${result?.name}, url: ${result?.url}, type: ${result?.type}]"
        )
        return result ?: throw PipelineNotFoundException()
    }

    fun findByNameAndProjectId(name: String, projectId: String): PipelineConfiguration? {
        val query = Query().addCriteria(Criteria.where("name").`is`(name).and("projectId").`is`(projectId))

        val result = mongoTemplate.findOne(query, PipelineConfiguration::class.java)

        logger.info("Query result for pipeline with name [$name] and project ID [$projectId] is [$result]")
        return result
    }

    fun findByProjectId(projectId: String): List<PipelineConfiguration> {
        val query = Query().addCriteria(Criteria.where("projectId").isEqualTo(projectId))

        val result = mongoTemplate.find(query, PipelineConfiguration::class.java)

        logger.info("Query result size for pipeline with project ID [$projectId] is [${result.size}]")
        return result
    }

    fun deleteById(pipelineId: String) {
        val query = Query().addCriteria(Criteria.where("id").isEqualTo(pipelineId))
        mongoTemplate.remove(query, PipelineConfiguration::class.java)
    }

    fun save(pipeline: PipelineConfiguration): PipelineConfiguration {
        return mongoTemplate.save(pipeline)
    }

    fun saveAll(pipelines: List<PipelineConfiguration>): List<PipelineConfiguration> {
        return mongoTemplate.insert(pipelines.toMutableList(), PipelineConfiguration::class.java).toList()
    }
}
