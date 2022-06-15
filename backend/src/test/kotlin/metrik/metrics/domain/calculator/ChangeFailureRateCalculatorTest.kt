package metrik.metrics.domain.calculator

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import metrik.metrics.domain.model.LEVEL
import metrik.project.domain.model.Execution
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@Import(ChangeFailureRateCalculator::class)
internal class ChangeFailureRateCalculatorTest {
    private val changeFailureRateCalculator = ChangeFailureRateCalculator()

    private val objectMapper = jacksonObjectMapper()

    /**
     * test file: builds-for-CFR-case-1.json
     * build 1 : 1606780800000, SUCCESS (deploy to prod)
     * build 2 : 1609459200000, FAILED (deploy to prod)
     * build 3 : 1610668800000, SUCCESS (deploy to prod)
     * build 4 : 1611964800000, SUCCESS (deploy to prod)
     * build 5 : 1611974800000, SUCCESS (deploy to uat)
     */
    @Test
    fun `should get change failure rate within time range given 3 valid build inside in 1 pipeline when calculate CFR`() {
        val pipelineStageMap = mapOf(Pair("1", "deploy to prod"))
        // build 2 - build 5
        val startTimestamp = 1609459200000L
        val endTimestamp = 1611994800000L

        val mockExecutionList: List<Execution> =
            objectMapper.readValue(this.javaClass.getResource("/calculator/builds-for-CFR-case-1.json").readText())

        assertThat(
            changeFailureRateCalculator.calculateValue(
                mockExecutionList,
                startTimestamp,
                endTimestamp,
                pipelineStageMap
            )
        ).isEqualTo(1 / 3.0 * PERCENTAGE_FACTOR)
    }

    /**
     * test file: builds-for-CFR-case-2.json
     * build 1 : 1606780800000, SUCCESS (deploy to prod) pipeline1
     * build 2 : 1609459200000, FAILED (deploy to prod) pipeline1
     * build 3 : 1610668800000, SUCCESS (deploy to prod) pipeline1
     * build 4 : 1611964800000, SUCCESS (deploy to prod) pipeline1
     * build 5 : 1611974800000, SUCCESS (deploy to uat) pipeline1

     * build 1 : 1606780800000, SUCCESS (deploy to uat) pipeline2
     * build 2 : 1609459200000, SUCCESS (deploy to uat) pipeline2
     * build 3 : 1610668800000, SUCCESS (deploy to uat) pipeline2
     * build 4 : 1611974800000, SUCCESS (deploy to prod) pipeline2
     */
    @Test
    fun `should get change failure rate within time range given 3 valid build inside in 2 pipeline when calculate CFR`() {
        val pipelineStageMap = mapOf(Pair("1", "deploy to prod"), Pair("2", "deploy to uat"))
        // build 2 - build 5 pipeline1, build2 - build4 pipeline2
        val startTimestamp = 1609459200000L
        val endTimestamp = 1611994800000L

        val mockExecutionList: List<Execution> =
            objectMapper.readValue(this.javaClass.getResource("/calculator/builds-for-CFR-case-2.json").readText())

        assertThat(
            changeFailureRateCalculator.calculateValue(
                mockExecutionList,
                startTimestamp,
                endTimestamp,
                pipelineStageMap
            )
        ).isEqualTo(1 / 5.0 * PERCENTAGE_FACTOR)
    }

    /**
     * test file: builds-for-CFR-case-1.json
     * build 1 : 1606780800000, SUCCESS (deploy to prod)
     * build 2 : 1609459200000, FAILED (deploy to prod)
     * build 3 : 1610668800000, SUCCESS (deploy to prod)
     * build 4 : 1611964800000, SUCCESS (deploy to prod)
     * build 5 : 1611974800000, SUCCESS (deploy to uat)
     */
    @Test
    fun `should get zero within time range given no valid build when calculate CFR`() {
        val targetStage = "deploy to prod"
        // build 5
        val startTimestamp = 1611974800000L
        val endTimestamp = 1611974800000L

        val mockExecutionList: List<Execution> =
            objectMapper.readValue(this.javaClass.getResource("/calculator/builds-for-CFR-case-1.json").readText())

        assertThat(
            changeFailureRateCalculator.calculateValue(
                mockExecutionList,
                startTimestamp,
                endTimestamp,
                mapOf(Pair("1", targetStage))
            )
        ).isEqualTo(Double.NaN)
    }

    @Test
    fun `should get INVALID_VALUE given no builds`() {
        val targetStage = "deploy to prod"
        val startTimestamp = 1611974800000L
        val endTimestamp = 1611974800000L

        assertThat(
            changeFailureRateCalculator.calculateValue(
                emptyList(),
                startTimestamp,
                endTimestamp,
                mapOf(Pair("1", targetStage))
            )
        ).isEqualTo(Double.NaN)
    }

    @Test
    fun `should get INVALID_VALUE given no pipeline stage map`() {
        val startTimestamp = 1611974800000L
        val endTimestamp = 1611974800000L

        assertThat(
            changeFailureRateCalculator.calculateValue(
                listOf(Execution(pipelineId = "1")),
                startTimestamp,
                endTimestamp,
                mapOf()
            )
        ).isEqualTo(Double.NaN)
    }

    @Test
    fun `should return level low given value is 0_45 when calculate level`() {
        assertEquals(LEVEL.LOW, changeFailureRateCalculator.calculateLevel(0.45 * PERCENTAGE_FACTOR))
    }

    @Test
    fun `should return level medium given value is 0_3 when calculate level`() {
        assertEquals(LEVEL.MEDIUM, changeFailureRateCalculator.calculateLevel(0.3 * PERCENTAGE_FACTOR))
    }

    @Test
    fun `should return level high given value is 0_15 when calculate level`() {
        assertEquals(LEVEL.HIGH, changeFailureRateCalculator.calculateLevel(0.15 * PERCENTAGE_FACTOR))
    }

    @Test
    fun `should return level elite given value lower than 0_14 when calculate level`() {
        assertEquals(LEVEL.ELITE, changeFailureRateCalculator.calculateLevel(0.14 * PERCENTAGE_FACTOR))
    }

    @Test
    fun `should return level invalid given invalid value  when calculate level`() {
        assertEquals(LEVEL.INVALID, changeFailureRateCalculator.calculateLevel(Double.NaN))
    }

    companion object {
        private const val PERCENTAGE_FACTOR = 100.0
    }
}
