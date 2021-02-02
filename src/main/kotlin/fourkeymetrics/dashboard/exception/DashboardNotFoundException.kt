package fourkeymetrics.dashboard.exception

import fourkeymetrics.exception.ApplicationException
import org.springframework.http.HttpStatus

class DashboardNotFoundException : ApplicationException(HttpStatus.NOT_FOUND, "Dashboard not exist")