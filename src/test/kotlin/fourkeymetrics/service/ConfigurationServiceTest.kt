package fourkeymetrics.service

import com.fasterxml.jackson.databind.ObjectMapper
import fourkeymetrics.exception.ApplicationException
import fourkeymetrics.pipeline.Jenkins
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@Import(ConfigurationService::class, ObjectMapper::class)
class ConfigurationServiceTest {
    @Autowired
    private lateinit var configurationService: ConfigurationService

    @MockBean
    private lateinit var jenkins: Jenkins

    @Test
    internal fun `should not throw exception when type is jenkins`(){
        val url = "http://jenkins.io"
        val username = "name"
        val token = "token"
        val type = "JENKINS"
        Mockito.doNothing().`when`(jenkins).verifyPipeline(url, username, token)
        Assertions.assertThatCode {
            configurationService.verifyPipeline(
                url,
                username,
                token,
                type
            )
        }.doesNotThrowAnyException()
    }

    @Test
    internal fun `should  throw exception when type is not jenkins`() {
        val url = "http://jenkins.io"
        val username = "name"
        val token = "token"
        val type = "Bamboo"
        Mockito.doNothing().`when`(jenkins).verifyPipeline(url, username, token)
        Assertions.assertThatThrownBy {
            configurationService.verifyPipeline(
                url,
                username,
                token,
                type
            )
        }.hasMessage("Pipeline type not support")
    }

    @Test
    internal fun `should  throw exception when verify pipeline is not found`() {
        val url = "http://jenkins.io/dd"
        val username = "name"
        val token = "token"
        val type = "JENKINS"
        Mockito.doThrow(ApplicationException(HttpStatus.NOT_FOUND,"the url is not found")).`when`(jenkins).verifyPipeline(url, username, token)
        Assertions.assertThatThrownBy {
            configurationService.verifyPipeline(
                url,
                username,
                token,
                type
            )
        }.hasMessage("the url is not found")
    }

}