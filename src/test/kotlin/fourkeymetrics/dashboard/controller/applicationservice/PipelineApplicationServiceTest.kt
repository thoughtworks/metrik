package fourkeymetrics.dashboard.controller.applicationservice

import fourkeymetrics.MockitoHelper.anyObject
import fourkeymetrics.MockitoHelper.argThat
import fourkeymetrics.dashboard.buildPipelineRequest
import fourkeymetrics.dashboard.buildPipelineVerificationRequest
import fourkeymetrics.dashboard.model.Pipeline
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

    private val dashboardId = "dashboardId"
    private val pipelineId = "pipelineId"

    @InjectMocks
    private lateinit var pipelineApplicationService: PipelineApplicationService

    @Test
    internal fun `should throw BadRequestException when verifyPipeline() called given pipeline type is not JENKINS`() {
        val pipelineVerificationRequest = buildPipelineVerificationRequest().copy(type = PipelineType.BAMBOO)

        val exception = assertThrows<BadRequestException> {
            pipelineApplicationService.verifyPipelineConfiguration(
                pipelineVerificationRequest
            )
        }

        assertEquals("Pipeline type not support", exception.message)
        assertEquals(HttpStatus.BAD_REQUEST, exception.httpStatus)
    }

    @Test
    internal fun `should invoke jenkinsPipelineService to verify when verifyPipeline() called given pipeline type is JENKINS`() {
        val pipelineVerificationRequest = buildPipelineVerificationRequest().copy(type = PipelineType.JENKINS)

        pipelineApplicationService.verifyPipelineConfiguration(pipelineVerificationRequest)

        verify(jenkinsPipelineService, times(1)).verifyPipelineConfiguration(
            pipelineVerificationRequest.url,
            pipelineVerificationRequest.username,
            pipelineVerificationRequest.credential
        )
    }

    @Test
    internal fun `should throw BadRequestException when createPipeline() called given pipeline name already exist`() {
        val pipelineRequest = buildPipelineRequest()
        `when`(pipelineRepository.pipelineExistWithNameAndDashboardId(pipelineRequest.name, dashboardId)).thenReturn(
            true
        )

        val exception = assertThrows<BadRequestException> {
            pipelineApplicationService.createPipeline(
                dashboardId,
                pipelineRequest
            )
        }

        verify(dashboardRepository).findById(dashboardId)
        verify(pipelineRepository, times(0)).save(anyObject())
        assertEquals("Pipeline name already exist", exception.message)
        assertEquals(HttpStatus.BAD_REQUEST, exception.httpStatus)
    }

    @Test
    internal fun `should return PipelineResponse when createPipeline() called given valid PipelineRequest`() {
        val pipelineRequest = buildPipelineRequest()
        `when`(pipelineRepository.pipelineExistWithNameAndDashboardId(pipelineRequest.name, dashboardId)).thenReturn(
            false
        )

        val result = pipelineApplicationService.createPipeline(dashboardId, pipelineRequest)

        verify(dashboardRepository).findById(dashboardId)
        verify(pipelineRepository).save(argThat {
            assertEquals(dashboardId, it.dashboardId)
            assertNotNull(it.id)
            assertEquals(pipelineRequest.name, it.name)
            assertEquals(pipelineRequest.username, it.username)
            assertEquals(pipelineRequest.credential, it.credential)
            assertEquals(pipelineRequest.url, it.url)
            assertEquals(pipelineRequest.type, it.type.toString())
            true
        })
        assertEquals(pipelineRequest.name, result.name)
        assertEquals(pipelineRequest.url, result.url)
        assertEquals(pipelineRequest.username, result.username)
        assertEquals(pipelineRequest.credential, result.credential)
        assertEquals(pipelineRequest.type, result.type.toString())
    }

    @Test
    internal fun `should return PipelineResponse when updatePipeline() called and successfully`() {
        val pipelineRequest = buildPipelineRequest()
        val newPipeline = Pipeline(
            id = pipelineId,
            dashboardId = dashboardId,
            name = pipelineRequest.name,
            username = pipelineRequest.username,
            credential = pipelineRequest.credential,
            url = pipelineRequest.url,
            type = PipelineType.valueOf(pipelineRequest.type)
        )
        `when`(pipelineRepository.save(anyObject())).thenReturn(newPipeline)

        val result = pipelineApplicationService.updatePipeline(dashboardId, pipelineId, pipelineRequest)

        verify(dashboardRepository).findById(dashboardId)
        verify(pipelineRepository).findById(pipelineId)
        verify(pipelineRepository).save(argThat {
            assertEquals(dashboardId, dashboardId)
            assertEquals(pipelineId, it.id)
            assertEquals(pipelineRequest.name, it.name)
            assertEquals(pipelineRequest.username, it.username)
            assertEquals(pipelineRequest.credential, it.credential)
            assertEquals(pipelineRequest.url, it.url)
            assertEquals(pipelineRequest.type, it.type.toString())
            true
        })
        assertEquals(pipelineRequest.name, result.name)
        assertEquals(pipelineRequest.url, result.url)
        assertEquals(pipelineRequest.username, result.username)
        assertEquals(pipelineRequest.credential, result.credential)
        assertEquals(pipelineRequest.type, result.type.toString())
    }

    @Test
    internal fun `should return PipelineResponse when getPipeline() called`() {
        val pipeline = Pipeline(
            id = pipelineId,
            dashboardId = dashboardId,
            name = "name",
            username = "username",
            credential = "credential",
            url = "url",
            type = PipelineType.JENKINS
        )
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
        pipelineApplicationService.deletePipeline(dashboardId, pipelineId)

        verify(dashboardRepository).findById(dashboardId)
        verify(pipelineRepository).findById(pipelineId)
        verify(pipelineRepository).deleteById(pipelineId)
        verify(buildRepository).clear(pipelineId)
    }
}