package metrik.project.domain.repository

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import metrik.project.domain.model.PipelineConfiguration
import metrik.project.exception.PipelineNotFoundException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@Import(PipelineRepository::class)
internal class PipelineRepositoryTest {
    @MockkBean(relaxed = true)
    private lateinit var mongoTemplate: MongoTemplate

    @Autowired
    private lateinit var pipelineRepository: PipelineRepository

    private val pipelineId = "pipelineId"
    private val projectId = "projectId"
    private val username = "tester"
    private val credential = "fake credential"

    @Test
    fun `should return Pipeline when findById() called given pipelineId exist in repo`() {
        every { mongoTemplate.hint(PipelineConfiguration::class).findOne<PipelineConfiguration>(any(), any()) } returns PipelineConfiguration(
            id = pipelineId,
            username = username,
            credential = credential
        )

        val pipeline = pipelineRepository.findById(pipelineId)

        assertEquals(pipelineId, pipeline.id)
    }

    @Test
    fun `should throw PipelineNotFoundException exception when findById() called given pipelineId not exist in repo`() {
        every { mongoTemplate.hint(PipelineConfiguration::class).findOne<PipelineConfiguration>(any(), any()) } returns null

        assertThrows<PipelineNotFoundException> { pipelineRepository.findById(pipelineId) }
    }

    @Test
    fun `should return pipeline when findByIdAndProjectId() called given the pipeline exist`() {
        every { mongoTemplate.hint(PipelineConfiguration::class).findOne<PipelineConfiguration>(any(), any()) } returns PipelineConfiguration(
            id = pipelineId,
            username = username,
            credential = credential
        )

        val pipeline = pipelineRepository.findByIdAndProjectId(pipelineId, projectId)

        assertEquals(pipelineId, pipeline.id)
    }

    @Test
    fun `should throw PipelineNotFoundException exception when findByIdAndProjectId() called given pipelineId not exist in repo`() {
        every { mongoTemplate.hint(PipelineConfiguration::class).findOne<PipelineConfiguration>(any(), any()) } returns null

        assertThrows<PipelineNotFoundException> { pipelineRepository.findByIdAndProjectId(pipelineId, projectId) }
    }

    @Test
    fun `should return pipeline when findByNameAndProjectId() called given pipelineExist`() {
        val pipeline = PipelineConfiguration(id = pipelineId, username = username, credential = credential)
        every { mongoTemplate.hint(PipelineConfiguration::class).findOne<PipelineConfiguration>(any(), any()) } returns pipeline
        assertEquals(pipelineRepository.findByNameAndProjectId("pipelineName", "projectId"), pipeline)
    }

    @Test
    fun `should return null when findByNameAndProjectId() called given name with projectId not exist in repo`() {
        every { mongoTemplate.hint(PipelineConfiguration::class).findOne<PipelineConfiguration>(any(), any()) } returns null
        assertNull(pipelineRepository.findByNameAndProjectId("name", "projectId"))
    }

    @Test
    fun `should invoke mongoTemplate to delete when deleteById() called`() {
        pipelineRepository.deleteById(pipelineId)
        verify(exactly = 1) { mongoTemplate.remove(any(), PipelineConfiguration::class.java) }
    }

    @Test
    fun `should return Pipeline when save() called`() {
        val pipeline = PipelineConfiguration(id = pipelineId, username = username, credential = credential)
        every { mongoTemplate.hint(PipelineConfiguration::class).save<PipelineConfiguration>(pipeline) } returns pipeline

        pipelineRepository.save(pipeline)
        verify {
            mongoTemplate.save<PipelineConfiguration>(
                withArg {
                    assertEquals("pipelineId", it.id)
                    assertEquals("", it.name)
                }
            )
        }
    }

    @Test
    fun `should return all Pipelines when saveAll() called`() {
        val pipeline = PipelineConfiguration(id = pipelineId, username = username, credential = credential)
        val pipelineList = listOf(pipeline)
        every { mongoTemplate.insert<PipelineConfiguration>(any(), PipelineConfiguration::class.java) } returns pipelineList

        val result = pipelineRepository.saveAll(pipelineList)

        assertEquals(1, result.size)
        assertEquals(pipelineId, result[0].id)
    }

    @Test
    fun `should invoke mongoTemplate to find when findByProjectId() called`() {
        every { mongoTemplate.find(any(), PipelineConfiguration::class.java) } returns listOf(
            PipelineConfiguration(
                id = pipelineId,
                username = username,
                credential = credential
            )
        )
        pipelineRepository.findByProjectId("projectId")
        verify { mongoTemplate.find(any(), PipelineConfiguration::class.java) }
    }
}
