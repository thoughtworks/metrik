package fourkeymetrics.exception

import org.springframework.http.HttpStatus

class DashboardNotFoundException(message: String): ApplicationException(HttpStatus.NOT_FOUND, message)
