package metrik.project.exception

import metrik.exception.ApplicationException
import org.springframework.http.HttpStatus

class SynchronizationException(message: String) : ApplicationException(
    HttpStatus.INTERNAL_SERVER_ERROR, message
)
