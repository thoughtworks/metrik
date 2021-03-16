package fourkeymetrics.common.utlils

import fourkeymetrics.common.utlils.TimeFormatUtil.mapDateToTimeStamp
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.ZoneId
import java.time.ZonedDateTime

internal class TimeFormatUtilTest {
    @Test
    internal fun `should convert zonedDateTime to timestamp`() {
        val date: ZonedDateTime = ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC"))
        assertEquals(1514764800000, mapDateToTimeStamp(date))
    }

    @Test
    internal fun `should return null when zonedDateTime is null`() {
        val date : ZonedDateTime? = null
        assertNull(mapDateToTimeStamp(date))
    }
}

