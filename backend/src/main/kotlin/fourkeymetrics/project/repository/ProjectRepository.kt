package fourkeymetrics.project.repository

import fourkeymetrics.project.exception.ProjectNotFoundException
import fourkeymetrics.project.model.Project
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.find
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Component

@Component
class ProjectRepository {
    @Autowired
    private lateinit var mongoTemplate: MongoTemplate
    private var logger = LoggerFactory.getLogger(this.javaClass.name)

    fun existWithGivenName(name: String): Boolean {
        val query = Query().addCriteria(Criteria.where("name").`is`(name))
        return mongoTemplate.find<Project>(query).isNotEmpty()
    }

    fun findById(id: String): Project {
        val result = mongoTemplate.findById(id, Project::class.java)
        logger.info("Query result project ID [$id] is [${result}]")
        return result ?: throw ProjectNotFoundException()
    }

    fun save(project: Project): Project {
        return mongoTemplate.save(project)
    }

    fun findAll(): List<Project> {
        val result = mongoTemplate.findAll(Project::class.java)
        logger.info("Query result size for all projects is [${result.size}]")
        return result
    }

    fun deleteById(projectId: String) {
        val query = Query().addCriteria(Criteria.where("id").isEqualTo(projectId))
        mongoTemplate.remove(query, Project::class.java)
    }

    fun updateSynchronizationTime(projectId: String, synchronizationTimestamp: Long): Long? {
        val query = Query(Criteria.where("_id").`is`(projectId))
        val update = Update().set("synchronizationTimestamp", synchronizationTimestamp)
        mongoTemplate.updateFirst(query, update, Project::class.java)
        return mongoTemplate.findOne(query, Project::class.java)?.synchronizationTimestamp
    }

}
