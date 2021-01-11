package fourkeymetrics.hello

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test


internal class HelloControllerTest {

    @Test
    internal fun test_hello_function_should_return_hello() {
        var helloController = HelloController()

        Assertions.assertEquals("Hello default", helloController.hello())
    }
}

