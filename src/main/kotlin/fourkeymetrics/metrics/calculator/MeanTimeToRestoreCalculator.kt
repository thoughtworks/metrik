package fourkeymetrics.metrics.calculator

import fourkeymetrics.common.model.Build
import fourkeymetrics.common.model.Stage
import fourkeymetrics.common.model.StageStatus
import fourkeymetrics.metrics.model.LEVEL
import fourkeymetrics.metrics.model.MetricsUnit
import org.springframework.stereotype.Component
import java.math.RoundingMode

@Component
class MeanTimeToRestoreCalculator : MetricsCalculator {

    companion object {
        private val TARGET_STAGE_STATUS_LIST = listOf(StageStatus.FAILED, StageStatus.SUCCESS)
        private const val MILLISECOND_TO_HOURS: Double = 3600000.0
        private const val NO_VALUE: Double = Double.NaN
        private const val ONE_HOUR: Double = 1.00
        private const val ONE_DAY: Double = 24.00
        private const val ONE_WEEK: Double = 168.00
    }

    override fun calculateValue(allBuilds: List<Build>, startTimestamp: Long,
                                endTimestamp: Long, targetStage: String): Number {
        val selectedStages = findSelectedStages(allBuilds, startTimestamp, endTimestamp, targetStage)

        return calculateMTTR(selectedStages)
    }

    override fun calculateLevel(value: Number, unit: MetricsUnit?): LEVEL {
        val meanTimeToRestore = value.toDouble()

        if (meanTimeToRestore.isNaN()) {
            return LEVEL.INVALID
        }

        val hours = meanTimeToRestore
            .div(MILLISECOND_TO_HOURS)
            .toBigDecimal()
            .setScale(2, RoundingMode.HALF_UP)
            .toDouble()

        return when {
            hours < ONE_HOUR -> LEVEL.ELITE
            hours < ONE_DAY -> LEVEL.HIGH
            hours < ONE_WEEK -> LEVEL.MEDIUM
            else -> LEVEL.LOW
        }
    }

    private fun findSelectedStages(allBuilds: List<Build>, startTimestamp: Long, endTimestamp: Long,
                                   targetStage: String): List<Stage> {
        val targetStages = allBuilds.asSequence()
            .flatMap { it.stages }
            .filter { it.name == targetStage }
            .filter { it.status in TARGET_STAGE_STATUS_LIST }
            .sortedBy { it.getStageDoneTime() }

        val lastSuccessfulDeploymentBeforeGivenTime = targetStages.findLast {
            it.status == StageStatus.SUCCESS && it.getStageDoneTime() < startTimestamp
        }?: return targetStages.takeWhile { it.getStageDoneTime() <= endTimestamp }.toList()

        val lastSuccessfulDeploymentTimestamp = lastSuccessfulDeploymentBeforeGivenTime.getStageDoneTime()

        return targetStages.filter {
            it.getStageDoneTime() in
                (lastSuccessfulDeploymentTimestamp.plus(1)).rangeTo(endTimestamp)
        }.toList()
    }

    private fun calculateMTTR(selectedStages: List<Stage>): Double {
        var firstFailedStage: Stage? = null
        var totalTime = 0.0
        var restoredTimes = 0
        for (stage in selectedStages) {
            if (stage.status == StageStatus.FAILED && firstFailedStage == null) {
                firstFailedStage = stage
                continue
            }
            if (stage.status == StageStatus.SUCCESS && firstFailedStage != null) {
                totalTime += stage.getStageDoneTime().minus(firstFailedStage.getStageDoneTime())
                restoredTimes++
                firstFailedStage = null
            }
        }
        if (restoredTimes > 0) {
            return totalTime.div(restoredTimes)
        }
        return NO_VALUE
    }
}