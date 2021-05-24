package metrik.project.exception

import metrik.exception.ApplicationException
import org.springframework.http.HttpStatus

class ProjectNameDuplicateException : ApplicationException(HttpStatus.BAD_REQUEST, "Project name duplicate")