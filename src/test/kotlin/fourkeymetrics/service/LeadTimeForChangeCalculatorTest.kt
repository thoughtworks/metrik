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

    @Test
    internal fun `case 1 should return metric with value 0 when there is no deployment in given time range`() {
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
    internal fun `case 2 should return metric with correct value when the first deployment is in the given time range`() {
        val allBuilds: List<Build> = ObjectMapper().readValue(
            this.javaClass.getResource("/calculator/builds-for-MLT-case-2.json").readText()
        )
        val startTimestamp = 0L
        val endTimestamp = 7L
        val targetStage = "deploy to prod"

        val leadTimeForChangeValue: Double =
            leadTimeForChangeCalculator.calculate(allBuilds, startTimestamp, endTimestamp, targetStage)

        assertEquals(3.5, leadTimeForChangeValue)
    }
}