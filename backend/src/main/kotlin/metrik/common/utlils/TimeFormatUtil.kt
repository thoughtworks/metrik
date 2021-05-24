package metrik.common.utlils

import java.time.ZonedDateTime

object TimeFormatUtil {
    fun mapDateToTimeStamp(date: ZonedDateTime): Long {
        return date.toInstant().toEpochMilli()
    }
}