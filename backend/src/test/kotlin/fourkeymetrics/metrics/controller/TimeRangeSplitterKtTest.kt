package fourkeymetrics.metrics.controller

import fourkeymetrics.metrics.model.MetricsUnit
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.time.LocalDate
import java.time.LocalTime

internal class TimeRangeSplitterKtTest {

    private var timeRangeSplitter: TimeRangeSplitter = TimeRangeSplitter()

    @Test
    fun `should get range list of 3 pair given from Sep_1 to Nov_3 when split time range monthly`() {

        val startTime = LocalDate.parse("2020-09-01").atStartOfDay()
        val startTimestamp = startTime.toDefaultZoneEpochMill()
        val endTime = LocalDate.parse("2020-11-03").atStartOfDay()
        val endTimestamp = endTime.toDefaultZoneEpochMill()

        val timeRangeList = timeRangeSplitter.split(startTimestamp, endTimestamp, MetricsUnit.Monthly)

        assertEquals(
            listOf(
                Pair(
                    startTimestamp,
                    LocalDate.parse("2020-09-30").atTime(LocalTime.MAX).toDefaultZoneEpochMill()
                ),
                Pair(
                    LocalDate.parse("2020-10-01").atStartOfDay().toDefaultZoneEpochMill(),
                    LocalDate.parse("2020-10-31").atTime(LocalTime.MAX).toDefaultZoneEpochMill()
                ),
                Pair(
                    LocalDate.parse("2020-11-01").atStartOfDay().toDefaultZoneEpochMill(),
                    endTimestamp
                )
            ), timeRangeList
        )
    }

    @Test
    fun `should get range list of 3 pair given from Jan_12 to April_2 when split time range monthly`() {

        val startTime = LocalDate.parse("2020-01-12").atStartOfDay()
        val startTimestamp = startTime.toDefaultZoneEpochMill()
        val endTime = LocalDate.parse("2020-04-02").atStartOfDay()
        val endTimestamp = endTime.toDefaultZoneEpochMill()

        val timeRangeList = timeRangeSplitter.split(startTimestamp, endTimestamp, MetricsUnit.Monthly)

        assertEquals(
            listOf(
                Pair(
                    startTimestamp,
                    LocalDate.parse("2020-01-31").atTime(LocalTime.MAX).toDefaultZoneEpochMill()
                ),
                Pair(
                    LocalDate.parse("2020-02-01").atStartOfDay().toDefaultZoneEpochMill(),
                    LocalDate.parse("2020-02-29").atTime(LocalTime.MAX).toDefaultZoneEpochMill()
                ),
                Pair(
                    LocalDate.parse("2020-03-01").atStartOfDay().toDefaultZoneEpochMill(),
                    LocalDate.parse("2020-03-31").atTime(LocalTime.MAX).toDefaultZoneEpochMill()
                ),
                Pair(
                    LocalDate.parse("2020-04-01").atStartOfDay().toDefaultZoneEpochMill(),
                    endTimestamp
                )
            ), timeRangeList
        )
    }

    @Test
    fun `should get range list of 1 pair given from Sep_1 to Sep_3 when split time range monthly`() {

        val startTime = LocalDate.parse("2020-09-01").atStartOfDay()
        val startTimestamp = startTime.toDefaultZoneEpochMill()
        val endTime = LocalDate.parse("2020-09-03").atStartOfDay()
        val endTimestamp = endTime.toDefaultZoneEpochMill()
        val timeRangeList = timeRangeSplitter.split(startTimestamp, endTimestamp, MetricsUnit.Monthly)

        assertEquals(listOf(Pair(startTimestamp, endTimestamp)), timeRangeList)
    }

    @Test
    fun `should get range list of 3 pair given Sep_1 to Sep_30 when split time range fortnightly`() {
        val startTime = LocalDate.parse("2020-09-01").atStartOfDay()
        val startTimestamp = startTime.toDefaultZoneEpochMill()
        val endTime = LocalDate.parse("2020-09-30").atStartOfDay()
        val endTimestamp = endTime.toDefaultZoneEpochMill()
        val timeRangeList = timeRangeSplitter.split(startTimestamp, endTimestamp, MetricsUnit.Fortnightly)

        assertEquals(
            listOf(
                Pair(
                    startTimestamp,
                    LocalDate.parse("2020-09-02").atTime(LocalTime.MAX).toDefaultZoneEpochMill()
                ),
                Pair(
                    LocalDate.parse("2020-09-03").atStartOfDay().toDefaultZoneEpochMill(),
                    LocalDate.parse("2020-09-16").atTime(LocalTime.MAX).toDefaultZoneEpochMill()
                ),
                Pair(
                    LocalDate.parse("2020-09-17").atStartOfDay().toDefaultZoneEpochMill(),
                    endTimestamp
                )
            ),
            timeRangeList
        )
    }

    @Test
    fun `should get range list of 1 pair given Sep_1 to Sep_3 when split time range fortnightly`() {
        val startTime = LocalDate.parse("2020-09-01").atStartOfDay()
        val startTimestamp = startTime.toDefaultZoneEpochMill()
        val endTime = LocalDate.parse("2020-09-03").atStartOfDay()
        val endTimestamp = endTime.toDefaultZoneEpochMill()
        val timeRangeList = timeRangeSplitter.split(startTimestamp, endTimestamp, MetricsUnit.Fortnightly)

        assertEquals(
            listOf(
                Pair(startTimestamp, endTimestamp),
            ),
            timeRangeList
        )
    }
}