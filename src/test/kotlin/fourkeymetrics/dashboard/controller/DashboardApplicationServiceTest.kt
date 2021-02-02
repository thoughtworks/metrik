package fourkeymetrics.dashboard.controller

import com.fasterxml.jackson.databind.ObjectMapper
import fourkeymetrics.dashboard.controller.vo.PipelineStagesResponse
import fourkeymetrics.dashboard.controller.vo.PipelineVerificationRequest
import fourkeymetrics.dashboard.exception.DashboardNotFoundException
import fourkeymetrics.dashboard.model.Dashboard
import fourkeymetrics.dashboard.model.Pipeline
import fourkeymetrics.dashboard.model.PipelineType
import fourkeymetrics.dashboard.repository.DashboardRepository
import fourkeymetrics.dashboard.service.jenkins.JenkinsPipelineService
import fourkeymetrics.exception.ApplicationException
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@Import(DashboardApplicationService::class, ObjectMapper::class)
class DashboardApplicationServiceTest {
    @Autowired
    private lateinit var dashboardApplicationService: DashboardApplicationService

    @MockBean
    private lateinit var jenkinsPipelineService: JenkinsPipelineService

    @MockBean
    private lateinit var dashboardRepository: DashboardRepository

    @Test
    internal fun `should not throw exception when type is jenkins`() {
        val url = "http://jenkins.io"
        val username = "name"
        val credential = "credential"
        val type = PipelineType.JENKINS
        Mockito.doNothing().`when`(jenkinsPipelineService).verifyPipelineConfiguration(url, username, credential)
        Assertions.assertThatCode {
            dashboardApplicationService.verifyPipeline(PipelineVerificationRequest(url, username, credential, type))
        }.doesNotThrowAnyException()
    }

    @Test
    internal fun `should  throw exception when type is not jenkins`() {
        val url = "http://jenkins.io"
        val username = "name"
        val credential = "credential"
        val type = PipelineType.BAMBOO
        Mockito.doNothing().`when`(jenkinsPipelineService).verifyPipelineConfiguration(url, username, credential)
        Assertions.assertThatThrownBy {
            dashboardApplicationService.verifyPipeline(PipelineVerificationRequest(url, username, credential, type))
        }.hasMessage("Pipeline type not support")
    }

    @Test
    internal fun `should  throw exception when verify pipeline is not found`() {
        val url = "http://jenkins.io/dd"
        val username = "name"
        val credential = "credential"
        val type = PipelineType.JENKINS
        Mockito.doThrow(ApplicationException(HttpStatus.NOT_FOUND, "the url is not found"))
            .`when`(jenkinsPipelineService).verifyPipelineConfiguration(url, username, credential)
        Assertions.assertThatThrownBy {
            dashboardApplicationService.verifyPipeline(PipelineVerificationRequest(url, username, credential, type))
        }.hasMessage("the url is not found")
    }

    @Test
    internal fun `should return pipeline stages sorted by Name when dashboard id is exist`() {
        val dashboardId = "1"
        `when`(dashboardRepository.getDashBoardDetailById(dashboardId)).thenReturn(Dashboard("1"))
        `when`(dashboardRepository.getPipelinesByDashboardId(dashboardId)).thenReturn(
            listOf(Pipeline("1", "km"), Pipeline("2", "Km"), Pipeline("3", "am"))
        )
        val stages = listOf("deploy to prod")
        `when`(jenkinsPipelineService.getStagesSortedByName(anyString())).thenReturn(stages)

        val actualPipelineStages = dashboardApplicationService.getPipelineStages(dashboardId)

        val expectedPipelineStages = listOf(
            PipelineStagesResponse("3", "am", stages),
            PipelineStagesResponse("2","Km", stages),
            PipelineStagesResponse("1","km", stages)
        )
        assertEquals(expectedPipelineStages, actualPipelineStages)
    }

    @Test
    internal fun `should throw dashboard not found exception when dashboard id is not exist`() {
        val dashboardId = "fake-dashboard-id"
        `when`(dashboardRepository.getDashBoardDetailById(dashboardId)).thenReturn(null)

        val exception = assertThrows<DashboardNotFoundException> {
            dashboardApplicationService.getPipelineStages(dashboardId)
        }

        assertEquals("Dashboard [id=fake-dashboard-id] is not existing", exception.message!!)
    }
}