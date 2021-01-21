package fourkeymetrics.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import fourkeymetrics.model.Build
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.lang.Double.NaN

internal class LeadTimeForChangeCalculatorTest {
    private lateinit var leadTimeForChangeCalculator: LeadTimeForChangeCalculator


    @BeforeEach
    internal fun setUp() {
        leadTimeForChangeCalculator = LeadTimeForChangeCalculator()
    }

    @Test
    internal fun `case 1 should return value 0 when there is no deployment in given time range`() {
        val allBuilds: List<Build> = ObjectMapper().readValue(
            this.javaClass.getResource("/calculator/builds-for-MLT-case-1.json").readText()
        )
        val startTimestamp = 32847923847L
        val endTimestamp = 328479237899L
        val targetStage = "deploy to prod"

        val leadTimeForChangeValue =
            leadTimeForChangeCalculator.calculate(allBuilds, startTimestamp, endTimestamp, targetStage)

        assertEquals(0.0, leadTimeForChangeValue)
    }

    @Test
    internal fun `case 2 there is only 1 deployment and the deployment finish time is in the given time range`() {
        val allBuilds: List<Build> = ObjectMapper().readValue(
            this.javaClass.getResource("/calculator/builds-for-MLT-case-2.json").readText()
        )
        val startTimestamp = 0L
        val endTimestamp = 20L
        val targetStage = "deploy to prod"

        val leadTimeForChangeValue: Double =
            leadTimeForChangeCalculator.calculate(allBuilds, startTimestamp, endTimestamp, targetStage)

        assertEquals(5.5, leadTimeForChangeValue)
    }

    @Test
    internal fun `case 3 there is only 1 deployment and the deployment finish time is same as start time`() {
        val allBuilds: List<Build> = ObjectMapper().readValue(
            this.javaClass.getResource("/calculator/builds-for-MLT-case-3.json").readText()
        )
        val startTimestamp = 8L
        val endTimestamp = 20L
        val targetStage = "deploy to prod"

        val leadTimeForChangeValue: Double =
            leadTimeForChangeCalculator.calculate(allBuilds, startTimestamp, endTimestamp, targetStage)

        assertEquals(5.5, leadTimeForChangeValue)
    }

    @Test
    internal fun `case 4 here is only 1 deployment and the deployment time is the end time`() {
        val allBuilds: List<Build> = ObjectMapper().readValue(
            this.javaClass.getResource("/calculator/builds-for-MLT-case-4.json").readText()
        )
        val startTimestamp = 1L
        val endTimestamp = 8L
        val targetStage = "deploy to prod"

        val leadTimeForChangeValue: Double =
            leadTimeForChangeCalculator.calculate(allBuilds, startTimestamp, endTimestamp, targetStage)

        assertEquals(5.5, leadTimeForChangeValue)
    }

    @Test
    internal fun `case 5 there are 3 deployments but only the middle 1 deployment finish time in the time range`() {
        val allBuilds: List<Build> = ObjectMapper().readValue(
            this.javaClass.getResource("/calculator/builds-for-MLT-case-5.json").readText()
        )
        val startTimestamp = 10L
        val endTimestamp = 14L
        val targetStage = "deploy to prod"

        val leadTimeForChangeValue: Double =
            leadTimeForChangeCalculator.calculate(allBuilds, startTimestamp, endTimestamp, targetStage)

        assertEquals(5.5, leadTimeForChangeValue)
    }

    @Test
    internal fun `case 5-1 there are three deployments without no changeset and only the middle 1 in the time range`() {
        val allBuilds: List<Build> = ObjectMapper().readValue(
            this.javaClass.getResource("/calculator/builds-for-MLT-case-5-1.json").readText()
        )
        val startTimestamp = 11L
        val endTimestamp = 13L
        val targetStage = "deploy to prod"

        val leadTimeForChangeValue: Double =
            leadTimeForChangeCalculator.calculate(allBuilds, startTimestamp, endTimestamp, targetStage)

        assertEquals(0.0, leadTimeForChangeValue)
    }

    @Test
    internal fun `case 6 there are three deployments but only the middle 1 deployment finish time in the time range and there are some other build without ssuccessful deployment between them`() {
        val allBuilds: List<Build> = ObjectMapper().readValue(
            this.javaClass.getResource("/calculator/builds-for-MLT-case-6.json").readText()
        )
        val startTimestamp = 11L
        val endTimestamp = 13L
        val targetStage = "deploy to prod"

        val leadTimeForChangeValue: Double =
            leadTimeForChangeCalculator.calculate(allBuilds, startTimestamp, endTimestamp, targetStage)

        assertEquals(6.5, leadTimeForChangeValue)
    }

    @Test
    internal fun `case 7 there are three deployments but only the middle 1 deployment finish time in the time range and there are some other build with wrong order without successsful deployment between them`() {
        val allBuilds: List<Build> = ObjectMapper().readValue(
            this.javaClass.getResource("/calculator/builds-for-MLT-case-7.json").readText()
        )
        val startTimestamp = 11L
        val endTimestamp = 13L
        val targetStage = "deploy to prod"

        val leadTimeForChangeValue: Double =
            leadTimeForChangeCalculator.calculate(allBuilds, startTimestamp, endTimestamp, targetStage)

        assertEquals(6.5, leadTimeForChangeValue)
    }

    @Test
    internal fun `case 8 there is no deployment before time range where thereAreTwoDeploymentsAndTheFirstDeploymentFinishTimeInTheTimeRangeAndTheredAreSomeOtherBuildsWithoutSuccesssfulDeploymentBetween`() {
        val allBuilds: List<Build> = ObjectMapper().readValue(
            this.javaClass.getResource("/calculator/builds-for-MLT-case-7.json").readText()
        )
        val startTimestamp = 11L
        val endTimestamp = 13L
        val targetStage = "deploy to prod"

        val leadTimeForChangeValue: Double =
            leadTimeForChangeCalculator.calculate(allBuilds, startTimestamp, endTimestamp, targetStage)

        assertEquals(6.5, leadTimeForChangeValue)
    }

    @Test
    internal fun `case 9 there are 4 deployments and middle 2 deployment finish time in the time range`() {
        val allBuilds: List<Build> = ObjectMapper().readValue(
            this.javaClass.getResource("/calculator/builds-for-MLT-case-9.json").readText()
        )
        val startTimestamp = 11L
        val endTimestamp = 16L
        val targetStage = "deploy to prod"

        val leadTimeForChangeValue: Double =
            leadTimeForChangeCalculator.calculate(allBuilds, startTimestamp, endTimestamp, targetStage)

        assertEquals(8.0, leadTimeForChangeValue)
    }

}