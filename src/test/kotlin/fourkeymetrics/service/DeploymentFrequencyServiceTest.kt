package fourkeymetrics.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import fourkeymetrics.model.Build
import fourkeymetrics.repository.BuildRepository
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
    private lateinit var buildRepository: BuildRepository

    /**
     * test file: builds-1.json
     * build 1 : deploy to prod, SUCCESS, 2021-01-01
     * build 2 : deploy to prod, SUCCESS, 2021-01-15
     * build 3 : deploy to prod, SUCCESS, 2021-01-30
     */
    @Test
    internal fun `should get deployment count within time range given 1 valid build inside when calculate deployment count`() {
        val pipelineId = "test pipeline - master"
        val targetStage = "deploy to prod"
        val startTimestamp = 1610236800L  // 2021-01-10
        val endTimestamp = 1611100800L   // 2021-01-20

        val mockBuildList: List<Build> = objectMapper.readValue(this.javaClass.getResource("/service/builds-1.json").readText())

        `when`(buildRepository.getAllBuilds(pipelineId)).thenReturn(mockBuildList)

        assertThat(deploymentFrequencyService.getDeploymentCount(pipelineId, targetStage, startTimestamp, endTimestamp)).isEqualTo(1)
    }

    /**
     * test file: builds-3.json
     * build 1 : 2021-01-01, [build: SUCCESS, deploy to prod: SUCCESS]
     * build 2 : 2021-01-15, [build: SUCCESS, deploy to prod: FAILED]
     * build 3 : 2021-01-30, [build: FAILED, deploy to prod: SUCCESS]
     */
    @Test
    internal fun `should get deployment count with success target stage status when calculate deployment count`() {
        val pipelineId = "test pipeline - master"
        val targetStage = "deploy to prod"
        val startTimestamp = 1609286400L  // 2020-12-30
        val endTimestamp = 1612137600L   // 2021-02-01

        val mockBuildList: List<Build> = objectMapper.readValue(this.javaClass.getResource("/service/builds-3.json").readText())

        `when`(buildRepository.getAllBuilds(pipelineId)).thenReturn(mockBuildList)

        assertThat(deploymentFrequencyService.getDeploymentCount(pipelineId, targetStage, startTimestamp, endTimestamp)).isEqualTo(2)
    }

    /**
     * test file: builds-4.json
     * build 1 : 2021-12-01, [commitID: 0]
     * build 2 : 2021-01-01, [commitID: 1]
     * build 3 : 2021-01-15, [commitID: 1]
     * build 4 : 2021-01-30, [commitID: 2]
     */
    @Test
    internal fun `should get deployment count with effective deployments only when calculate deployment count`() {
        val pipelineId = "test pipeline - master"
        val targetStage = "deploy to prod"
        val startTimestamp = 1609286400L  // 2020-12-30
        val endTimestamp = 1612137600L   // 2021-02-01

        val mockBuildList: List<Build> = objectMapper.readValue(this.javaClass.getResource("/service/builds-4.json").readText())

        `when`(buildRepository.getAllBuilds(pipelineId)).thenReturn(mockBuildList)

        assertThat(deploymentFrequencyService.getDeploymentCount(pipelineId, targetStage, startTimestamp, endTimestamp)).isEqualTo(2)
    }
}