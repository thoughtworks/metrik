package fourkeymetrics.exception

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

internal class GlobalExceptionHandlerTest {
    lateinit var globalExceptionHandler: GlobalExceptionHandler

    @BeforeEach
    internal fun setUp() {
        globalExceptionHandler = GlobalExceptionHandler();
    }

    @Test
    internal fun should_handle_application_exception() {
        val responseEntity = globalExceptionHandler.handleAppException(ApplicationException(HttpStatus.BAD_REQUEST, "invalid request"))

        val errorResponse = responseEntity.body

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.statusCode)
        Assertions.assertEquals(400, errorResponse?.status)
        Assertions.assertEquals("invalid request", errorResponse?.message)
    }
}