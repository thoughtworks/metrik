package fourkeymetrics.pipeline

import fourkeymetrics.model.Build
import fourkeymetrics.model.PipelineConfiguration
import fourkeymetrics.model.Stage
import fourkeymetrics.repository.BuildRepository
import fourkeymetrics.repository.PipelineRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@Import(Jenkins::class, PipelineRepository::class, BuildRepository::class)
internal class JenkinsTest {
    @Autowired
    private lateinit var jenkins: Jenkins

    @MockBean
    private lateinit var pipelineRepository: PipelineRepository

    @MockBean
    private lateinit var buildRepository: BuildRepository

    @Test
    internal fun `should return true given pipeline exits in database`() {
        val dashboardId = "dashboard ID"
        val pipelineId = "fake pipeline"

        `when`(pipelineRepository.getPipeline(dashboardId, pipelineId)).thenReturn(PipelineConfiguration())

        assertThat(jenkins.hasPipeline(dashboardId, pipelineId)).isTrue
    }

    @Test
    internal fun `should return true given stage exits in last build`() {
        val pipelineId = "fake pipeline"
        val targetStage = "deploy to dev"
        val lastBuild = Build(stages = listOf(Stage(name = targetStage), Stage(name = "build")))

        `when`(buildRepository.getLastBuild(pipelineId)).thenReturn(lastBuild)

        assertThat(jenkins.hasStageInLastBuild(pipelineId, targetStage)).isTrue
    }

    @Test
    internal fun `should return false given stage not exits in last build`() {
        val pipelineId = "fake pipeline"
        val targetStage = "deploy to dev"
        val lastBuild = Build(stages = listOf(Stage(name = "clone"), Stage(name = "build")))

        `when`(buildRepository.getLastBuild(pipelineId)).thenReturn(lastBuild)

        assertThat(jenkins.hasStageInLastBuild(pipelineId, targetStage)).isFalse
    }

    @Test
    internal fun `should return false given no builds exist in pipeline`() {
        val pipelineId = "fake pipeline"
        val targetStage = "deploy to dev"

        `when`(buildRepository.getLastBuild(pipelineId)).thenReturn(null)

        assertThat(jenkins.hasStageInLastBuild(pipelineId, targetStage)).isFalse
    }
}