package fourkeymetrics.dashboard.repository

import fourkeymetrics.MockitoHelper.anyObject
import fourkeymetrics.MockitoHelper.argThat
import fourkeymetrics.dashboard.exception.PipelineNotFoundException
import fourkeymetrics.dashboard.model.Pipeline
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
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
    private val dashboardId = "dashboardId"

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
    internal fun `should return pipeline when findByIdAndDashboardId() called given the pipeline exist`() {
        `when`(mongoTemplate.findOne(anyObject(), eq(Pipeline::class.java))).thenReturn(Pipeline(id = pipelineId))

        val pipeline = pipelineRepository.findByIdAndDashboardId(pipelineId, dashboardId)

        assertEquals(pipelineId, pipeline.id)
    }

    @Test
    internal fun `should throw PipelineNotFoundException exception when findByIdAndDashboardId() called given pipelineId not exist in repo`() {
        `when`(mongoTemplate.findOne(anyObject(), eq(Pipeline::class.java))).thenReturn(null)

        assertThrows<PipelineNotFoundException> { pipelineRepository.findByIdAndDashboardId(pipelineId, dashboardId) }
    }

    @Test
    internal fun `should return true when pipelineExistWithNameAndDashboardId() called given name with dashboardId exist in repo`() {
        `when`(mongoTemplate.find(anyObject(), eq(Pipeline::class.java))).thenReturn(listOf(Pipeline()))
        assertTrue(pipelineRepository.pipelineExistWithNameAndDashboardId("pipelineName", "dashboardId"))
    }

    @Test
    internal fun `should return false when pipelineExistWithNameAndDashboardId() called given name with dashboardId not exist in repo`() {
        `when`(mongoTemplate.find(anyObject(), eq(Pipeline::class.java))).thenReturn(emptyList<Pipeline>())
        assertFalse(pipelineRepository.pipelineExistWithNameAndDashboardId("name", "dashboardId"))
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
    internal fun `should invoke mongoTemplate to delete when deleteByDashboardId() called`() {
        pipelineRepository.deleteByDashboardId("dashboardId")
        verify(mongoTemplate).remove(anyObject(), eq(Pipeline::class.java))
    }

    @Test
    internal fun `should invoke mongoTemplate to find when findByDashboardId() called`() {
        pipelineRepository.findByDashboardId("dashboardId")
        verify(mongoTemplate).find(anyObject(), eq(Pipeline::class.java))
    }
}