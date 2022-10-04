package metrik.project.domain.service.factory

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import metrik.project.domain.model.PipelineType
import metrik.project.domain.service.bamboo.BambooDeploymentPipelineService
import metrik.project.domain.service.bamboo.BambooPipelineService
import metrik.project.domain.service.buddy.BuddyPipelineService
import metrik.project.domain.service.githubactions.GithubActionsPipelineService
import metrik.project.domain.service.jenkins.JenkinsPipelineService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class GithubPipelineServiceFactoryTest {
    @MockK(relaxed = true)
    private lateinit var jenkinsPipelineService: JenkinsPipelineService

    @MockK(relaxed = true)
    private lateinit var bambooPipelineService: BambooPipelineService

    @MockK
    private lateinit var githubActionsPipelineService: GithubActionsPipelineService

    @MockK
    private lateinit var buddyPipelineService: BuddyPipelineService

    @MockK
    private lateinit var bambooDeploymentPipelineService: BambooDeploymentPipelineService

    @MockK
    private lateinit var noopPipelineService: NoopPipelineService

    @InjectMockKs(overrideValues = true)
    private lateinit var pipelineServiceFactory: PipelineServiceFactory

    @BeforeEach
    fun setUp() {
        every { jenkinsPipelineService.getStagesSortedByName("id") } returns emptyList()
        every { bambooPipelineService.getStagesSortedByName("id") } returns emptyList()
        every { bambooDeploymentPipelineService.getStagesSortedByName("id") } returns emptyList()
        every { githubActionsPipelineService.getStagesSortedByName("id") } returns emptyList()
        every { buddyPipelineService.getStagesSortedByName("id") } returns emptyList()
        every { noopPipelineService.getStagesSortedByName("id") } returns emptyList()
    }

    @Test
    fun `should get corresponding service instance given a valid PipelineType`() {
        verify(exactly = 0) { jenkinsPipelineService.getStagesSortedByName("id") }
        verify(exactly = 0) { bambooPipelineService.getStagesSortedByName("id") }
        verify(exactly = 0) { bambooDeploymentPipelineService.getStagesSortedByName("id") }
        verify(exactly = 0) { githubActionsPipelineService.getStagesSortedByName("id") }
        verify(exactly = 0) { buddyPipelineService.getStagesSortedByName("id") }
        verify(exactly = 0) { noopPipelineService.getStagesSortedByName("id") }

        pipelineServiceFactory.getService(PipelineType.JENKINS).getStagesSortedByName("id")
        verify(exactly = 1) { jenkinsPipelineService.getStagesSortedByName("id") }

        pipelineServiceFactory.getService(PipelineType.BAMBOO).getStagesSortedByName("id")
        verify(exactly = 1) { bambooPipelineService.getStagesSortedByName("id") }
        pipelineServiceFactory.getService(PipelineType.BAMBOO_DEPLOYMENT).getStagesSortedByName("id")
        verify(exactly = 1) { bambooDeploymentPipelineService.getStagesSortedByName("id") }

        pipelineServiceFactory.getService(PipelineType.GITHUB_ACTIONS).getStagesSortedByName("id")
        verify(exactly = 1) { githubActionsPipelineService.getStagesSortedByName("id") }

        pipelineServiceFactory.getService(PipelineType.BUDDY).getStagesSortedByName("id")
        verify(exactly = 1) { buddyPipelineService.getStagesSortedByName("id") }

        pipelineServiceFactory.getService(PipelineType.NOT_SUPPORTED).getStagesSortedByName("id")
        verify(exactly = 1) { noopPipelineService.getStagesSortedByName("id") }
    }
}
