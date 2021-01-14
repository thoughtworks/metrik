package fourkeymetrics.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import fourkeymetrics.model.Build
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@Import(DeploymentFrequencyService::class, ObjectMapper::class)
internal class DeploymentFrequencyServiceTest {
    @Autowired
    private lateinit var deploymentFrequencyService: DeploymentFrequencyService

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var pipelineService: PipelineService

    /**
     * test file: deployments-1.json
     * build 1 : 2021-01-01, SUCCESS
     * build 2 : 2021-01-15, SUCCESS
     * build 3 : 2021-01-30, SUCCESS
     */
    @Test
    internal fun `should get deployment count within time range given 1 valid build inside when calculate deployment count`() {
        val pipelineID = "test pipeline - master"
        val targetStage = "deploy to prod"
        val startTime = 1610236800L  // 2021-01-10
        val endTime = 1611100800L   // 2021-01-20

        val mockBuildList: List<Build> = objectMapper.readValue(this.javaClass.getResource("/service/deployments-1.json").readText())

        `when`(pipelineService.getBuildsByTimeRange(pipelineID, startTime, endTime)).thenReturn(mockBuildList)

        assertThat(deploymentFrequencyService.getDeploymentCount(pipelineID, targetStage, startTime, endTime)).isEqualTo(1)
    }

    /**
     * test file: deployments-2.json
     * build 1 : 2021-01-01, FAILED
     * build 2 : 2021-01-15, SUCCESS
     * build 3 : 2021-01-30, SUCCESS
     */
    @Test
    internal fun `should get deployment count with success build status given 2 success build when calculate deployment count`() {
        val pipelineID = "test pipeline - master"
        val targetStage = "deploy to prod"
        val startTime = 1609286400L  // 2020-12-30
        val endTime = 1612137600L   // 2021-02-01

        val mockBuildList: List<Build> = objectMapper.readValue(this.javaClass.getResource("/service/deployments-2.json").readText())

        `when`(pipelineService.getBuildsByTimeRange(pipelineID, startTime, endTime)).thenReturn(mockBuildList)

        assertThat(deploymentFrequencyService.getDeploymentCount(pipelineID, targetStage, startTime, endTime)).isEqualTo(2)
    }

    /**
     * test file: deployments-3.json
     * build 1 : 2021-01-01, [build: SUCCESS, deploy to prod: SUCCESS]
     * build 2 : 2021-01-15, [build: SUCCESS, deploy to prod: FAILED]
     * build 3 : 2021-01-30, [build: FAILED, deploy to prod: SUCCESS]
     */
    @Test
    internal fun `should get deployment count with success target stage status when calculate deployment count`() {
        val pipelineID = "test pipeline - master"
        val targetStage = "deploy to prod"
        val startTime = 1609286400L  // 2020-12-30
        val endTime = 1612137600L   // 2021-02-01

        val mockBuildList: List<Build> = objectMapper.readValue(this.javaClass.getResource("/service/deployments-3.json").readText())

        `when`(pipelineService.getBuildsByTimeRange(pipelineID, startTime, endTime)).thenReturn(mockBuildList)

        assertThat(deploymentFrequencyService.getDeploymentCount(pipelineID, targetStage, startTime, endTime)).isEqualTo(2)
    }
}