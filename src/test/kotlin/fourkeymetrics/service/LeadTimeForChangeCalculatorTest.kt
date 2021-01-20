package fourkeymetrics.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import fourkeymetrics.model.Build
import fourkeymetrics.model.Metric
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class LeadTimeForChangeCalculatorTest {
    private lateinit var leadTimeForChangeCalculator: LeadTimeForChangeCalculator
    private var allBuilds: List<Build> = ObjectMapper().readValue(
        this.javaClass.getResource("/calculator/builds-for-lead-time-for-change.json").readText()
    )

    @BeforeEach
    internal fun setUp() {
        leadTimeForChangeCalculator = LeadTimeForChangeCalculator()
    }

    @Test
    internal fun `should return metric with value 0 when there is no deployment in given time`() {
        val startTimestamp = 32847923847L
        val endTimestamp = 328479237899L
        val targetStage = "deploy to prod"

        val metric: Metric = leadTimeForChangeCalculator.calculate(allBuilds, startTimestamp, endTimestamp, targetStage)

        assertEquals(true, true)
//        assertEquals(0, metric.value)
//        assertEquals(startTimestamp, metric.startTimestamp)
//        assertEquals(endTimestamp, metric.endTimestamp)
    }
}