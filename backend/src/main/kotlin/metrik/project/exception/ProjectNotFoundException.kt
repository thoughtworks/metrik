package metrik.project.exception

import metrik.exception.ApplicationException
import org.springframework.http.HttpStatus

class ProjectNotFoundException : ApplicationException(HttpStatus.NOT_FOUND, "Project not exist")