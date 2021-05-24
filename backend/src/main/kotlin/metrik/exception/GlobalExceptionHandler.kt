package metrik.exception

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

@RestControllerAdvice
class GlobalExceptionHandler {
    private var logger = LoggerFactory.getLogger(this.javaClass.name)

    @ExceptionHandler(ApplicationException::class)
    fun handleAppException(ex: ApplicationException): ResponseEntity<ErrorResponse> {
        return handlerErrorResponse(ex.httpStatus, ex.message, ex)
    }

    @ExceptionHandler(Throwable::class)
    fun handleThrowable(ex: Throwable): ResponseEntity<ErrorResponse> {
        return handlerErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.message, ex)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(ex: HttpMessageNotReadableException): ResponseEntity<ErrorResponse> {
        return handlerErrorResponse(HttpStatus.BAD_REQUEST, "Invalid request body", ex)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val message = ex.allErrors.joinToString("; ") { "Field ${it.defaultMessage}" }
        return handlerErrorResponse(HttpStatus.BAD_REQUEST, message, ex)
    }

    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleParamMissingException(ex: MissingServletRequestParameterException): ResponseEntity<ErrorResponse> {
        return handlerErrorResponse(HttpStatus.BAD_REQUEST, ex.message, ex)
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleParamIllegalException(ex: MethodArgumentTypeMismatchException): ResponseEntity<ErrorResponse> {
        val message = "argument type mismatch"
        return handlerErrorResponse(HttpStatus.BAD_REQUEST, message, ex)
    }

    private fun handlerErrorResponse(
        httpStatus: HttpStatus,
        message: String?,
        ex: Throwable
    ): ResponseEntity<ErrorResponse> {
        logger.error("Unexpected exception happened with error message: $message", ex)
        val errorResponse = ErrorResponse(httpStatus.value(), message)
        return ResponseEntity(errorResponse, httpStatus)
    }
}