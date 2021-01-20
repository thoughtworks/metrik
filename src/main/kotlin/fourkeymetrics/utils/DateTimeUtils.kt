package fourkeymetrics.utils

import java.time.Instant
import java.time.LocalDateTime
import java.util.*

fun getLocalDateTimeFromTimestampMillis(timestamp: Long): LocalDateTime {
    return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), TimeZone.getDefault().toZoneId())
}