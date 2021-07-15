package metrik.project.domain.service.factory

import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import metrik.project.domain.model.PipelineType
import metrik.project.domain.service.bamboo.BambooPipelineService
import metrik.project.domain.service.jenkins.JenkinsPipelineService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class PipelineServiceFactoryTest {
    @MockK(relaxed = true)
    private lateinit var jenkinsPipelineService: JenkinsPipelineService

    @MockK(relaxed = true)
    private lateinit var bambooPipelineService: BambooPipelineService

    @MockK(relaxed = true)
    private lateinit var noopPipelineService: NoopPipelineService

    @InjectMockKs(overrideValues = true)
    private lateinit var pipelineServiceFactory: PipelineServiceFactory

    @Test
    fun `should get corresponding service instance given a valid PipelineType`() {
        verify(exactly = 0) { jenkinsPipelineService.getStagesSortedByName("id") }
        verify(exactly = 0) { bambooPipelineService.getStagesSortedByName("id") }
        verify(exactly = 0) { noopPipelineService.getStagesSortedByName("id") }

        pipelineServiceFactory.getService(PipelineType.JENKINS).getStagesSortedByName("id")
        verify(exactly = 1) { jenkinsPipelineService.getStagesSortedByName("id") }

        pipelineServiceFactory.getService(PipelineType.BAMBOO).getStagesSortedByName("id")
        verify(exactly = 1) { bambooPipelineService.getStagesSortedByName("id") }

        pipelineServiceFactory.getService(PipelineType.GITHUB_ACTIONS).getStagesSortedByName("id")
        verify(exactly = 1) { noopPipelineService.getStagesSortedByName("id") }
    }
}