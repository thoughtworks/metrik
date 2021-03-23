package fourkeymetrics.project.repository

import fourkeymetrics.project.exception.PipelineNotFoundException
import fourkeymetrics.project.model.Pipeline
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

    fun findById(pipelineId: String): Pipeline {
        val query = Query().addCriteria(
            Criteria.where("id").isEqualTo(pipelineId)
        )

        val result = mongoTemplate.findOne(query, Pipeline::class.java)
        logger.info("Query result for pipeline with ID [$pipelineId] is [$result]")

        return result ?: throw PipelineNotFoundException()
    }

    fun findByIdAndProjectId(pipelineId: String, projectId: String): Pipeline {
        val query = Query().addCriteria(
            Criteria.where("id").isEqualTo(pipelineId).and("projectId").isEqualTo(projectId)
        )
        val result = mongoTemplate.findOne(query, Pipeline::class.java)

        logger.info("Query result for pipeline with project ID [$projectId] and ID [$pipelineId] is [$result]")
        return result ?: throw PipelineNotFoundException()
    }

    fun findByNameAndProjectId(name: String, projectId: String): Pipeline? {
        val query = Query().addCriteria(Criteria.where("name").`is`(name).and("projectId").`is`(projectId))

        val result = mongoTemplate.findOne(query, Pipeline::class.java)

        logger.info("Query result for pipeline with name [$name] and project ID [$projectId] is [$result]")
        return result
    }

    fun findByProjectId(projectId: String): List<Pipeline> {
        val query = Query().addCriteria(Criteria.where("projectId").isEqualTo(projectId))

        val result = mongoTemplate.find(query, Pipeline::class.java)

        logger.info("Query result size for pipeline with project ID [$projectId] is [${result.size}]")
        return result
    }

    fun deleteById(pipelineId: String) {
        val query = Query().addCriteria(Criteria.where("id").isEqualTo(pipelineId))
        mongoTemplate.remove(query, Pipeline::class.java)
    }

    fun save(pipeline: Pipeline): Pipeline {
        return mongoTemplate.save(pipeline)
    }

    fun saveAll(pipelines: List<Pipeline>): List<Pipeline> {
        return mongoTemplate.insert(pipelines.toMutableList(), Pipeline::class.java).toList()
    }
}
