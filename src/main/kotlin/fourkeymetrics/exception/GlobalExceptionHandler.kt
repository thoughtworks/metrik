package fourkeymetrics.exception

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

@RestControllerAdvice
class GlobalExceptionHandler {
    private var logger = LoggerFactory.getLogger(this.javaClass.name)

    @ExceptionHandler(ApplicationException::class)
    fun handleAppException(ex: ApplicationException): ResponseEntity<ErrorResponse> {
        logger.error("Application exception happened with error message: ${ex.message}", ex)
        val httpStatus = ex.httpStatus
        val errorResponse = ErrorResponse(httpStatus.value(), ex.message)
        return ResponseEntity(errorResponse, httpStatus)
    }

    @ExceptionHandler(Throwable::class)
    fun handleThrowable(ex: Throwable): ResponseEntity<ErrorResponse> {
        logger.error("Unexpected exception happened with error message: ${ex.message}", ex)
        val httpStatus = HttpStatus.INTERNAL_SERVER_ERROR
        val errorResponse = ErrorResponse(httpStatus.value(), ex.message)
        return ResponseEntity(errorResponse, httpStatus)
    }

    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleParamMissingException(ex: MissingServletRequestParameterException): ResponseEntity<ErrorResponse>{
        logger.error("Unexpected exception happened with error message: ${ex.message}", ex)
        val httpStatus = HttpStatus.BAD_REQUEST
        val errorResponse = ErrorResponse(httpStatus.value(), ex.message)
        return ResponseEntity(errorResponse, httpStatus)
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleParamIllegalException(ex: MethodArgumentTypeMismatchException): ResponseEntity<ErrorResponse>{
        logger.error("Unexpected exception happened with error message: ${ex.message}", ex)
        val httpStatus = HttpStatus.BAD_REQUEST

        val errorResponse = ErrorResponse(httpStatus.value(), "argument type mismatch")
        return ResponseEntity(errorResponse, httpStatus)
    }
}