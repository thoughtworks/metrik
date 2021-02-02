package fourkeymetrics.metrics.calculator

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import fourkeymetrics.common.model.Build
import fourkeymetrics.metrics.model.LEVEL
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Duration

internal class DeploymentFrequencyCalculatorTest {

    private lateinit var deploymentFrequencyCalculator: DeploymentFrequencyCalculator


    @BeforeEach
    internal fun setUp() {
        deploymentFrequencyCalculator = DeploymentFrequencyCalculator()
    }

    /**
     * test file: builds-for-DF-case-1.json
     * build 1 : deploy to prod, SUCCESS, start at 2021-01-01, end at 2021-01-01
     * build 2 : deploy to prod, SUCCESS, start at 2021-01-15, end at 2021-01-15
     * build 3 : deploy to prod, SUCCESS, start at 2021-01-30, end at 2021-02-01
     */
    @Test
    internal fun `should return MTTR given all builds with some Aborted and others success`() {
        val allBuilds: List<Build> = ObjectMapper().readValue(
            this.javaClass.getResource("/calculator/builds-for-DF-case-1.json").readText()
        )

        val startTimestamp = 1610236800000L  // 2021-01-10
        val endTimestamp = 1611100800000L   // 2021-01-20
        val targetStage = "deploy to prod"

        val deploymentCount = deploymentFrequencyCalculator.calculateValue(allBuilds, startTimestamp, endTimestamp, targetStage)

        assertEquals(deploymentCount, 1)
    }

    /**
     * test file: builds-for-DF-case-3.json
     * build 1 : 2021-01-01, [build: SUCCESS, deploy to prod: SUCCESS]
     * build 2 : 2021-01-15, [build: SUCCESS, deploy to prod: FAILED]
     * build 3 : 2021-01-30, [build: FAILED, deploy to prod: SUCCESS]
     */
    @Test
    internal fun `should get deployment count with success target stage status when calculate deployment count`() {
        val targetStage = "deploy to prod"
        val startTimestamp = 1609286400000L  // 2020-12-30
        val endTimestamp = 1612137600000L   // 2021-02-01

        val mockBuildList: List<Build> = ObjectMapper().readValue(this.javaClass.getResource("/calculator/builds-for-DF-case-3.json").readText())

        assertThat(deploymentFrequencyCalculator.calculateValue(mockBuildList, startTimestamp, endTimestamp, targetStage)).isEqualTo(2)
    }

    @Test
    internal fun `should return elite given input deployment count greater than 15 when calculated in fortnightly`() {
        val deployments = 15
        val days = 14

        assertThat(deploymentFrequencyCalculator.calculateLevel(deployments, days)).isEqualTo(LEVEL.ELITE)
    }

    @Test
    internal fun `should return high given input deployment frequency within 1 div 7 and 1`() {
        val deployments = 1
        val days = 3

        assertThat(deploymentFrequencyCalculator.calculateLevel(deployments, days)).isEqualTo(LEVEL.HIGH)
    }

    @Test
    internal fun `should return medium given input deployment frequency within 1 div 30 and 1 div 7`() {
        val deployments = 1
        val days = 7

        assertThat(deploymentFrequencyCalculator.calculateLevel(deployments, days)).isEqualTo(LEVEL.MEDIUM)
    }

    @Test
    internal fun `should return low given input deployment frequency greater than`() {
        val deployments = 1
        val days = 30

        assertThat(deploymentFrequencyCalculator.calculateLevel(deployments, days)).isEqualTo(LEVEL.LOW)
    }

    @Test
    internal fun `should return invalid given input days is 0`() {
        val deployments = 1
        assertThat(deploymentFrequencyCalculator.calculateLevel(deployments, 0)).isEqualTo(LEVEL.INVALID)
    }

    @Test
    internal fun `should return invalid given input days is null`() {
        val deployments = 1
        assertThat(deploymentFrequencyCalculator.calculateLevel(deployments)).isEqualTo(LEVEL.INVALID)
    }
}