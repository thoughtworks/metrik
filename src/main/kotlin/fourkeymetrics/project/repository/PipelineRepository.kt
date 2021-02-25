package fourkeymetrics.project.repository

import fourkeymetrics.common.encryption.decrypt
import fourkeymetrics.common.encryption.encrypt
import fourkeymetrics.project.exception.PipelineNotFoundException
import fourkeymetrics.project.model.Pipeline
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

    fun findById(pipelineId: String): Pipeline {
        val query = Query().addCriteria(
            Criteria.where("id").isEqualTo(pipelineId)
        )
        val pipeline = mongoTemplate.findOne(query, Pipeline::class.java) ?: throw PipelineNotFoundException()
        pipeline.credential = pipeline.credential.decrypt()
        return pipeline
    }

    fun findByIdAndProjectId(pipelineId: String, projectId: String): Pipeline {
        val query = Query().addCriteria(
            Criteria.where("id").isEqualTo(pipelineId).and("projectId").isEqualTo(projectId)
        )
        val pipeline = mongoTemplate.findOne(query, Pipeline::class.java) ?: throw PipelineNotFoundException()
        pipeline.credential = pipeline.credential.decrypt()
        return pipeline
    }

    fun findByNameAndProjectId(name: String, projectId: String): Pipeline? {
        val query = Query().addCriteria(Criteria.where("name").`is`(name).and("projectId").`is`(projectId))
        return mongoTemplate.findOne(query, Pipeline::class.java)
    }

    fun deleteById(pipelineId: String) {
        val query = Query().addCriteria(Criteria.where("id").isEqualTo(pipelineId))
        mongoTemplate.remove(query, Pipeline::class.java)
    }

    fun save(pipeline: Pipeline): Pipeline {
        pipeline.credential = pipeline.credential.encrypt()
        return mongoTemplate.save(pipeline)
    }

    fun saveAll(pipelines: List<Pipeline>): List<Pipeline> {
        pipelines.forEach { it.credential = it.credential.encrypt() }
        return mongoTemplate.insert(pipelines.toMutableList(), Pipeline::class.java).toList()
    }

    fun findByProjectId(projectId: String): List<Pipeline> {
        val query = Query().addCriteria(Criteria.where("projectId").isEqualTo(projectId))
        val pipelines = mongoTemplate.find(query, Pipeline::class.java)
        pipelines.forEach{ it.credential = it.credential.decrypt() }
        return pipelines
    }
}
