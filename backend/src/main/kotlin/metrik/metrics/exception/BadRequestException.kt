package metrik.metrics.exception

import metrik.exception.ApplicationException
import org.springframework.http.HttpStatus

class BadRequestException(message: String) : ApplicationException(HttpStatus.BAD_REQUEST, message)
