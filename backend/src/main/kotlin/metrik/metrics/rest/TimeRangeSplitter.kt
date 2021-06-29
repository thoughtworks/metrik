package metrik.metrics.rest

import metrik.infrastructure.utlils.atStartOfDay
import metrik.infrastructure.utlils.endTimeOfSameMonth
import metrik.infrastructure.utlils.toDefaultZoneEpochMill
import metrik.infrastructure.utlils.toLocalDateTime
import metrik.metrics.domain.model.CalculationPeriod

object TimeRangeSplitter {

    fun split(
        startTimestamp: Long,
        endTimestamp: Long,
        period: CalculationPeriod
    ): List<Pair<Long, Long>> {
        return when (period) {
            CalculationPeriod.Monthly -> splitTimeRangeMonthly(startTimestamp, endTimestamp)
            CalculationPeriod.Fortnightly -> splitTimeRangeFortnightly(startTimestamp, endTimestamp)
        }
    }

    private fun splitTimeRangeMonthly(
        startTimestamp: Long,
        endTimestamp: Long
    ): List<Pair<Long, Long>> {
        val startTime = startTimestamp.toLocalDateTime()
        val endTime = endTimestamp.toLocalDateTime()
        val timeRanges = mutableListOf<Pair<Long, Long>>()
        var tempStartTime = startTime
        var tempEndTime = startTime.endTimeOfSameMonth()
        while (tempEndTime.isBefore(endTime)) {
            timeRanges.add(Pair(tempStartTime.toDefaultZoneEpochMill(), tempEndTime.toDefaultZoneEpochMill()))
            tempStartTime = tempEndTime.plusNanos(1)
            tempEndTime = tempStartTime.endTimeOfSameMonth()
        }
        timeRanges.add(Pair(tempStartTime.toDefaultZoneEpochMill(), endTime.toDefaultZoneEpochMill()))
        return timeRanges.toList()
    }

    private fun splitTimeRangeFortnightly(
        startTimestamp: Long,
        endTimestamp: Long
    ): List<Pair<Long, Long>> {

        val startTime = startTimestamp.toLocalDateTime()
        val endTime = endTimestamp.toLocalDateTime()
        val timeRanges = mutableListOf<Pair<Long, Long>>()

        var tempEndTime = endTime
        var tempStartTime = tempEndTime.minusDays(CalculationPeriod.Fortnightly.timeInDays - 1).atStartOfDay()

        while (tempStartTime.isAfter(startTime)) {
            timeRanges.add(Pair(tempStartTime.toDefaultZoneEpochMill(), tempEndTime.toDefaultZoneEpochMill()))
            tempEndTime = tempStartTime.minusNanos(1)
            tempStartTime = tempEndTime.minusDays(CalculationPeriod.Fortnightly.timeInDays - 1).atStartOfDay()
        }
        timeRanges.add(Pair(startTimestamp, tempEndTime.toDefaultZoneEpochMill()))
        return timeRanges.asReversed().toList()
    }
}
