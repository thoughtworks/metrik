package fourkeymetrics.dashboard.controller

import fourkeymetrics.dashboard.controller.vo.DashboardRequest
import fourkeymetrics.dashboard.controller.vo.PipelineRequest
import fourkeymetrics.dashboard.controller.vo.PipelineStagesResponse
import fourkeymetrics.dashboard.controller.vo.PipelineVerificationRequest
import fourkeymetrics.dashboard.model.Dashboard
import fourkeymetrics.dashboard.model.Pipeline
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.GetMapping

@RestController
class DashboardController {

    @Autowired
    private lateinit var dashboardApplicationService: DashboardApplicationService

    @PostMapping("/api/pipeline/verify")
    fun verifyPipeline(@RequestBody pipelineVerificationRequest: PipelineVerificationRequest) {
        with(pipelineVerificationRequest) {
            dashboardApplicationService.verifyPipeline(url, username, credential, type.name)
        }
    }

    //TODO @GetMapping("/api/dashboards/{id}")

    @PostMapping("api/pipeline/config")
    fun save(@RequestBody config: DashboardRequest): Dashboard {
        return dashboardApplicationService.save(config)
    }

    @PutMapping("api/dashboard/{dashboardId}/pipeline/{pipelineId}/config/")
    fun update(
        @RequestBody config: PipelineRequest,
        @PathVariable("dashboardId") dashboardId: String,
        @PathVariable("pipelineId") pipelineId: String
    ): Pipeline {
        return dashboardApplicationService.update(config, dashboardId, pipelineId)
    }

    @GetMapping("/api/dashboards/{dashboardId}/pipeline/stage")
    fun getPipelineStages(@PathVariable("dashboardId") dashboardId: String): List<PipelineStagesResponse> {
        return dashboardApplicationService.getPipelineStages(dashboardId)
    }
}