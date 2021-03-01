package fourkeymetrics.project.repository

import fourkeymetrics.project.exception.ProjectNotFoundException
import fourkeymetrics.project.model.Project
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.context.annotation.Import
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit.jupiter.SpringExtension

@DataMongoTest
@ExtendWith(SpringExtension::class)
@Import(ProjectRepository::class)
internal class ProjectRepositoryTest {
    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    @Autowired
    private lateinit var projectRepository: ProjectRepository

    private val projectName = "project name"
    private val projectId = "project id"
    private val synchronisationTime = 98708L
    private val project = Project(projectId, projectName, synchronisationTime)

    @BeforeEach
    internal fun setUp(@Autowired mongoTemplate: MongoTemplate) {
        val collectionName = "project"
        mongoTemplate.dropCollection(collectionName)
    }

    @Test
    internal fun `test existByName will false true if project with name not exist`() {
        mongoTemplate.save(project)

        assertFalse(projectRepository.existWithGivenName("some name"))
    }

    @Test
    internal fun `test existByName will return true if project with name not exist`() {
        mongoTemplate.save(project)

        assertTrue(projectRepository.existWithGivenName(projectName))
    }

    @Test
    internal fun `test find by id will return project`() {
        mongoTemplate.save(project)

        val project1 = projectRepository.findById(projectId)

        assertEquals(project1.id, projectId)
        assertEquals(project1.name, projectName)
        assertEquals(project1.synchronizationTimestamp, synchronisationTime)
    }

    @Test
    internal fun `test find by id will throw ProjectNotFoundException if project not exist`() {
        mongoTemplate.save(project)

        val exception: ProjectNotFoundException = assertThrows { projectRepository.findById("some id") }

        assertEquals(exception.httpStatus, HttpStatus.NOT_FOUND)
    }

    @Test
    internal fun `test save project`() {
        projectRepository.save(project)

        val foundProject = mongoTemplate.findById(projectId, Project::class.java)

        assertEquals(foundProject?.id, projectId)
        assertEquals(foundProject?.name, projectName)
        assertEquals(foundProject?.synchronizationTimestamp, synchronisationTime)
    }

    @Test
    internal fun `test findAll project`() {
        mongoTemplate.save(project)

        val projects = projectRepository.findAll()

        assertEquals(projects.size, 1)
        assertEquals(projects[0].id, projectId)
        assertEquals(projects[0].name, projectName)
    }

    @Test
    internal fun `test delete project by id`() {
        mongoTemplate.save(project)
        projectRepository.deleteById(projectId)

        val foundProject = mongoTemplate.findById(projectId, Project::class.java)

        assertNull(foundProject)
    }

    @Test
    internal fun `test update synchronisation time`() {
        val newSynchronisationTime = 13432L
        mongoTemplate.save(project)

        val updateSynchronizationTime =
            projectRepository.updateSynchronizationTime(projectId, newSynchronisationTime)

        assertEquals(updateSynchronizationTime, newSynchronisationTime)


        val findById = mongoTemplate.findById(projectId, Project::class.java)

        assertEquals(findById?.synchronizationTimestamp, newSynchronisationTime)
    }
}