package fourkeymetrics.project.exception

import fourkeymetrics.exception.ApplicationException
import org.springframework.http.HttpStatus

class ProjectNotFoundException : ApplicationException(HttpStatus.NOT_FOUND, "Project not exist")