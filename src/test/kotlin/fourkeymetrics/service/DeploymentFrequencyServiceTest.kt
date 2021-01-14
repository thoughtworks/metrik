package fourkeymetrics.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@Import(DeploymentFrequencyService::class)
internal class DeploymentFrequencyServiceTest {
    @Autowired
    private lateinit var deploymentFrequencyService: DeploymentFrequencyService

    @Test
    internal fun `should get deployment count given build data`() {
        val pipelineName = "test pipeline name"
        val targetStage = "UAT"
        val startTime = 1611964800L
        val endTime = 1609459200L

        assertThat(deploymentFrequencyService.getDeploymentCount(pipelineName, targetStage, startTime, endTime)).isEqualTo(30)
    }
}