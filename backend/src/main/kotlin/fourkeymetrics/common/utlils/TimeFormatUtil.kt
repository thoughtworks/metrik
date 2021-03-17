package fourkeymetrics.common.utlils

import java.time.ZonedDateTime

object TimeFormatUtil {
    fun mapDateToTimeStamp(date: ZonedDateTime?): Long? {
        if (date == null) {
            return null
        }
        return date.toInstant().toEpochMilli()
    }
}