package fourkeymetrics.exception

import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice


@RestControllerAdvice
class GlobalExceptionHandler {
    private var logger = KotlinLogging.logger(this.javaClass.name)

    @ExceptionHandler(ApplicationException::class, DashboardNotFoundException::class)
    fun handleAppException(ex: ApplicationException): ResponseEntity<ErrorResponse> {
        logger.error(ex) { "Application exception happened with error message: ${ex.message}" }
        val httpStatus = ex.httpStatus
        val errorResponse = ErrorResponse(httpStatus.value(), ex.message)
        return ResponseEntity(errorResponse, httpStatus)
    }

    @ExceptionHandler(Throwable::class)
    fun handleThrowable(ex: Throwable): ResponseEntity<ErrorResponse> {
        logger.error(ex) { "Unexpected exception happened with error message: ${ex.message}" }
        val httpStatus = HttpStatus.INTERNAL_SERVER_ERROR
        val errorResponse = ErrorResponse(httpStatus.value(), ex.message)
        return ResponseEntity(errorResponse, httpStatus)
    }
}