package fourkeymetrics.dashboard.controller

import com.fasterxml.jackson.databind.ObjectMapper
import fourkeymetrics.dashboard.controller.applicationservice.DashboardApplicationService
import fourkeymetrics.dashboard.repository.DashboardRepository1
import fourkeymetrics.dashboard.service.jenkins.JenkinsPipelineService
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@Import(DashboardApplicationService::class, ObjectMapper::class)
class DashboardApplicationServiceTest {
    @Autowired
    private lateinit var dashboardApplicationService: DashboardApplicationService

    @MockBean
    private lateinit var jenkinsPipelineFacade: JenkinsPipelineService

    @MockBean
    private lateinit var dashboardRepository: DashboardRepository1

//    @Test
//    internal fun `should not throw exception when type is jenkins`() {
//        val url = "http://jenkins.io"
//        val username = "name"
//        val credential = "credential"
//        val type = PipelineType.JENKINS
//        Mockito.doNothing().`when`(jenkinsPipelineFacade).verifyPipelineConfiguration(url, username, credential)
//        Assertions.assertThatCode {
//            dashboardApplicationService.verifyPipeline(PipelineVerificationRequest(url, username, credential, type))
//        }.doesNotThrowAnyException()
//    }

//    @Test
//    internal fun `should  throw exception when type is not jenkins`() {
//        val url = "http://jenkins.io"
//        val username = "name"
//        val credential = "credential"
//        val type = PipelineType.BAMBOO
//        Mockito.doNothing().`when`(jenkinsPipelineFacade).verifyPipelineConfiguration(url, username, credential)
//        Assertions.assertThatThrownBy {
//            dashboardApplicationService.verifyPipeline(PipelineVerificationRequest(url, username, credential, type))
//        }.hasMessage("Pipeline type not support")
//    }

//    @Test
//    internal fun `should  throw exception when verify pipeline is not found`() {
//        val url = "http://jenkins.io/dd"
//        val username = "name"
//        val credential = "credential"
//        val type = PipelineType.JENKINS
//        Mockito.doThrow(ApplicationException(HttpStatus.NOT_FOUND, "the url is not found"))
//            .`when`(jenkinsPipelineFacade).verifyPipelineConfiguration(url, username, credential)
//        Assertions.assertThatThrownBy {
//            dashboardApplicationService.verifyPipeline(PipelineVerificationRequest(url, username, credential, type))
//        }.hasMessage("the url is not found")
//    }

//    @Test
//    internal fun `should return pipeline stages when dashboard id is exist`() {
//        val dashboardId = "1"
//        `when`(dashboardRepository.getDashBoardDetailById(dashboardId)).thenReturn(Dashboard("1"))
//
//        val expectedPipelineStages = listOf(
//            PipelineStagesResponse("4km", listOf("4km-DEV", "4km-QA", "4km-UAT")),
//            PipelineStagesResponse("5km", listOf("5km-DEV", "5km-QA", "5km-UAT"))
//        )
//        `when`(jenkinsPipelineFacade.getPipelineStages(dashboardId)).thenReturn(expectedPipelineStages)
//
//        val actualPipelineStages = dashboardApplicationService.getPipelinesStages(dashboardId)
//
//        assertEquals(expectedPipelineStages, actualPipelineStages)
//    }

//    @Test
//    internal fun `should throw dashboard not found exception when dashboard id is not exist`() {
//        val dashboardId = "fake-dashboard-id"
//        `when`(dashboardRepository.getDashBoardDetailById(dashboardId)).thenReturn(null)
//
//        val exception = assertThrows<DashboardNotFoundException> {
//            dashboardApplicationService.getPipelinesStages(dashboardId)
//        }
//
//        assertEquals("Dashboard [id=fake-dashboard-id] is not existing", exception.message!!)
//    }
}