package fourkeymetrics.exception

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice


@RestControllerAdvice
class GlobalExceptionHandler {


    @ExceptionHandler(ApplicationException::class)
    fun handleAppException(ex: ApplicationException): ResponseEntity<ErrorResponse> {
        val message= ex.message
        val httpStatus = ex.httpStatus
        val errorResponse = ErrorResponse(httpStatus.value(), message)
        return ResponseEntity(errorResponse, httpStatus)
    }
}