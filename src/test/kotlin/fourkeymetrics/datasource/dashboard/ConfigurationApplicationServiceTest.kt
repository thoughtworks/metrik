package fourkeymetrics.datasource.dashboard

import com.fasterxml.jackson.databind.ObjectMapper
import fourkeymetrics.datasource.dashboard.controller.ConfigurationApplicationService
import fourkeymetrics.datasource.dashboard.repository.DashboardRepository
import fourkeymetrics.exception.ApplicationException
import fourkeymetrics.datasource.dashboard.facade.jenkins.JenkinsFacade
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
@Import(ConfigurationApplicationService::class, ObjectMapper::class)
class ConfigurationApplicationServiceTest {
    @Autowired
    private lateinit var configurationApplicationService: ConfigurationApplicationService

    @MockBean
    private lateinit var jenkinsFacade: JenkinsFacade

    @MockBean
    private lateinit var dashboardRepository: DashboardRepository

    @Test
    internal fun `should not throw exception when type is jenkins`(){
        val url = "http://jenkins.io"
        val username = "name"
        val credential = "credential"
        val type = "JENKINS"
        Mockito.doNothing().`when`(jenkinsFacade).verifyPipelineConfiguration(url, username, credential)
        Assertions.assertThatCode {
            configurationApplicationService.verifyPipeline(
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
        Mockito.doNothing().`when`(jenkinsFacade).verifyPipelineConfiguration(url, username, credential)
        Assertions.assertThatThrownBy {
            configurationApplicationService.verifyPipeline(
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
        Mockito.doThrow(ApplicationException(HttpStatus.NOT_FOUND,"the url is not found")).`when`(jenkinsFacade).verifyPipelineConfiguration(url, username, credential)
        Assertions.assertThatThrownBy {
            configurationApplicationService.verifyPipeline(
                url,
                username,
                credential,
                type
            )
        }.hasMessage("the url is not found")
    }

}