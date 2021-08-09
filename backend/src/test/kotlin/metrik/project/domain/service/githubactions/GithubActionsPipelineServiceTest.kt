package metrik.project.domain.service.githubactions

import metrik.project.domain.repository.BuildRepository
import org.junit.jupiter.api.BeforeEach

import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.client.MockRestServiceServer
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



}