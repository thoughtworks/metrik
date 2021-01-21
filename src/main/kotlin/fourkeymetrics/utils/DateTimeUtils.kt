package fourkeymetrics.utils

import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*

internal const val FORTNIGHTLY_DAYS = 13L

fun getLocalDateTimeFromTimestampMillis(timestamp: Long): LocalDateTime {
    return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), TimeZone.getDefault().toZoneId())
}

fun splitTimeRangeMonthly(
    startTimestampMillis: Long,
    endTimestampMillis: Long
): List<Pair<Long, Long>> {
    val startTime = getLocalDateTimeFromTimestampMillis(startTimestampMillis).atZone(ZoneId.systemDefault())
    val endTime = getLocalDateTimeFromTimestampMillis(endTimestampMillis).atZone(ZoneId.systemDefault())
    val months = ChronoUnit.MONTHS.between(startTime, endTime)
    return if (months >= 1) {
        val timeRanges = mutableListOf<Pair<Long, Long>>()
        var tempStartTime = startTime
        LongRange(0, months - 1)
            .map { YearMonth.from(startTime).plusMonths(it) }
            .map { it.atEndOfMonth().atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()) }
            .forEach {
                timeRanges.add(Pair(tempStartTime.toInstant().toEpochMilli(), it.toInstant().toEpochMilli()))
                tempStartTime = it.plusNanos(1)
            }
        timeRanges.add(Pair(tempStartTime.toInstant().toEpochMilli(), endTimestampMillis))
        timeRanges.toList()
    } else {
        listOf(Pair(startTime.toInstant().toEpochMilli(), endTime.toInstant().toEpochMilli()))
    }
}

fun splitTimeRangeFortnightly(
    startTimestampMillis: Long,
    endTimestampMillis: Long
): List<Pair<Long, Long>> {

    val startTime = getLocalDateTimeFromTimestampMillis(startTimestampMillis).atZone(ZoneId.systemDefault())
    val endTime = getLocalDateTimeFromTimestampMillis(endTimestampMillis).atZone(ZoneId.systemDefault())
    val timeRanges = mutableListOf<Pair<Long, Long>>()

    var tempEndTime = endTime
    var tempStartTime = tempEndTime.toLocalDate().minusDays(FORTNIGHTLY_DAYS)
        .atStartOfDay().atZone(ZoneId.systemDefault())

    while (tempStartTime.isAfter(startTime)) {
        timeRanges.add(Pair(tempStartTime.toInstant().toEpochMilli(), tempEndTime.toInstant().toEpochMilli()))
        tempEndTime = tempStartTime.minusNanos(1)
        tempStartTime = tempEndTime.toLocalDate().minusDays(FORTNIGHTLY_DAYS)
            .atStartOfDay().atZone(ZoneId.systemDefault())
    }
    timeRanges.add(Pair(startTimestampMillis, tempEndTime.toInstant().toEpochMilli()))
    return timeRanges.asReversed().toList()
}