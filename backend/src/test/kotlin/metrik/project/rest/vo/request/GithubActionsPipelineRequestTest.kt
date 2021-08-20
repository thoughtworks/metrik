package metrik.project.rest.vo.request

import io.mockk.junit5.MockKExtension
import metrik.project.buildGithubActionsPipelineRequest
import metrik.project.githubActionsPipeline
import metrik.project.pipelineID
import metrik.project.projectID
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class GithubActionsPipelineRequestTest {

    @Test
    fun `should convert githubActions request to pipeline`() {
        val githubActionsPipelineRequest = buildGithubActionsPipelineRequest()
        val pipeline = githubActionsPipelineRequest.toPipeline(projectID, pipelineID)
        assertEquals(pipeline, githubActionsPipeline)
    }

    @Test
    fun `should throw exception when convert wrong githubActions url`() {
        val githubActionsPipelineRequest = GithubActionsPipelineRequest(
            name = "pipeline",
            credential = "credential",
            url = "https://github.com"
        )
        assertThrows(
            IndexOutOfBoundsException::class.java
        ) {
            githubActionsPipelineRequest.toPipeline(projectID, pipelineID)
        }
    }
}
