package metrik.metrics.domain.calculator

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import metrik.project.domain.model.Build
import metrik.project.domain.repository.BuildRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
internal class DeploymentFrequencyServiceTest {
    @Mock
    private lateinit var buildRepository: BuildRepository

    @InjectMocks
    private lateinit var deploymentFrequencyService: DeploymentFrequencyService

    /**
     * test file: builds-for-jenkins-1.json
     * build 1 : deploy to prod, SUCCESS, start at 2021-01-01, end at 2021-01-01
     * build 2 : deploy to prod, SUCCESS, start at 2021-01-15, end at 2021-01-15
     * build 3 : deploy to prod, SUCCESS, start at 2021-01-30, end at 2021-02-01
     */
    @Test
    internal fun `should get deployment count within time range given 1 valid build inside when calculate deployment count`() {
        val pipelineId = "test pipeline - master"
        val targetStage = "deploy to prod"
        val startTimestamp = 1610236800000L  // 2021-01-10
        val endTimestamp = 1611100800000L   // 2021-01-30

        val mockBuildList: List<Build> = ObjectMapper().readValue(this.javaClass.getResource("/service/builds-for-df-1.json").readText())

        `when`(buildRepository.getAllBuilds(pipelineId)).thenReturn(mockBuildList)

        assertThat(deploymentFrequencyService.getDeploymentCount(pipelineId, targetStage, startTimestamp, endTimestamp)).isEqualTo(1)
    }

    /**
     * test file: builds-for-build-repo-3.json
     * build 1 : 2021-01-01, [build: SUCCESS, deploy to prod: SUCCESS]
     * build 2 : 2021-01-15, [build: SUCCESS, deploy to prod: FAILED]
     * build 3 : 2021-01-30, [build: FAILED, deploy to prod: SUCCESS]
     */
    @Test
    internal fun `should get deployment count with success target stage status when calculate deployment count`() {
        val pipelineId = "test pipeline - master"
        val targetStage = "deploy to prod"
        val startTimestamp = 1609286400000L  // 2020-12-30
        val endTimestamp = 1612137600000L   // 2021-02-01

        val mockBuildList: List<Build> = ObjectMapper().readValue(this.javaClass.getResource("/service/builds-for-df-3.json").readText())

        `when`(buildRepository.getAllBuilds(pipelineId)).thenReturn(mockBuildList)

        assertThat(deploymentFrequencyService.getDeploymentCount(pipelineId, targetStage, startTimestamp, endTimestamp)).isEqualTo(2)
    }

    /**
     * test file: builds-for-build-repo-3.json
     * build 1 : 2021-01-01, [build: SUCCESS, deploy to prod: SUCCESS, start time: null]
     */
    @Test
    internal fun `should get deployment count as 0 when all stages don't have start time`() {
        val pipelineId = "test pipeline - master"
        val targetStage = "deploy to prod"
        val startTimestamp = 1609286400000L  // 2020-12-30
        val endTimestamp = 1612137600000L   // 2021-02-01

        val mockBuildList: List<Build> = ObjectMapper().readValue(this.javaClass.getResource("/service/builds-for-df-4.json").readText())

        `when`(buildRepository.getAllBuilds(pipelineId)).thenReturn(mockBuildList)

        assertThat(deploymentFrequencyService.getDeploymentCount(pipelineId, targetStage, startTimestamp, endTimestamp)).isEqualTo(0)
    }
}