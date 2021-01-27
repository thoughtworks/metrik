package fourkeymetrics.dashboard.controller


import fourkeymetrics.common.model.Build
import fourkeymetrics.dashboard.service.jenkins.JenkinsPipelineService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

/**
 * This controller is for testing purpose only
 */
@RestController
class TestController {

    @Autowired
    private lateinit var jenkinsPipelineService: JenkinsPipelineService

    @PostMapping("/api/dashboard/{dashboardId}/pipeline/{pipelineId}/builds")
    fun pullBuilds(
        @PathVariable dashboardId: String,
        @PathVariable pipelineId: String
    ): List<Build> {
        return jenkinsPipelineService.syncBuilds(dashboardId, pipelineId)
    }
}