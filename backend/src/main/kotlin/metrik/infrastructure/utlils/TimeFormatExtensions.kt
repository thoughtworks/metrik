package metrik.infrastructure.utlils

import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.TimeZone

fun ZonedDateTime.toTimestamp(): Long = this.toInstant().toEpochMilli()

fun LocalDateTime.toDefaultZoneEpochMill(): Long = this.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

fun LocalDateTime.atStartOfDay(): LocalDateTime = this.toLocalDate().atStartOfDay()

fun LocalDateTime.endTimeOfSameMonth(): LocalDateTime = YearMonth.from(this).atEndOfMonth().atTime(LocalTime.MAX)

fun Long.toLocalDateTime(): LocalDateTime =
    LocalDateTime.ofInstant(Instant.ofEpochMilli(this), TimeZone.getDefault().toZoneId())
