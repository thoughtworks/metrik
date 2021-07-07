package metrik.project.domain.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class StageTest {
    @Test
    internal fun `should return completedTime when stage contains completedTime`() {
        assertEquals(
            1610700490630,
            Stage(
                startTimeMillis = 1610700490500,
                durationMillis = 129,
                pauseDurationMillis = 1,
                completedTimeMillis = 1610700490630
            ).getStageDoneTime()
        )
    }

    @Test
    internal fun `should return sum of startTime and durationTime and pauseTime given stage does not contain completedTime`() {
        assertEquals(
            1610700490630,
            Stage(
                startTimeMillis = 1610700490500,
                durationMillis = 129,
                pauseDurationMillis = 1,
                completedTimeMillis = null
            ).getStageDoneTime()
        )
    }
}