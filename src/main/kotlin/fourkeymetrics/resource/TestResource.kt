package fourkeymetrics.resource

import fourkeymetrics.model.Build
import fourkeymetrics.repository.pipeline.Pipeline
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * This controller is for testing purpose only
 */
@RestController
class TestResource {

    @Autowired
    private lateinit var pipeline: Pipeline

    @GetMapping("/api/test/builds")
    fun pullBuilds(
        @RequestParam dashboardId: String,
        @RequestParam pipelineId: String
    ): List<Build> {
        return pipeline.fetchAllBuilds(dashboardId, pipelineId)
    }
}