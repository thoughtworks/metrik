package metrik

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class FirstApiTestExample : ApiTestBase() {
    @Test
    fun `good first sample test case`() {
        assertEquals("expect to pass", "expect to pass")
    }
}