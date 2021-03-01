package fourkeymetrics.project.repository

import fourkeymetrics.MockitoHelper.anyObject
import fourkeymetrics.MockitoHelper.argThat
import fourkeymetrics.project.exception.PipelineNotFoundException
import fourkeymetrics.project.model.Pipeline
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.eq
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.mongodb.core.MongoTemplate

@ExtendWith(MockitoExtension::class)
internal class PipelineRepositoryTest {
    @Mock
    private lateinit var mongoTemplate: MongoTemplate

    @InjectMocks
    private lateinit var pipelineRepository: PipelineRepository

    private val pipelineId = "pipelineId"
    private val projectId = "projectId"

    @Test
    internal fun `should return Pipeline when findById() called given pipelineId exist in repo`() {
        `when`(mongoTemplate.findOne(anyObject(), eq(Pipeline::class.java))).thenReturn(Pipeline(id = pipelineId))

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
        `when`(mongoTemplate.findOne(anyObject(), eq(Pipeline::class.java))).thenReturn(Pipeline(id = pipelineId))

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
        val pipeline = Pipeline()
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
        val pipeline = Pipeline(id = pipelineId, name = "name")
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
        val pipeline = Pipeline(id = pipelineId, name = "name")
        val pipelineList = listOf(pipeline)
        `when`(mongoTemplate.insert<Pipeline>(anyObject(), eq(Pipeline::class.java))).thenReturn(pipelineList)

        val result = pipelineRepository.saveAll(pipelineList)

        assertEquals(1, result.size)
        assertEquals(pipelineId, result[0].id)
    }

    @Test
    internal fun `should invoke mongoTemplate to find when findByProjectId() called`() {
        pipelineRepository.findByProjectId("projectId")
        verify(mongoTemplate).find(anyObject(), eq(Pipeline::class.java))
    }
}