package metrik.metrics.domain.calculator

import metrik.metrics.domain.model.LEVEL
import metrik.project.domain.model.Execution
import metrik.project.domain.model.Stage
import metrik.project.domain.model.Status
import org.springframework.stereotype.Component
import java.math.RoundingMode

@Component
class MeanTimeToRestoreCalculator : MetricsCalculator {

    companion object {
        private val TARGET_STAGE_STATUS_LIST = listOf(Status.FAILED, Status.SUCCESS)
        private const val MILLISECOND_TO_HOURS: Double = 3600000.0
        private const val NO_VALUE: Double = Double.NaN
        private const val ONE_HOUR: Double = 1.00
        private const val ONE_DAY: Double = 24.00
        private const val ONE_WEEK: Double = 168.00
    }

    override fun calculateValue(
        allExecutions: List<Execution>,
        startTimestamp: Long,
        endTimestamp: Long,
        pipelineStagesMap: Map<String, String>
    ): Number {
        val (totalTime, restoreTimes) = allExecutions
            .groupBy { it.pipelineId }
            .map {
                val selectedStages =
                    findSelectedStages(it.value, startTimestamp, endTimestamp, pipelineStagesMap[it.key])
                calculateTotalTimesAndRestoredTimes(selectedStages)
            }
            .stream().reduce(Pair(0.0, 0)) { result, current ->
                Pair(
                    result.first + current.first,
                    result.second + current.second
                )
            }

        if (restoreTimes > 0) {
            return totalTime / restoreTimes / MILLISECOND_TO_HOURS
        }
        return NO_VALUE
    }

    override fun calculateLevel(value: Number, days: Int?): LEVEL {
        val meanTimeToRestore = value.toDouble()

        if (meanTimeToRestore.isNaN()) {
            return LEVEL.INVALID
        }

        val hours = meanTimeToRestore
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

    private fun findSelectedStages(
        allExecutions: List<Execution>,
        startTimestamp: Long,
        endTimestamp: Long,
        targetStage: String?
    ): List<Stage> {
        if (targetStage == null) return emptyList()
        val targetStages = allExecutions.asSequence()
            .flatMap { it.stages }
            .filter { it.name == targetStage }
            .filter { it.status in TARGET_STAGE_STATUS_LIST }
            .sortedBy { it.getStageDoneTime() }

        val lastSuccessfulDeploymentBeforeGivenTime = targetStages.findLast {
            it.status == Status.SUCCESS && it.getStageDoneTime() < startTimestamp
        } ?: return targetStages.takeWhile { it.getStageDoneTime() <= endTimestamp }.toList()

        val lastSuccessfulDeploymentTimestamp = lastSuccessfulDeploymentBeforeGivenTime.getStageDoneTime()

        return targetStages.filter {
            it.getStageDoneTime() in
                (lastSuccessfulDeploymentTimestamp.plus(1)).rangeTo(endTimestamp)
        }.toList()
    }

    private fun calculateTotalTimesAndRestoredTimes(selectedStages: List<Stage>): Pair<Double, Int> {
        var firstFailedStage: Stage? = null
        var totalTime = 0.0
        var restoredTimes = 0
        for (stage in selectedStages) {
            if (stage.status == Status.FAILED && firstFailedStage == null) {
                firstFailedStage = stage
                continue
            }
            if (stage.status == Status.SUCCESS && firstFailedStage != null) {
                totalTime += stage.getStageDoneTime().minus(firstFailedStage.getStageDoneTime())
                restoredTimes++
                firstFailedStage = null
            }
        }
        return Pair(totalTime, restoredTimes)
    }
}
