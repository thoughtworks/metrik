package fourkeymetrics.dashboard.controller.applicationservice

import fourkeymetrics.MockitoHelper.anyObject
import fourkeymetrics.MockitoHelper.argThat
import fourkeymetrics.dashboard.buildPipeline
import fourkeymetrics.dashboard.model.PipelineType
import fourkeymetrics.dashboard.repository.BuildRepository
import fourkeymetrics.dashboard.repository.DashboardRepository
import fourkeymetrics.dashboard.repository.PipelineRepository
import fourkeymetrics.dashboard.service.jenkins.JenkinsPipelineService
import fourkeymetrics.exception.BadRequestException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpStatus

@ExtendWith(MockitoExtension::class)
internal class PipelineApplicationServiceTest {
    @Mock
    private lateinit var jenkinsPipelineService: JenkinsPipelineService

    @Mock
    private lateinit var pipelineRepository: PipelineRepository

    @Mock
    private lateinit var dashboardRepository: DashboardRepository

    @Mock
    private lateinit var buildRepository: BuildRepository

    @InjectMocks
    private lateinit var pipelineApplicationService: PipelineApplicationService

    @Test
    internal fun `should throw BadRequestException when verifyPipeline() called given pipeline type is not JENKINS`() {
        val pipeline = buildPipeline().copy(type = PipelineType.BAMBOO)

        val exception = assertThrows<BadRequestException> {
            pipelineApplicationService.verifyPipelineConfiguration(pipeline)
        }

        assertEquals("Pipeline type not support", exception.message)
        assertEquals(HttpStatus.BAD_REQUEST, exception.httpStatus)
    }

    @Test
    internal fun `should invoke jenkinsPipelineService to verify when verifyPipeline() called given pipeline type is JENKINS`() {
        val pipeline = buildPipeline().copy(type = PipelineType.JENKINS)

        pipelineApplicationService.verifyPipelineConfiguration(pipeline)

        verify(jenkinsPipelineService, times(1)).verifyPipelineConfiguration(
            pipeline.url, pipeline.username, pipeline.credential
        )
    }

    @Test
    internal fun `should throw BadRequestException when createPipeline() called given pipeline name already exist`() {
        val pipeline = buildPipeline()
        val dashboardId = pipeline.dashboardId
        `when`(pipelineRepository.pipelineExistWithNameAndDashboardId(pipeline.name, dashboardId)).thenReturn(
            true
        )

        val exception = assertThrows<BadRequestException> { pipelineApplicationService.createPipeline(pipeline) }

        verify(dashboardRepository).findById(dashboardId)
        verify(pipelineRepository, times(0)).save(anyObject())
        assertEquals("Pipeline name already exist", exception.message)
        assertEquals(HttpStatus.BAD_REQUEST, exception.httpStatus)
    }

    @Test
    internal fun `should return saved pipeline when createPipeline() called given valid Pipeline`() {
        val pipeline = buildPipeline()
        val dashboardId = pipeline.dashboardId
        `when`(pipelineRepository.pipelineExistWithNameAndDashboardId(pipeline.name, dashboardId)).thenReturn(false)
        `when`(pipelineRepository.save(anyObject())).thenReturn(pipeline)

        val result = pipelineApplicationService.createPipeline(pipeline)

        verify(dashboardRepository).findById(dashboardId)
        verify(jenkinsPipelineService, times(1)).verifyPipelineConfiguration(
            pipeline.url, pipeline.username, pipeline.credential
        )
        verify(pipelineRepository).save(argThat {
            assertEquals(dashboardId, it.dashboardId)
            assertNotNull(it.id)
            assertEquals(pipeline.name, it.name)
            assertEquals(pipeline.username, it.username)
            assertEquals(pipeline.credential, it.credential)
            assertEquals(pipeline.url, it.url)
            assertEquals(pipeline.type, it.type)
            true
        })
        assertEquals(pipeline.name, result.name)
        assertEquals(pipeline.url, result.url)
        assertEquals(pipeline.username, result.username)
        assertEquals(pipeline.credential, result.credential)
        assertEquals(pipeline.type, result.type)
    }

    @Test
    internal fun `should return saved pipeline when updatePipeline() called and successfully`() {
        val pipeline = buildPipeline()
        val savedPipeline = buildPipeline()
        `when`(pipelineRepository.save(anyObject())).thenReturn(savedPipeline)

        val result = pipelineApplicationService.updatePipeline(pipeline)

        verify(dashboardRepository).findById(pipeline.dashboardId)
        verify(pipelineRepository).findById(pipeline.id)
        verify(jenkinsPipelineService, times(1)).verifyPipelineConfiguration(
            pipeline.url, pipeline.username, pipeline.credential
        )
        verify(pipelineRepository).save(argThat {
            assertEquals(pipeline.dashboardId, it.dashboardId)
            assertEquals(pipeline.id, it.id)
            assertEquals(pipeline.name, it.name)
            assertEquals(pipeline.username, it.username)
            assertEquals(pipeline.credential, it.credential)
            assertEquals(pipeline.url, it.url)
            assertEquals(pipeline.type, it.type)
            true
        })
        assertEquals(pipeline.name, result.name)
        assertEquals(pipeline.url, result.url)
        assertEquals(pipeline.username, result.username)
        assertEquals(pipeline.credential, result.credential)
        assertEquals(pipeline.type, result.type)
    }

    @Test
    internal fun `should return pipeline when getPipeline() called`() {
        val pipeline = buildPipeline()
        val pipelineId = pipeline.id
        val dashboardId = pipeline.dashboardId
        `when`(pipelineRepository.findById(pipelineId)).thenReturn(pipeline)

        val result = pipelineApplicationService.getPipeline(dashboardId, pipelineId)

        verify(dashboardRepository).findById(dashboardId)
        assertEquals(pipeline.id, result.id)
        assertEquals(pipeline.name, result.name)
        assertEquals(pipeline.username, result.username)
        assertEquals(pipeline.credential, result.credential)
        assertEquals(pipeline.url, result.url)
        assertEquals(pipeline.type, result.type)
    }

    @Test
    internal fun `should delete pipeline and its builds when deletePipeline() called`() {
        val pipelineId = "pipelineId"
        val dashboardId = "dashboardId"
        pipelineApplicationService.deletePipeline(dashboardId, pipelineId)

        verify(dashboardRepository).findById(dashboardId)
        verify(pipelineRepository).findById(pipelineId)
        verify(pipelineRepository).deleteById(pipelineId)
        verify(buildRepository).clear(pipelineId)
    }
}