package fourkeymetrics.metrics.calculator

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import fourkeymetrics.common.model.Build
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@Import(MeanTimeToRestoreCalculator::class, ObjectMapper::class)
internal class MeanTimeToRestoreCalculatorTest{
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
    internal fun `should return selected builds given all builds`() {
        val allBuilds: List<Build> = objectMapper.readValue(
            this.javaClass.getResource("/calculator/builds-for-MTTR-case-1.json").readText()
        )

        val startTimestamp = 1609459200000L // 2021.01.01
        val endTimestamp = 1609689600000L // 2021.01.04
        val targetStage = "deploy to prod"

        val selectedBuilds =  meanTimeToRestoreCalculator.findSelectedBuilds(allBuilds, startTimestamp, endTimestamp, targetStage)

        assertTrue(selectedBuilds.size == 5)
    }

    /**
     * test file: builds-for-MTTR-case-2.json
     * build 1 : deploy to prod, FAILED, end at 2020-12-29
     * build 2 : deploy to prod, FAILED, end at 2020-12-30 *
     * build 3 : deploy to prod, FAILED, end at 2020-12-31 *
     * build 4 : deploy to prod, SUCCESS, end at 2021-01-01 *
     * build 5 : deploy to prod, FAILED, end at 2021-01-02 *
     * build 6 : deploy to prod, SUCCESS, end at 2021-01-03 *
     * build 7 : deploy to prod, SUCCESS, end at 2021-01-05
     */
    @Test
    internal fun `should contain first build given builds all failed before begin time`() {
        val allBuilds: List<Build> = objectMapper.readValue(
            this.javaClass.getResource("/calculator/builds-for-MTTR-case-2.json").readText()
        )

        val startTimestamp = 1609459200000L // 2021.01.01
        val endTimestamp = 1609689600000L // 2021.01.04
        val targetStage = "deploy to prod"

        val selectedBuilds =  meanTimeToRestoreCalculator.findSelectedBuilds(allBuilds, startTimestamp, endTimestamp, targetStage)

        assertTrue(selectedBuilds.size == 6)
    }
}