package fourkeymetrics.utils

import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*

internal const val FORTNIGHTLY_DAYS = 14L

fun LocalDateTime.toDefaultZoneEpochMill(): Long {
    return this.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
}

fun LocalDateTime.atStartOfDay(): LocalDateTime {
    return this.toLocalDate().atStartOfDay()
}

fun getLocalDateTimeFromTimestampMillis(timestamp: Long): LocalDateTime {
    return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), TimeZone.getDefault().toZoneId())
}

fun splitTimeRangeMonthly(
    startTimestampMillis: Long,
    endTimestampMillis: Long
): List<Pair<Long, Long>> {
    val startTime = getLocalDateTimeFromTimestampMillis(startTimestampMillis)
    val endTime = getLocalDateTimeFromTimestampMillis(endTimestampMillis)
    val months = ChronoUnit.MONTHS.between(startTime, endTime)
    return if (months >= 1) {
        val timeRanges = mutableListOf<Pair<Long, Long>>()
        var tempStartTime = startTime
        LongRange(0, months - 1)
            .map { YearMonth.from(startTime).plusMonths(it) }
            .map { it.atEndOfMonth().atTime(LocalTime.MAX) }
            .forEach {
                timeRanges.add(Pair(tempStartTime.toDefaultZoneEpochMill(), it.toDefaultZoneEpochMill()))
                tempStartTime = it.plusNanos(1) // next period start time
            }
        timeRanges.add(Pair(tempStartTime.toDefaultZoneEpochMill(), endTimestampMillis))
        timeRanges.toList()
    } else {
        listOf(Pair(startTimestampMillis, endTimestampMillis))
    }
}

fun splitTimeRangeFortnightly(
    startTimestampMillis: Long,
    endTimestampMillis: Long
): List<Pair<Long, Long>> {

    val startTime = getLocalDateTimeFromTimestampMillis(startTimestampMillis)
    val endTime = getLocalDateTimeFromTimestampMillis(endTimestampMillis)
    val timeRanges = mutableListOf<Pair<Long, Long>>()

    var tempEndTime = endTime
    var tempStartTime = tempEndTime.minusDays(FORTNIGHTLY_DAYS - 1).atStartOfDay()

    while (tempStartTime.isAfter(startTime)) {
        timeRanges.add(Pair(tempStartTime.toDefaultZoneEpochMill(), tempEndTime.toDefaultZoneEpochMill()))
        tempEndTime = tempStartTime.minusNanos(1)
        tempStartTime = tempEndTime.minusDays(FORTNIGHTLY_DAYS - 1).atStartOfDay()
    }
    timeRanges.add(Pair(startTimestampMillis, tempEndTime.toDefaultZoneEpochMill()))
    return timeRanges.asReversed().toList()
}