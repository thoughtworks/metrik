package metrik.infrastructure.utlils

import java.time.ZonedDateTime

object TimeFormatUtil {
    fun mapDateToTimeStamp(date: ZonedDateTime): Long {
        return date.toInstant().toEpochMilli()
    }
}