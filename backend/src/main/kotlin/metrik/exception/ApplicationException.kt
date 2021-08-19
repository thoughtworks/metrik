package metrik.exception

import org.springframework.http.HttpStatus

open class ApplicationException(val httpStatus: HttpStatus, message: String) : RuntimeException(message)
