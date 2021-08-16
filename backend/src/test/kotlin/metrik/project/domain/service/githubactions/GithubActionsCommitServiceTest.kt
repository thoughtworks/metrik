package metrik.project.domain.service.githubactions

import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.SpyK
import io.mockk.junit5.MockKExtension
import metrik.configuration.RestTemplateConfiguration
import metrik.project.commit
import metrik.project.credential
import metrik.project.githubActionsPipeline
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.MediaType
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers
import org.springframework.test.web.client.response.MockRestResponseCreators
import org.springframework.web.client.RestTemplate
import java.time.ZonedDateTime

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
@ExtendWith(MockKExtension::class)
internal class GithubActionsCommitServiceTest {

    @SpyK
    private var restTemplate: RestTemplate = RestTemplateConfiguration().restTemplate()

    @InjectMockKs
    private lateinit var githubActionsCommitService: GithubActionsCommitService

    private lateinit var mockServer: MockRestServiceServer

    @BeforeEach
    fun setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate)
    }

    @Test
    fun `should retrieve commits between builds successfully`() {
        mockServer.expect(MockRestRequestMatchers.requestTo("$commitUrl?since=$sinceTimeStamp&until=$untilTimeStamp&per_page=100&page=1"))
            .andExpect { MockRestRequestMatchers.header("Authorization", credential) }
            .andRespond(
                MockRestResponseCreators.withSuccess(
                    javaClass.getResource(
                        "/pipeline/githubactions/commits/commit1.json"
                    ).readText(),
                    MediaType.APPLICATION_JSON
                )
            )

        mockServer.expect(MockRestRequestMatchers.requestTo("$commitUrl?since=$sinceTimeStamp&until=$untilTimeStamp&per_page=100&page=2"))
            .andExpect { MockRestRequestMatchers.header("Authorization", credential) }
            .andRespond(
                MockRestResponseCreators.withSuccess(
                    javaClass.getResource(
                        "/pipeline/githubactions/commits/empty-commit.json"
                    ).readText(),
                    MediaType.APPLICATION_JSON
                )
            )

        val commits = githubActionsCommitService.getCommitsBetweenBuilds(
            sinceTimeStampZonedFormat,
            untilTimeStampZonedFormat,
            pipeline = githubActionsPipeline
        )

        Assertions.assertEquals(listOf(commit), commits)
    }

    private companion object {
        const val commitUrl = "http://localhost:80/test_project/test_repo/commits"
        const val sinceTimeStamp = "2021-08-10T01:46:31Z"
        const val untilTimeStamp = "2021-08-11T01:46:31Z"
        val sinceTimeStampZonedFormat: ZonedDateTime = ZonedDateTime.parse(sinceTimeStamp)!!
        val untilTimeStampZonedFormat: ZonedDateTime = ZonedDateTime.parse(untilTimeStamp)!!
    }
}
