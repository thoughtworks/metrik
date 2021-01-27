package fourkeymetrics.dashboard.controller


import fourkeymetrics.common.model.Build
import fourkeymetrics.dashboard.repository.BuildRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

/**
 * This controller is for testing purpose only
 */
@RestController
class TestController {

    @Autowired
    private lateinit var buildRepository: BuildRepository

    @GetMapping("/api/{pipelineId}/builds")
    fun pullBuilds(
        @PathVariable pipelineId: String
    ): List<Build> {
        return buildRepository.getAllBuilds(pipelineId)
    }
}