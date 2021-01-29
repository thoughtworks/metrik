package fourkeymetrics.dashboard.exception

import fourkeymetrics.exception.ApplicationException
import org.springframework.http.HttpStatus

class PipelineNotFoundException(httpStatus: HttpStatus, message: String) : ApplicationException(httpStatus, message)