package fourkeymetrics.resource

import fourkeymetrics.exception.ApplicationException
import fourkeymetrics.service.ConfigurationService
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@WebMvcTest(controllers = [ConfigurationResource::class])
class ConfigurationResourceTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var configurationService: ConfigurationService



    @Test
    internal fun `should return Ok when jenkins domain is valid `(){
        val url = "http://jenkins.io/"
        val username = "user"
        val credential = "credential"
        val type = "JENKINS"
        Mockito.doNothing().`when`(configurationService).verifyPipeline(url,username, credential,type)
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/pipeline/verify")
            .param("url", url)
            .param("type", type)
            .param("username", username)
            .param("credential", credential))
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    internal fun `should return 404 when jenkins domain is not valid `(){
        val url = "http://jenkins.io/notfound"
        val username = "user"
        val credential = "credential"
        val type = "JENKINS"
        Mockito.doThrow(ApplicationException(HttpStatus.NOT_FOUND,"")).`when`(configurationService).verifyPipeline(url,username, credential,type)
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/pipeline/verify")
                .param("url", url)
                .param("type", type)
                .param("username", username)
                .param("credential", credential))
            .andExpect(MockMvcResultMatchers.status().is4xxClientError)
    }
}