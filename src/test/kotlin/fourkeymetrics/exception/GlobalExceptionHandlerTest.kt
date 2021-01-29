package fourkeymetrics.exception

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.web.bind.MissingServletRequestParameterException

internal class GlobalExceptionHandlerTest {
    lateinit var globalExceptionHandler: GlobalExceptionHandler

    @BeforeEach
    internal fun setUp() {
        globalExceptionHandler = GlobalExceptionHandler()
    }

    @Test
    internal fun `should handle application exception`() {
        val responseEntity = globalExceptionHandler.handleAppException(ApplicationException(HttpStatus.BAD_REQUEST, "invalid request"))

        val errorResponse = responseEntity.body

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.statusCode)
        Assertions.assertEquals(400, errorResponse?.status)
        Assertions.assertEquals("invalid request", errorResponse?.message)
    }

    @Test
    internal fun `should handle unexpected exception`() {
        val responseEntity = globalExceptionHandler.handleThrowable(OutOfMemoryError("error message"))

        val errorResponse = responseEntity.body

        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.statusCode)
        Assertions.assertEquals(500, errorResponse?.status)
        Assertions.assertEquals("error message", errorResponse?.message)
    }

    @Test
    internal fun `should handle parameter missing exception`() {
        val responseEntity = globalExceptionHandler.handleParamMissingException(
            MissingServletRequestParameterException("pipelineId", "String")
        )

        val errorResponse = responseEntity.body

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.statusCode)
        Assertions.assertEquals(400, errorResponse?.status)
        Assertions.assertEquals("Required String parameter 'pipelineId' is not present", errorResponse?.message)
    }
}