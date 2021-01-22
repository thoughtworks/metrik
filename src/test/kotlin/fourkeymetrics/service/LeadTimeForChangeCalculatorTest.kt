package fourkeymetrics.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import fourkeymetrics.model.Build
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class LeadTimeForChangeCalculatorTest {
    private lateinit var leadTimeForChangeCalculator: LeadTimeForChangeCalculator


    @BeforeEach
    internal fun setUp() {
        leadTimeForChangeCalculator = LeadTimeForChangeCalculator()
    }

    /**
     * test file: builds-for-MLT-case-1.json
     * build 1 : star time 3: deploy to prod, SUCCESS, deployment finish time 8, two commits time: 1, 2
     */
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

    /**
     * test file: builds-for-MLT-case-2.json
     * build 1 : star time 3: deploy to prod, SUCCESS, deployment finish time 8, two commits time: 1, 2
     */
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

        assertEquals(6.5, leadTimeForChangeValue)
    }


    /**
     * test file: builds-for-MLT-case-3.json
     * build 1 : star time 3: deploy to prod, SUCCESS, deployment finish time 8, two commits time: 1, 2
     */
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

        assertEquals(6.5, leadTimeForChangeValue)
    }


    /**
     * test file: builds-for-MLT-case-4.json
     * build 1 : star time 3: deploy to prod, SUCCESS, deployment finish time 8, two commits time: 1, 2
     */
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

        assertEquals(6.5, leadTimeForChangeValue)
    }


    /**
     * test file: builds-for-MLT-case-5.json
     * build 1 : star time 3: deploy to prod, SUCCESS, deployment finish time 8, two commits time: 1, 2
     * build 2 : star time 8: deploy to prod, SUCCESS, deployment finish time 13, two commits time: 6, 7
     * build 3 : star time 12: deploy to prod, SUCCESS, deployment finish time 17, two commits time: 10, 11
     */
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

        assertEquals(6.5, leadTimeForChangeValue)
    }

    /**
     * test file: builds-for-MLT-case-5-1.json
     * build 1 : star time 3: deploy to prod, SUCCESS, deployment finish time 8, no commits
     * build 2 : star time 8: deploy to prod, SUCCESS, deployment finish time 13, no commits
     * build 3 : star time 12: deploy to prod, SUCCESS, deployment finish time 17, no commits
     */
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

    /**
     * test file: builds-for-MLT-case-6.json
     * build 1 : star time 3: deploy to prod, SUCCESS, deployment finish time 8, two commits time: 1, 2
     * build 2 : star time 8: deploy to prod, FAILED, deployment finish time 11, two commits time: 4, 5
     * build 3 : star time 12: deploy to prod, ABORTED, deployment finish time 14, no commits
     * build 4 : star time 15: deploy to prod, SUCCESS, deployment finish time 19, two commits time: 13, 14
     * build 5 : star time 20: deploy to prod, ABORTED, deployment finish time 25, no commits
     * build 6 : star time 26: deploy to prod, ABORTED, deployment finish time 30, two commits time: 22, 23
     * build 7 : star time 31: deploy to prod, SUCCESS, deployment finish time 36, two commits time: 28, 29
     */
    @Test
    internal fun `case 6 there are three deployments but only the middle 1 deployment finish time in the time range and there are some other build without ssuccessful deployment between them`() {
        val allBuilds: List<Build> = ObjectMapper().readValue(
            this.javaClass.getResource("/calculator/builds-for-MLT-case-6.json").readText()
        )
        val startTimestamp = 13L
        val endTimestamp = 26L
        val targetStage = "deploy to prod"

        val leadTimeForChangeValue: Double =
            leadTimeForChangeCalculator.calculate(allBuilds, startTimestamp, endTimestamp, targetStage)

        assertEquals(10.0, leadTimeForChangeValue)
    }


    /**
     * test file: builds-for-MLT-case-7.json
     * build 3 : star time 12: deploy to prod, ABORTED, deployment finish time 14, no commits
     * build 7 : star time 31: deploy to prod, SUCCESS, deployment finish time 36, two commits time: 28, 29
     * build 1 : star time 3: deploy to prod, SUCCESS, deployment finish time 8, two commits time: 1, 2
     * build 2 : star time 8: deploy to prod, FAILED, deployment finish time 11, two commits time: 4, 5
     * build 4 : star time 15: deploy to prod, SUCCESS, deployment finish time 19, two commits time: 13, 14
     * build 5 : star time 20: deploy to prod, ABORTED, deployment finish time 25, no commits
     * build 6 : star time 26: deploy to prod, ABORTED, deployment finish time 30, two commits time: 22, 23
     */
    @Test
    internal fun `case 7 there are three deployments but only the middle 1 deployment finish time in the time range and there are some other build with wrong order without successsful deployment between them`() {
        val allBuilds: List<Build> = ObjectMapper().readValue(
            this.javaClass.getResource("/calculator/builds-for-MLT-case-7.json").readText()
        )
        val startTimestamp = 13L
        val endTimestamp = 26L
        val targetStage = "deploy to prod"

        val leadTimeForChangeValue: Double =
            leadTimeForChangeCalculator.calculate(allBuilds, startTimestamp, endTimestamp, targetStage)

        assertEquals(10.0, leadTimeForChangeValue)
    }


    /**
     * test file: builds-for-MLT-case-8.json
     * build 2 : star time 8: deploy to prod, FAILED, deployment finish time 11, two commits time: 4, 5
     * build 3 : star time 12: deploy to prod, ABORTED, deployment finish time 14, no commits
     * build 4 : star time 15: deploy to prod, SUCCESS, deployment finish time 19, two commits time: 13, 14
     * build 5 : star time 20: deploy to prod, ABORTED, deployment finish time 25, no commits
     * build 6 : star time 26: deploy to prod, ABORTED, deployment finish time 30, two commits time: 22, 23
     * build 7 : star time 31: deploy to prod, SUCCESS, deployment finish time 36, two commits time: 28, 29
     */
    @Test
    internal fun `case 8 there is no deployment before time range where thereAreTwoDeploymentsAndTheFirstDeploymentFinishTimeInTheTimeRangeAndTheredAreSomeOtherBuildsWithoutSuccesssfulDeploymentBetween`() {
        val allBuilds: List<Build> = ObjectMapper().readValue(
            this.javaClass.getResource("/calculator/builds-for-MLT-case-8.json").readText()
        )
        val startTimestamp = 13L
        val endTimestamp = 26L
        val targetStage = "deploy to prod"

        val leadTimeForChangeValue: Double =
            leadTimeForChangeCalculator.calculate(allBuilds, startTimestamp, endTimestamp, targetStage)

        assertEquals(10.0, leadTimeForChangeValue)
    }


    /**
     * test file: builds-for-MLT-case-9.json
     * build 1 : star time 3: deploy to prod, SUCCESS, deployment finish time 8, two commits time: 1, 2
     * build 2 : star time 8: deploy to prod, SUCCESS, deployment finish time 13, two commits time: 6, 7
     * build 3 : star time 12: deploy to prod, SUCCESS, deployment finish time 17, three commits time: 10, 11,12
     * build 4 : star time 18: deploy to prod, SUCCESS, deployment finish time 23, two commits time: 16, 17
     */
    @Test
    internal fun `case 9 there are 4 deployments and the middle two in the time range`() {
        val allBuilds: List<Build> = ObjectMapper().readValue(
            this.javaClass.getResource("/calculator/builds-for-MLT-case-9.json").readText()
        )
        val startTimestamp = 13L
        val endTimestamp = 17L
        val targetStage = "deploy to prod"

        val leadTimeForChangeValue: Double =
            leadTimeForChangeCalculator.calculate(allBuilds, startTimestamp, endTimestamp, targetStage)

        assertEquals(6.2, leadTimeForChangeValue)
    }

    /**
     * test file: builds-for-MLT-case-10.json
     * build 1 : star time 3: deploy to prod, SUCCESS, deployment finish time 8, two commits time: 1, 2
     * build 2 : star time 8: deploy to prod, FAILED, deployment finish time 13, two commits time: 6, 7
     * build 3 : star time 12: deploy to prod, SUCCESS, deployment finish time 17, three commits time: 10, 11,12
     * build 5 : star time 18: deploy to prod, ABORTED, deployment finish time 23, two commits time: 16, 17
     * build 6 : star time 24: deploy to prod, ABORTED, deployment finish time 29, no commits
     * build 7 : star time 30: deploy to prod, SUCCESS, deployment finish time 34, no commits
     * build 8 : star time 35: deploy to prod, ABORTED, deployment finish time 39, two commits time: 33, 34
     * build 9 : star time 40: deploy to prod, SUCCESS, deployment finish time 44, two commits time: 37, 38
     * build 10 : star time 45: deploy to prod, ABORTED, deployment finish time 49, two commits time: 41, 42
     * result  = (  (44*4-37-38-33-34) + (34*2-16-17) + (17*5-10-11-12-6-7)  )/11
     */
    @Test
    internal fun `case 10 there are 4 deployments and the last 3 in the time range and there are other buildsWithoutDeploymentBetween`() {
        val allBuilds: List<Build> = ObjectMapper().readValue(
            this.javaClass.getResource("/calculator/builds-for-MLT-case-10.json").readText()
        )
        val startTimestamp = 13L
        val endTimestamp = 49L
        val targetStage = "deploy to prod"

        val leadTimeForChangeValue: Double =
            leadTimeForChangeCalculator.calculate(allBuilds, startTimestamp, endTimestamp, targetStage)

        assertEquals(9.818181818181818, leadTimeForChangeValue)
    }

}