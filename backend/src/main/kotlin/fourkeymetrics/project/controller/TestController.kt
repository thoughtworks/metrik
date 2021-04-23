package fourkeymetrics.project.controller


import fourkeymetrics.common.model.Build
import fourkeymetrics.project.service.jenkins.JenkinsPipelineService
import fourkeymetrics.exception.ApplicationException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

/**
 * This controller is for testing purpose only
 */
@RestController
class TestController {
    private var logger = LoggerFactory.getLogger(this::class.java)

    @Autowired
    private lateinit var jenkinsPipelineService: JenkinsPipelineService

    @PostMapping("/api/project/{projectId}/pipeline/{pipelineId}/builds")
    fun pullBuilds(
        @PathVariable projectId: String,
        @PathVariable pipelineId: String
    ): List<Build> {
        return jenkinsPipelineService.syncBuilds(pipelineId)
    }

    @GetMapping("/test")
    fun test(): String {
        logger.info("message 1")
        logger.info("message 2")
        logger.info("message 3")
        throw ApplicationException(HttpStatus.INTERNAL_SERVER_ERROR, "error happened")
    }
}