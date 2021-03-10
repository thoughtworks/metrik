package fourkeymetrics.project.service.bamboo

import fourkeymetrics.exception.ApplicationException
import fourkeymetrics.project.model.Pipeline
import fourkeymetrics.project.repository.BuildRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers
import org.springframework.test.web.client.response.MockRestResponseCreators
import org.springframework.web.client.RestTemplate


@ExtendWith(SpringExtension::class)
@Import(BambooPipelineService::class, RestTemplate::class)
@RestClientTest
internal class BambooPipelineServiceTest {
    @Autowired
    private lateinit var restTemplate: RestTemplate

    @Autowired
    private lateinit var bambooPipelineService: BambooPipelineService

    @MockBean
    private lateinit var buildRepository: BuildRepository

    @Test
    internal fun `should throw exception when verify pipeline given response is 500`() {
        val credential = "fake-credential"
        val url = "http://localhost/deploy/viewDeploymentProjectEnvironments.action?id=4751362"
        val baseUrl = "http://localhost:80"
        val mockServer = MockRestServiceServer.bindTo(restTemplate).ignoreExpectOrder(true).build()

        mockServer.expect(MockRestRequestMatchers.requestTo("${baseUrl}/rest/api/latest/project/"))
            .andRespond(
                MockRestResponseCreators.withServerError()
            )

        Assertions.assertThrows(ApplicationException::class.java) {
            bambooPipelineService.verifyPipelineConfiguration(Pipeline(credential = credential, url = url))
        }
    }

    @Test
    internal fun `should throw exception when verify pipeline given response is 400`() {
        val credential = "fake-credential"
        val url = "http://localhost/deploy/viewDeploymentProjectEnvironments.action?id=4751362"
        val baseUrl = "http://localhost:80"
        val mockServer = MockRestServiceServer.bindTo(restTemplate).ignoreExpectOrder(true).build()

        mockServer.expect(MockRestRequestMatchers.requestTo("${baseUrl}/rest/api/latest/project/"))
            .andRespond(
                MockRestResponseCreators.withBadRequest()
            )

        Assertions.assertThrows(ApplicationException::class.java) {
            bambooPipelineService.verifyPipelineConfiguration(Pipeline(credential = credential, url = url))
        }
    }
}