package fourkeymetrics.dashboard

import com.fasterxml.jackson.databind.ObjectMapper
import fourkeymetrics.dashboard.controller.DashboardApplicationService
import fourkeymetrics.dashboard.repository.DashboardRepository
import fourkeymetrics.exception.ApplicationException
import fourkeymetrics.dashboard.service.jenkins.JenkinsPipelinService
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
@Import(DashboardApplicationService::class, ObjectMapper::class)
class DashboardApplicationServiceTest {
    @Autowired
    private lateinit var dashboardApplicationService: DashboardApplicationService

    @MockBean
    private lateinit var jenkinsPipelinFacade: JenkinsPipelinService

    @MockBean
    private lateinit var dashboardRepository: DashboardRepository

    @Test
    internal fun `should not throw exception when type is jenkins`(){
        val url = "http://jenkins.io"
        val username = "name"
        val credential = "credential"
        val type = "JENKINS"
        Mockito.doNothing().`when`(jenkinsPipelinFacade).verifyPipelineConfiguration(url, username, credential)
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
        Mockito.doNothing().`when`(jenkinsPipelinFacade).verifyPipelineConfiguration(url, username, credential)
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
        Mockito.doThrow(ApplicationException(HttpStatus.NOT_FOUND,"the url is not found")).`when`(jenkinsPipelinFacade).verifyPipelineConfiguration(url, username, credential)
        Assertions.assertThatThrownBy {
            dashboardApplicationService.verifyPipeline(
                url,
                username,
                credential,
                type
            )
        }.hasMessage("the url is not found")
    }

}