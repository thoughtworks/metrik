package fourkeymetrics.metrics.calculator

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import fourkeymetrics.common.model.Build
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@Import(MeanTimeToRestoreCalculator::class, ObjectMapper::class)
internal class MeanTimeToRestoreCalculatorTest {
    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var meanTimeToRestoreCalculator: MeanTimeToRestoreCalculator

    /**
     * test file: builds-for-MTTR-case-1.json
     * build 1 : deploy to prod, SUCCESS, end at 2020-12-29
     * build 2 : deploy to prod, FAILED, end at 2020-12-30 *
     * build 3 : deploy to prod, FAILED, end at 2020-12-31 *
     * build 4 : deploy to prod, SUCCESS, end at 2021-01-01 *
     * build 5 : deploy to prod, FAILED, end at 2021-01-02 *
     * build 6 : deploy to prod, SUCCESS, end at 2021-01-03 *
     * build 7 : deploy to prod, SUCCESS, end at 2021-01-05
     */
    @Test
    internal fun `should return MTTR given all builds`() {
        val allBuilds: List<Build> = objectMapper.readValue(
            this.javaClass.getResource("/calculator/builds-for-MTTR-case-1.json").readText()
        )

        val startTimestamp = 1609459200000L // 2021.01.01
        val endTimestamp = 1609689600000L // 2021.01.04
        val targetStage = "deploy to prod"

        val mttr = meanTimeToRestoreCalculator.calculateValue(allBuilds, startTimestamp, endTimestamp, targetStage)

        assertThat(mttr).isEqualTo(40.00)
    }

    /**
     * test file: builds-for-MTTR-case-2.json
     * build 1 : deploy to prod, FAILED, end at 2020-12-29 *
     * build 2 : deploy to prod, FAILED, end at 2020-12-30 *
     * build 3 : deploy to prod, FAILED, end at 2020-12-31 *
     * build 4 : deploy to prod, SUCCESS, end at 2021-01-01 *
     * build 5 : deploy to prod, FAILED, end at 2021-01-02 *
     * build 6 : deploy to prod, SUCCESS, end at 2021-01-03 *
     * build 7 : deploy to prod, SUCCESS, end at 2021-01-05
     */
    @Test
    internal fun `should contain first stage given builds all failed before begin time`() {
        val allBuilds: List<Build> = objectMapper.readValue(
            this.javaClass.getResource("/calculator/builds-for-MTTR-case-2.json").readText()
        )

        val startTimestamp = 1609459200000L // 2021.01.01
        val endTimestamp = 1609689600000L // 2021.01.04
        val targetStage = "deploy to prod"

        val averageMTTR = meanTimeToRestoreCalculator.calculateValue(allBuilds, startTimestamp, endTimestamp, targetStage)

        assertThat(averageMTTR).isEqualTo(52.00)
    }

    /**
     * test file: builds-for-MTTR-case-3.json
     * build 1 : deploy to prod, SUCCESS, end at 2020-12-29
     * build 2 : deploy to prod, SUCCESS, end at 2020-12-30
     * build 3 : deploy to prod, SUCCESS, end at 2020-12-31
     * build 4 : deploy to prod, SUCCESS, end at 2021-01-01
     * build 5 : deploy to prod, SUCCESS, end at 2021-01-02
     * build 6 : deploy to prod, SUCCESS, end at 2021-01-03
     * build 7 : deploy to prod, SUCCESS, end at 2021-01-05
     */
    @Test
    internal fun `should return NaN while all builds are success`() {
        val allBuilds: List<Build> = objectMapper.readValue(
            this.javaClass.getResource("/calculator/builds-for-MTTR-case-3.json").readText()
        )

        val startTimestamp = 1609459200000L // 2021.01.01
        val endTimestamp = 1609689600000L // 2021.01.04
        val targetStage = "deploy to prod"

        val averageMTTR = meanTimeToRestoreCalculator.calculateValue(allBuilds, startTimestamp, endTimestamp, targetStage)

        assertEquals(averageMTTR, Double.NaN)
    }

    /**
     * test file: builds-for-MTTR-case-4.json
     * build 1 : deploy to prod, FAILED, end at 2020-12-29
     * build 2 : deploy to prod, FAILED, end at 2020-12-30
     * build 3 : deploy to prod, FAILED, end at 2020-12-31
     * build 4 : deploy to prod, FAILED, end at 2021-01-01
     * build 5 : deploy to prod, FAILED, end at 2021-01-02
     * build 6 : deploy to prod, FAILED, end at 2021-01-03
     * build 7 : deploy to prod, FAILED, end at 2021-01-05
     */
    @Test
    internal fun `should return NaN while all builds are failed`() {
        val allBuilds: List<Build> = objectMapper.readValue(
            this.javaClass.getResource("/calculator/builds-for-MTTR-case-4.json").readText()
        )

        val startTimestamp = 1609459200000L // 2021.01.01
        val endTimestamp = 1609689600000L // 2021.01.04
        val targetStage = "deploy to prod"

        val averageMTTR = meanTimeToRestoreCalculator.calculateValue(allBuilds, startTimestamp, endTimestamp, targetStage)

        assertEquals(averageMTTR, Double.NaN)
    }

    /**
     * test file: builds-for-MTTR-case-5.json
     * build 1 : deploy to prod, SUCCESS, end at 2020-12-29
     * build 2 : deploy to prod, SUCCESS, end at 2020-12-30
     * build 3 : deploy to prod, FAILED, end at 2020-12-31 *
     * build 4 : deploy to prod, SUCCESS, end at 2021-01-01 *
     * build 5 : deploy to prod, SUCCESS, end at 2021-01-02 *
     * build 6 : deploy to prod, FAILED, end at 2021-01-03 *
     * build 7 : deploy to prod, SUCCESS, end at 2021-01-05
     */
    @Test
    internal fun `should return correct MTTR given last stage status is failed `() {
        val allBuilds: List<Build> = objectMapper.readValue(
            this.javaClass.getResource("/calculator/builds-for-MTTR-case-5.json").readText()
        )

        val startTimestamp = 1609459200000L // 2021.01.01
        val endTimestamp = 1609689600000L // 2021.01.04
        val targetStage = "deploy to prod"

        val averageMTTR = meanTimeToRestoreCalculator.calculateValue(allBuilds, startTimestamp, endTimestamp, targetStage)

        assertThat(averageMTTR).isEqualTo(32.14)
    }
}