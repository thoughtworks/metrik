package metrik.project.domain.service.githubactions

import metrik.exception.ApplicationException
import metrik.project.domain.model.Pipeline
import metrik.project.domain.repository.BuildRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
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
@Import(GithubActionsPipelineService::class, RestTemplate::class)
@RestClientTest
internal class GithubActionsPipelineServiceTest {

    @Autowired
    private lateinit var restTemplate: RestTemplate

    @Autowired
    private lateinit var githubActionsPipelineService: GithubActionsPipelineService

    @MockBean
    private lateinit var buildRepository: BuildRepository

    private lateinit var mockServer: MockRestServiceServer

    @BeforeEach
    fun setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate)
    }

    @Test
    fun `should throw exception when verify pipeline given response is 500`() {
        mockServer.expect(MockRestRequestMatchers.requestTo("${baseUrl}/actions/runs?per_page=1"))
            .andRespond(
                MockRestResponseCreators.withServerError()
            )

        Assertions.assertThrows(ApplicationException::class.java) {
            githubActionsPipelineService.verifyPipelineConfiguration(
                Pipeline(credential = credential, url = userInputURL)
            )
        }
    }

    @Test
    fun `should throw exception when verify pipeline given response is 400`() {

        mockServer.expect(MockRestRequestMatchers.requestTo("${baseUrl}/actions/runs?per_page=1"))
            .andRespond(
                MockRestResponseCreators.withBadRequest()
            )

        Assertions.assertThrows(ApplicationException::class.java) {
            githubActionsPipelineService.verifyPipelineConfiguration(
                Pipeline(credential = credential, url = userInputURL)
            )
        }
    }

    private companion object{
        private const val baseUrl = "http://localhost:80"
        private const val credential = "fake-credential"
        private const val userInputURL = "http://localhost:80/test_project/test_repo"
    }

}