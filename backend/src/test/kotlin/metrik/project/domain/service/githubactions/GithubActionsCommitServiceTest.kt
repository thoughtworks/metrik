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
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers
import org.springframework.test.web.client.response.MockRestResponseCreators
import org.springframework.web.client.HttpServerErrorException.InternalServerError
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

    @Test
    fun `should get all commits given since timestamp is not provided`() {
        mockServer.expect(MockRestRequestMatchers.requestTo("$commitUrl?until=$untilTimeStamp&per_page=100&page=1"))
            .andExpect { MockRestRequestMatchers.header("Authorization", credential) }
            .andRespond(
                MockRestResponseCreators.withSuccess(
                    javaClass.getResource(
                        "/pipeline/githubactions/commits/commit1.json"
                    ).readText(),
                    MediaType.APPLICATION_JSON
                )
            )

        mockServer.expect(MockRestRequestMatchers.requestTo("$commitUrl?until=$untilTimeStamp&per_page=100&page=2"))
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
            untilTimeStamp = untilTimeStampZonedFormat,
            pipeline = githubActionsPipeline
        )

        Assertions.assertEquals(listOf(commit), commits)
    }

    @Test
    fun `should get all commits in assigned branch`() {
        mockServer.expect(MockRestRequestMatchers.requestTo("$commitUrl?until=$untilTimeStamp&per_page=100&sha=feature&page=1"))
            .andExpect { MockRestRequestMatchers.header("Authorization", credential) }
            .andRespond(
                MockRestResponseCreators.withSuccess(
                    javaClass.getResource(
                        "/pipeline/githubactions/commits/commit1.json"
                    ).readText(),
                    MediaType.APPLICATION_JSON
                )
            )

        mockServer.expect(MockRestRequestMatchers.requestTo("$commitUrl?until=$untilTimeStamp&per_page=100&sha=feature&page=2"))
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
            untilTimeStamp = untilTimeStampZonedFormat,
            branch = "feature",
            pipeline = githubActionsPipeline
        )

        Assertions.assertEquals(listOf(commit), commits)
    }

    @Test
    fun `should stop calling next page api when the current api call throw not found exception`() {
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
                MockRestResponseCreators.withStatus(HttpStatus.NOT_FOUND)
            )

        val commits = githubActionsCommitService.getCommitsBetweenBuilds(
            sinceTimeStampZonedFormat,
            untilTimeStampZonedFormat,
            pipeline = githubActionsPipeline
        )

        Assertions.assertEquals(listOf(commit), commits)
    }

    @Test
    fun `should throw exception when the server responds 500 at any time`() {
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
                MockRestResponseCreators.withServerError()
            )

        Assertions.assertThrows(InternalServerError::class.java) {
            githubActionsCommitService.getCommitsBetweenBuilds(
                sinceTimeStampZonedFormat,
                untilTimeStampZonedFormat,
                pipeline = githubActionsPipeline
            )
        }
    }

    private companion object {
        const val commitUrl = "http://localhost:80/test_project/test_repo/commits"
        const val sinceTimeStamp = "2021-08-10T01:46:31Z"
        const val untilTimeStamp = "2021-08-11T01:46:31Z"
        val sinceTimeStampZonedFormat: ZonedDateTime = ZonedDateTime.parse(sinceTimeStamp)!!
        val untilTimeStampZonedFormat: ZonedDateTime = ZonedDateTime.parse(untilTimeStamp)!!
    }
}
