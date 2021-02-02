package fourkeymetrics.dashboard.controller

import fourkeymetrics.dashboard.controller.applicationservice.DashboardApplicationService
import fourkeymetrics.dashboard.controller.vo.PipelineStagesResponse
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath

@WebMvcTest(controllers = [DashboardController::class])
class DashboardControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var dashboardApplicationService: DashboardApplicationService

//    @Test
//    internal fun `should return Ok when jenkins domain is valid `() {
//        val url = "http://jenkins.io/"
//        val username = "user"
//        val credential = "credential"
//        val type = PipelineType.JENKINS
//        Mockito.doNothing().`when`(dashboardApplicationService)
//            .verifyPipeline(PipelineVerificationRequest(url, username, credential, type))
//        mockMvc.perform(
//            MockMvcRequestBuilders.post("/api/pipeline/verify")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(
//                    """
//                        {
//                            "url":"$url",
//                            "credential":"$credential",
//                            "username":"$username",
//                            "type":"$type"
//                       }
//                    """.trimIndent()
//                )
//        ).andExpect(status().isOk)
//    }

//    @Test
//    internal fun `should return 404 when jenkins domain is not valid `() {
//        val url = "http://jenkins.io/notfound"
//        val username = "user"
//        val credential = "credential"
//        val type = PipelineType.JENKINS
//
//        Mockito.doThrow(ApplicationException(HttpStatus.NOT_FOUND, ""))
//            .`when`(dashboardApplicationService)
//            .verifyPipeline(PipelineVerificationRequest(url, username, credential, type))
//
//        mockMvc.perform(
//            MockMvcRequestBuilders.post("/api/pipeline/verify")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(
//                    """
//                        {
//                            "url":"$url",
//                            "credential":"$credential",
//                            "username":"$username",
//                            "type":"$type"
//                       }
//                    """.trimIndent()
//                )
//        ).andExpect(status().is4xxClientError)
//    }

//    @Test
//    internal fun `should return pipeline stages when call pipeline stage api`() {
//        val dashboardId = "1"
//        val expectedPipelineStages = listOf(
//            PipelineStagesResponse("4km", listOf("4km-DEV", "4km-QA", "4km-UAT")),
//            PipelineStagesResponse("5km", listOf("5km-DEV", "5km-QA", "5km-UAT"))
//        )
//        `when`(dashboardApplicationService.getPipelinesStages(dashboardId)).thenReturn(expectedPipelineStages)
//
//        mockMvc.perform(get("/api/dashboard/${dashboardId}/stage"))
//            .andExpect(jsonPath("$.length()").value(2))
//            .andExpect(jsonPath("$[0].pipelineName").value("4km"))
//            .andExpect(jsonPath("$[0].stages.length()").value(3))
//            .andExpect(jsonPath("$[0].stages[0]").value("4km-DEV"))
//            .andExpect(jsonPath("$[0].stages[1]").value("4km-QA"))
//            .andExpect(jsonPath("$[0].stages[2]").value("4km-UAT"))
//            .andExpect(jsonPath("$[1].pipelineName").value("5km"))
//    }
}