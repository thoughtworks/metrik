package fourkeymetrics

import org.mockito.ArgumentMatcher
import org.mockito.Mockito

object MockitoHelper {
    fun <T> anyObject(): T {
        Mockito.any<T>()
        return uninitialized()
    }

    fun <T> argThat(matcher: ArgumentMatcher<T>): T {
        Mockito.argThat(matcher)
        return uninitialized()
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> uninitialized(): T = null as T
}