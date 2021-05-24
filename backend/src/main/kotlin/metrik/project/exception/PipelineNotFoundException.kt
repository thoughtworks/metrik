package metrik.project.exception

import metrik.exception.ApplicationException
import org.springframework.http.HttpStatus

class PipelineNotFoundException : ApplicationException(HttpStatus.NOT_FOUND, "Pipeline not exist")