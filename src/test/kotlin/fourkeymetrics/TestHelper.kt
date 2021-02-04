package fourkeymetrics

import org.mockito.Mockito

class TestHelper {
    companion object {
        fun <T> any(type: Class<T>): T = Mockito.any<T>(type)
    }
}