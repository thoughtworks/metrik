package metrik.project.domain.service.factory

import metrik.project.domain.model.PipelineType
import metrik.project.domain.service.bamboo.BambooPipelineService
import metrik.project.domain.service.jenkins.JenkinsPipelineService
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@ExtendWith(MockitoExtension::class)
internal class PipelineServiceFactoryTest {
    @Autowired
    private lateinit var pipelineServiceFactory: PipelineServiceFactory

    @Test
    internal fun `should get corresponding service instance given a valid PipelineType`() {
        val jService = pipelineServiceFactory.getService(PipelineType.JENKINS)
        val bService = pipelineServiceFactory.getService(PipelineType.BAMBOO)
        val gService = pipelineServiceFactory.getService(PipelineType.GITHUB_ACTIONS)

        assertTrue(jService is JenkinsPipelineService)
        assertTrue(bService is BambooPipelineService)
        assertTrue(gService is NoopPipelineService)
    }
}