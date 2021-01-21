package fourkeymetrics.utils

import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.temporal.ChronoUnit
import java.util.*

internal const val FORTNIGHTLY_DAYS = 13L

fun getLocalDateTimeFromTimestampMillis(timestamp: Long): LocalDateTime {
    return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), TimeZone.getDefault().toZoneId())
}

fun splitTimeRangeMonthly(
    startTimestampMillis: Long,
    endTimestampMillis: Long
): List<Pair<LocalDateTime, LocalDateTime>> {
    val startTime = getLocalDateTimeFromTimestampMillis(startTimestampMillis)
    val endTime = getLocalDateTimeFromTimestampMillis(endTimestampMillis)
    val months = ChronoUnit.MONTHS.between(startTime, endTime)
    return if (months >= 1) {
        val timeRanges = mutableListOf<Pair<LocalDateTime, LocalDateTime>>()
        var tempStartTime = startTime
        LongRange(0, months - 1)
            .map { YearMonth.from(startTime).plusMonths(it) }
            .map { it.atEndOfMonth().atTime(LocalTime.MAX) }
            .forEach {
                timeRanges.add(Pair(tempStartTime, it))
                tempStartTime = it.plusNanos(1)
            }
        timeRanges.add(Pair(tempStartTime, endTime))
        timeRanges.toList()
    } else {
        listOf(Pair(startTime, endTime))
    }
}

fun splitTimeRangeFortnightly(
    startTimestampMillis: Long,
    endTimestampMillis: Long
): List<Pair<LocalDateTime, LocalDateTime>> {

    val startTime = getLocalDateTimeFromTimestampMillis(startTimestampMillis)
    val endTime = getLocalDateTimeFromTimestampMillis(endTimestampMillis)
    val timeRanges = mutableListOf<Pair<LocalDateTime, LocalDateTime>>()

    var tempEndTime = endTime
    var tempStartTime = tempEndTime.toLocalDate().minusDays(FORTNIGHTLY_DAYS).atStartOfDay()

    while (tempStartTime.isAfter(startTime)) {
        timeRanges.add(Pair(tempStartTime, tempEndTime))
        tempEndTime = tempStartTime.minusNanos(1)
        tempStartTime = tempEndTime.toLocalDate().minusDays(FORTNIGHTLY_DAYS).atStartOfDay()
    }
    timeRanges.add(Pair(startTime, tempEndTime))
    return timeRanges.asReversed().toList()
}