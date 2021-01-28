package fourkeymetrics.dashboard

import fourkeymetrics.dashboard.controller.DashboardApplicationService
import fourkeymetrics.dashboard.controller.DashboardController
import fourkeymetrics.exception.ApplicationException
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@WebMvcTest(controllers = [DashboardController::class])
class DashboardControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var dashboardApplicationService: DashboardApplicationService

    @Test
    internal fun `should return Ok when jenkins domain is valid `() {
        val url = "http://jenkins.io/"
        val username = "user"
        val credential = "credential"
        val type = "JENKINS"
        Mockito.doNothing().`when`(dashboardApplicationService).verifyPipeline(url, username, credential, type)
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/pipeline/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                        {
                            "url":"$url",
                            "credential":"$credential",
                            "username":"$username",
                            "type":"$type"
                       }
                    """.trimIndent()
                )
        ).andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    internal fun `should return 404 when jenkins domain is not valid `() {
        val url = "http://jenkins.io/notfound"
        val username = "user"
        val credential = "credential"
        val type = "JENKINS"

        Mockito.doThrow(ApplicationException(HttpStatus.NOT_FOUND, ""))
            .`when`(dashboardApplicationService).verifyPipeline(url, username, credential, type)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/pipeline/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                        {
                            "url":"$url",
                            "credential":"$credential",
                            "username":"$username",
                            "type":"$type"
                       }
                    """.trimIndent()
                )
        ).andExpect(MockMvcResultMatchers.status().is4xxClientError)
    }
}