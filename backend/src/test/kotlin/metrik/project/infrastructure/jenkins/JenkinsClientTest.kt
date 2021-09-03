package metrik.project.infrastructure.jenkins

import feign.FeignException
import feign.Request
import feign.Response
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import metrik.project.domain.service.jenkins.dto.*
import metrik.project.infrastructure.jenkins.feign.JenkinsFeignClient
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.net.URI

@ExtendWith(MockKExtension::class)
class JenkinsClientTest {
    @InjectMockKs
    private lateinit var jenkinsClient: JenkinsClient

    @MockK(relaxed = true)
    private lateinit var jenkinsFeignClient: JenkinsFeignClient

    private val dummyFeignRequest = Request.create(Request.HttpMethod.POST, "url", mapOf(), null, null, null)

    @Test
    fun `should return build details when retrieve build details from jenkins`() {
        every {
            jenkinsFeignClient.retrieveBuildDetailsFromJenkins(baseUrl, auth, buildSummaryNumber)
        } returns BuildDetailsDTO(
            listOf(stage, anotherStage)
        )
        val buildDetails = jenkinsClient.retrieveBuildDetailsFromJenkins(baseUrl, auth, buildSummaryNumber)
        assertEquals(
            buildDetails,
            BuildDetailsDTO(
                listOf(stage, anotherStage)
            )
        )
    }

    @Test
    fun `should return build summaries when retrieve build summaries from jenkins`() {
        every {
            jenkinsFeignClient.retrieveBuildSummariesFromJenkins(baseUrl, auth)
        } returns BuildSummaryCollectionDTO(
            listOf(buildSummary, buildSummary)
        )
        val buildSummaries = jenkinsClient.retrieveBuildSummariesFromJenkins(baseUrl, auth)
        assertEquals(
            buildSummaries,
            BuildSummaryCollectionDTO(
                listOf(buildSummary, buildSummary)
            )
        )

    }

    @Test
    fun `should throw exception when the jenkins return 404 not found status code for verification`() {
        every {
            jenkinsFeignClient.verifyJenkinsUrl(baseUrl, auth)
        } throws FeignException.errorStatus(
            "GET",
            buildFeignResponse(404)
        )
        assertThrows(
            FeignException.NotFound::class.java
        ) {
            jenkinsClient.verifyJenkinsUrl(baseUrl, auth)
        }
    }

    @Test
    fun `should return null when the github return 404 not found status code for non verification`() {
        every {
            jenkinsFeignClient.retrieveBuildSummariesFromJenkins(baseUrl, auth)
        } throws FeignException.errorStatus(
            "GET",
            buildFeignResponse(404)
        )
        every {
            jenkinsFeignClient.retrieveBuildDetailsFromJenkins(baseUrl, auth, buildSummaryNumber)
        } throws FeignException.errorStatus(
            "GET",
            buildFeignResponse(404)
        )
        assertNull(jenkinsClient.retrieveBuildSummariesFromJenkins(baseUrl, auth))
        assertNull(jenkinsClient.retrieveBuildDetailsFromJenkins(baseUrl, auth, buildSummaryNumber))
    }

    @Test
    fun `should throw corresponding exception when the jenkins return status code other than 404`() {
        every {
            jenkinsFeignClient.retrieveBuildSummariesFromJenkins(baseUrl, auth)
        } throws FeignException.errorStatus(
            "GET",
            buildFeignResponse(400)
        )
        every {
            jenkinsFeignClient.retrieveBuildDetailsFromJenkins(baseUrl, auth, buildSummaryNumber)
        } throws FeignException.errorStatus(
            "GET",
            buildFeignResponse(500)
        )
        every {
            jenkinsFeignClient.retrieveBuildDetailsFromJenkins(baseUrl, wrongAuth, buildSummaryNumber)
        } throws FeignException.errorStatus(
            "GET",
            buildFeignResponse(403)
        )
        assertThrows(
            FeignException.BadRequest::class.java
        ) {
            jenkinsClient.retrieveBuildSummariesFromJenkins(baseUrl, auth)
        }
        assertThrows(
            FeignException.InternalServerError::class.java
        ) {
            jenkinsClient.retrieveBuildDetailsFromJenkins(baseUrl, auth, buildSummaryNumber)
        }
        assertThrows(
            FeignException.Forbidden::class.java
        ) {
            jenkinsClient.retrieveBuildDetailsFromJenkins(baseUrl, wrongAuth, buildSummaryNumber)
        }

    }


    private fun buildFeignResponse(statusCode: Int) =
        Response.builder().status(statusCode).request(dummyFeignRequest).build()

    private companion object {
        const val buildSummaryNumber = 2
        const val auth = "Bearer"
        const val wrongAuth = "wrong token"
        val stage = StageDTO("test1", "SUCCESS", 1630485560707, 53, 0)
        val anotherStage = StageDTO("test2", "SUCCESS", 1630569880520, 47, 10)
        val commit = CommitDTO("TEST", 1630485560707, "today", "test a test")
        val changeSet = ChangeSetDTO(listOf(commit, commit))
        val buildSummary =
            BuildSummaryDTO(1, "SUCCESS", 42, 1630485560707, "http://this.is.a.test.com", listOf(changeSet, changeSet))
        val baseUrl = URI("https://this-is-a-test.com")
    }

}