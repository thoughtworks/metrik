package fourkeymetrics.dashboard.controller

import com.fasterxml.jackson.databind.ObjectMapper
import fourkeymetrics.dashboard.controller.vo.PipelineStagesResponse
import fourkeymetrics.dashboard.model.Dashboard
import fourkeymetrics.dashboard.repository.DashboardRepository
import fourkeymetrics.dashboard.service.jenkins.JenkinsPipelineService
import fourkeymetrics.exception.ApplicationException
import fourkeymetrics.exception.DashboardNotFoundException
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
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
    private lateinit var jenkinsPipelineFacade: JenkinsPipelineService

    @MockBean
    private lateinit var dashboardRepository: DashboardRepository

    @Test
    internal fun `should not throw exception when type is jenkins`(){
        val url = "http://jenkins.io"
        val username = "name"
        val credential = "credential"
        val type = "JENKINS"
        Mockito.doNothing().`when`(jenkinsPipelineFacade).verifyPipelineConfiguration(url, username, credential)
        Assertions.assertThatCode {
            dashboardApplicationService.verifyPipeline(
                url,
                username,
                credential,
                type
            )
        }.doesNotThrowAnyException()
    }

    @Test
    internal fun `should  throw exception when type is not jenkins`() {
        val url = "http://jenkins.io"
        val username = "name"
        val credential = "credential"
        val type = "Bamboo"
        Mockito.doNothing().`when`(jenkinsPipelineFacade).verifyPipelineConfiguration(url, username, credential)
        Assertions.assertThatThrownBy {
            dashboardApplicationService.verifyPipeline(
                url,
                username,
                credential,
                type
            )
        }.hasMessage("Pipeline type not support")
    }

    @Test
    internal fun `should  throw exception when verify pipeline is not found`() {
        val url = "http://jenkins.io/dd"
        val username = "name"
        val credential = "credential"
        val type = "JENKINS"
        Mockito.doThrow(ApplicationException(HttpStatus.NOT_FOUND,"the url is not found")).`when`(jenkinsPipelineFacade).verifyPipelineConfiguration(url, username, credential)
        Assertions.assertThatThrownBy {
            dashboardApplicationService.verifyPipeline(
                url,
                username,
                credential,
                type
            )
        }.hasMessage("the url is not found")
    }

    @Test
    internal fun `should return pipeline stages when dashboard id is exist`() {
        val dashboardId = "1"
        `when`(dashboardRepository.getDashBoardDetailById(dashboardId)).thenReturn(Dashboard("1"))

        val expectedPipelineStages = listOf(
            PipelineStagesResponse("4km", listOf("4km-DEV", "4km-QA", "4km-UAT")),
            PipelineStagesResponse("5km", listOf("5km-DEV", "5km-QA", "5km-UAT"))
        )
        `when`(jenkinsPipelineFacade.getPipelineStages(dashboardId)).thenReturn(expectedPipelineStages)

        val actualPipelineStages = dashboardApplicationService.getPipelineStages(dashboardId)

        assertEquals(expectedPipelineStages, actualPipelineStages)
    }

    @Test
    internal fun `should throw dashboard not found exception when dashboard id is not exist`() {
        val dashboardId = "dashboard id is not exist"
        `when`(dashboardRepository.getDashBoardDetailById(dashboardId)).thenReturn(null)

        val exception = assertThrows<DashboardNotFoundException> {
            dashboardApplicationService.getPipelineStages(dashboardId)
        }
        assertTrue(exception.message!!.contains("Dashboard Not Found"))
    }
}