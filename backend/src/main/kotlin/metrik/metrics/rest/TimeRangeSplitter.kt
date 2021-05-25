package metrik.metrics.rest

import metrik.metrics.domain.model.MetricsUnit
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.ZoneId
import java.util.*

@Component
class TimeRangeSplitter {

    companion object {
        private const val FORTNIGHTLY_DAYS: Long = 14L
    }

    private fun getLocalDateTimeFromTimestampMillis(timestamp: Long): LocalDateTime {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), TimeZone.getDefault().toZoneId())
    }

    fun split(
        startTimestamp: Long,
        endTimestamp: Long,
        unit: MetricsUnit
    ): List<Pair<Long, Long>> {
        return when (unit) {
            MetricsUnit.Monthly -> splitTimeRangeMonthly(startTimestamp, endTimestamp)
            MetricsUnit.Fortnightly -> splitTimeRangeFortnightly(startTimestamp, endTimestamp)
        }

    }

    private fun splitTimeRangeMonthly(
        startTimestamp: Long,
        endTimestamp: Long
    ): List<Pair<Long, Long>> {
        val startTime = getLocalDateTimeFromTimestampMillis(startTimestamp)
        val endTime = getLocalDateTimeFromTimestampMillis(endTimestamp)
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

        val startTime = getLocalDateTimeFromTimestampMillis(startTimestamp)
        val endTime = getLocalDateTimeFromTimestampMillis(endTimestamp)
        val timeRanges = mutableListOf<Pair<Long, Long>>()

        var tempEndTime = endTime
        var tempStartTime = tempEndTime.minusDays(FORTNIGHTLY_DAYS - 1).atStartOfDay()

        while (tempStartTime.isAfter(startTime)) {
            timeRanges.add(Pair(tempStartTime.toDefaultZoneEpochMill(), tempEndTime.toDefaultZoneEpochMill()))
            tempEndTime = tempStartTime.minusNanos(1)
            tempStartTime = tempEndTime.minusDays(FORTNIGHTLY_DAYS - 1).atStartOfDay()
        }
        timeRanges.add(Pair(startTimestamp, tempEndTime.toDefaultZoneEpochMill()))
        return timeRanges.asReversed().toList()
    }

}

fun LocalDateTime.toDefaultZoneEpochMill(): Long {
    return this.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
}

fun LocalDateTime.atStartOfDay(): LocalDateTime {
    return this.toLocalDate().atStartOfDay()
}

fun LocalDateTime.endTimeOfSameMonth(): LocalDateTime {
    return YearMonth.from(this).atEndOfMonth().atTime(LocalTime.MAX)
}

