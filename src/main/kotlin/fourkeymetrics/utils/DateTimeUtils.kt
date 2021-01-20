package fourkeymetrics.utils

import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.temporal.ChronoUnit
import java.util.*

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
                tempStartTime = it.toLocalDate().plusDays(1L).atStartOfDay()
            }
        timeRanges.add(Pair(tempStartTime, endTime))
        timeRanges.toList()
    } else {
        listOf(Pair(startTime, endTime))
    }
}