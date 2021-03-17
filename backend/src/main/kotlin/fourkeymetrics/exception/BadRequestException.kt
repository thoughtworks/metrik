package fourkeymetrics.exception

import org.springframework.http.HttpStatus

class BadRequestException(message: String): ApplicationException(HttpStatus.BAD_REQUEST, message)
