package metrik.project.repository

import metrik.MockitoHelper.anyObject
import metrik.common.encryption.AESEncryptionService
import metrik.project.exception.PipelineNotFoundException
import metrik.project.model.Pipeline
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@Import(PipelineRepository::class)
internal class PipelineRepositoryTest {
    @MockBean
    private lateinit var mongoTemplate: MongoTemplate

    @Autowired
    private lateinit var pipelineRepository: PipelineRepository

    private val pipelineId = "pipelineId"
    private val projectId = "projectId"
    private val username = "tester"
    private val credential = "fake credential"


    @Test
    internal fun `should return Pipeline when findById() called given pipelineId exist in repo`() {
        `when`(mongoTemplate.findOne(anyObject(), eq(Pipeline::class.java))).thenReturn(
            Pipeline(id = pipelineId, username = username, credential = credential)
        )

        val pipeline = pipelineRepository.findById(pipelineId)

        assertEquals(pipelineId, pipeline.id)
    }

    @Test
    internal fun `should throw PipelineNotFoundException exception when findById() called given pipelineId not exist in repo`() {
        `when`(mongoTemplate.findOne(anyObject(), eq(Pipeline::class.java))).thenReturn(null)

        assertThrows<PipelineNotFoundException> { pipelineRepository.findById(pipelineId) }
    }

    @Test
    internal fun `should return pipeline when findByIdAndProjectId() called given the pipeline exist`() {
        `when`(mongoTemplate.findOne(anyObject(), eq(Pipeline::class.java))).thenReturn(
            Pipeline(
                id = pipelineId,
                username = username,
                credential = credential
            )
        )

        val pipeline = pipelineRepository.findByIdAndProjectId(pipelineId, projectId)

        assertEquals(pipelineId, pipeline.id)
    }

    @Test
    internal fun `should throw PipelineNotFoundException exception when findByIdAndProjectId() called given pipelineId not exist in repo`() {
        `when`(mongoTemplate.findOne(anyObject(), eq(Pipeline::class.java))).thenReturn(null)

        assertThrows<PipelineNotFoundException> { pipelineRepository.findByIdAndProjectId(pipelineId, projectId) }
    }

    @Test
    internal fun `should return pipeline when findByNameAndProjectId() called given pipelineExist`() {
        val pipeline = Pipeline(id = pipelineId, username = username, credential = credential)
        `when`(mongoTemplate.findOne(anyObject(), eq(Pipeline::class.java))).thenReturn(pipeline)
        assertEquals(pipelineRepository.findByNameAndProjectId("pipelineName", "projectId"), pipeline)
    }

    @Test
    internal fun `should return null when findByNameAndProjectId() called given name with projectId not exist in repo`() {
        `when`(mongoTemplate.findOne(anyObject(), eq(Pipeline::class.java))).thenReturn(null)
        assertNull(pipelineRepository.findByNameAndProjectId("name", "projectId"))
    }

    @Test
    internal fun `should invoke mongoTemplate to delete when deleteById() called`() {
        pipelineRepository.deleteById(pipelineId)
        verify(mongoTemplate).remove(anyObject(), eq(Pipeline::class.java))
    }

    @Test
    internal fun `should return Pipeline when save() called`() {
        val pipeline = Pipeline(id = pipelineId, username = username, credential = credential)
        `when`(mongoTemplate.save<Pipeline>(anyObject())).thenReturn(pipeline)

        pipelineRepository.save(pipeline)
        verify(mongoTemplate).save<Pipeline>(argThat {
            assertEquals(pipeline.id, it.id)
            assertEquals(pipeline.name, it.name)
            true
        })
    }

    @Test
    internal fun `should return all Pipelines when saveAll() called`() {
        val pipeline = Pipeline(id = pipelineId, username = username, credential = credential)
        val pipelineList = listOf(pipeline)
        `when`(mongoTemplate.insert<Pipeline>(anyObject(), eq(Pipeline::class.java))).thenReturn(pipelineList)

        val result = pipelineRepository.saveAll(pipelineList)

        assertEquals(1, result.size)
        assertEquals(pipelineId, result[0].id)
    }

    @Test
    internal fun `should invoke mongoTemplate to find when findByProjectId() called`() {
        `when`(mongoTemplate.find(anyObject(), eq(Pipeline::class.java))).thenReturn(
            listOf(Pipeline(id = pipelineId, username = username, credential = credential))
        )
        pipelineRepository.findByProjectId("projectId")
        verify(mongoTemplate).find(anyObject(), eq(Pipeline::class.java))
    }
}