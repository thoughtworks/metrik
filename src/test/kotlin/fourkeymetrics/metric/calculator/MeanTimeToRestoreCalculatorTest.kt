package fourkeymetrics.metric.calculator

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import fourkeymetrics.metric.model.Build
import fourkeymetrics.metric.model.BuildStatus
import fourkeymetrics.metric.model.Stage
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
    internal fun `should return selected stages given all builds`() {
        val allBuilds: List<Build> = objectMapper.readValue(
            this.javaClass.getResource("/calculator/builds-for-MTTR-case-1.json").readText()
        )

        val startTimestamp = 1609459200000L // 2021.01.01
        val endTimestamp = 1609689600000L // 2021.01.04
        val targetStage = "deploy to prod"

        val selectedStages = meanTimeToRestoreCalculator.findSelectedStages(allBuilds, startTimestamp, endTimestamp, targetStage)

        assertTrue(selectedStages.size == 5)
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
    internal fun `should contain first stage given builds all failed before begin time`() {
        val allBuilds: List<Build> = objectMapper.readValue(
            this.javaClass.getResource("/calculator/builds-for-MTTR-case-2.json").readText()
        )

        val startTimestamp = 1609459200000L // 2021.01.01
        val endTimestamp = 1609689600000L // 2021.01.04
        val targetStage = "deploy to prod"

        val selectedStages = meanTimeToRestoreCalculator.findSelectedStages(allBuilds, startTimestamp, endTimestamp, targetStage)

        assertTrue(selectedStages.size == 6)
        assertThat(selectedStages[0].name).isEqualTo(targetStage)
        assertThat(selectedStages[0].status).isEqualTo(BuildStatus.FAILED)
        assertThat(selectedStages[0].startTimeMillis).isEqualTo(1609171200000L)
    }

    @Test
    internal fun `should return restored build stages given selected builds`() {
        val targetStage = "deploy to prod"

        val selectedBuilds: List<Build> = objectMapper.readValue(
            this.javaClass.getResource("/calculator/builds-for-MTTR-restored-pair-case-1.json").readText()
        )

        val selectedStages: List<Stage> = selectedBuilds.flatMap { build -> build.stages }.filter { stage -> stage.name == targetStage }

        val restoredStagePairs = meanTimeToRestoreCalculator.generateRestoredPairs(selectedStages)

        assertThat(restoredStagePairs.size).isEqualTo(2)
        assertThat(restoredStagePairs[0].first.status).isEqualTo(BuildStatus.FAILED)
        assertThat(restoredStagePairs[0].first.startTimeMillis).isEqualTo(1609257600000L)
        assertThat(restoredStagePairs[0].second.status).isEqualTo(BuildStatus.SUCCESS)
        assertThat(restoredStagePairs[0].second.startTimeMillis).isEqualTo(1609459200000L)
        assertThat(restoredStagePairs[1].first.status).isEqualTo(BuildStatus.FAILED)
        assertThat(restoredStagePairs[1].first.startTimeMillis).isEqualTo(1609516800000L)
        assertThat(restoredStagePairs[1].second.status).isEqualTo(BuildStatus.SUCCESS)
        assertThat(restoredStagePairs[1].second.startTimeMillis).isEqualTo(1609603200000L)
    }

    @Test
    internal fun `should return restored build stages given selected builds with last stage is failed`() {
        val targetStage = "deploy to prod"

        val selectedBuilds: List<Build> = objectMapper.readValue(
            this.javaClass.getResource("/calculator/builds-for-MTTR-restored-pair-case-2.json").readText()
        )

        val selectedStages: List<Stage> = selectedBuilds.flatMap { build -> build.stages }.filter { stage -> stage.name == targetStage }

        val restoredStagePairs = meanTimeToRestoreCalculator.generateRestoredPairs(selectedStages)

        assertThat(restoredStagePairs.size).isEqualTo(1)
        assertThat(restoredStagePairs[0].first.status).isEqualTo(BuildStatus.FAILED)
        assertThat(restoredStagePairs[0].first.startTimeMillis).isEqualTo(1609344000000L)
        assertThat(restoredStagePairs[0].second.status).isEqualTo(BuildStatus.SUCCESS)
        assertThat(restoredStagePairs[0].second.startTimeMillis).isEqualTo(1609459200000L)
    }

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

        // first mttr 56.00
        // second mttr 24.00
        assertThat(mttr).isEqualTo(40.00)
    }
}