package fourkeymetrics.utils

import java.time.Instant
import java.time.LocalDateTime
import java.util.*

fun getLocalDateTimeFromTimestampSec(timestamp: Long): LocalDateTime {
    return LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), TimeZone.getDefault().toZoneId())
}

fun getLocalDateTimeFromTimestampMillis(timestamp: Long): LocalDateTime {
    return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), TimeZone.getDefault().toZoneId())
}