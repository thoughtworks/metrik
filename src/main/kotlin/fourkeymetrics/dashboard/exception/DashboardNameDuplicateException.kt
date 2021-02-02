package fourkeymetrics.dashboard.exception

import fourkeymetrics.exception.ApplicationException
import org.springframework.http.HttpStatus

class DashboardNameDuplicateException : ApplicationException(HttpStatus.BAD_REQUEST, "Dashboard name duplicate")