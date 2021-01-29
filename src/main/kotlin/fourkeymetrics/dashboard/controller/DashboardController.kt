package fourkeymetrics.dashboard.controller

import fourkeymetrics.dashboard.controller.vo.PipelineRequest
import fourkeymetrics.dashboard.controller.vo.PipelineStagesResponse
import fourkeymetrics.dashboard.controller.vo.PipelineVerificationRequest
import fourkeymetrics.dashboard.model.Dashboard
import fourkeymetrics.dashboard.model.Pipeline
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class DashboardController {
    @Autowired
    private lateinit var dashboardApplicationService: DashboardApplicationService

    @PostMapping("/dashboard")
    fun createDashboard(@RequestBody dashboardName: String): Dashboard {
        return dashboardApplicationService.createDashboard(dashboardName)
    }

    @PutMapping("/dashboard/{dashboardId}")
    @ResponseStatus(HttpStatus.OK)
    fun updateDashboardName(@PathVariable dashboardId: String, @RequestBody dashboardName: String): Dashboard {
        return dashboardApplicationService.updateDashboardName(dashboardId, dashboardName)
    }

    @DeleteMapping("/dashboard/{dashboardId}")
    @ResponseStatus(HttpStatus.OK)
    fun deleteDashboard(@PathVariable dashboardId: String) {
        return dashboardApplicationService.deleteDashboard(dashboardId)
    }

    @PostMapping("/pipeline/verify")
    @ResponseStatus(HttpStatus.OK)
    fun verifyPipeline(@RequestBody pipelineVerificationRequest: PipelineVerificationRequest) {
        dashboardApplicationService.verifyPipeline(pipelineVerificationRequest)
    }

    @PostMapping("/dashboard/{dashboardId}/pipeline")
    fun createPipeline(
        @PathVariable("dashboardId") dashboardId: String,
        @RequestBody pipelineRequest: PipelineRequest
    ): Dashboard {
        return dashboardApplicationService.createPipeline(dashboardId, pipelineRequest)
    }

    @PutMapping("/dashboard/{dashboardId}/pipeline/{pipelineId}")
    fun updatePipeline(
        @PathVariable("dashboardId") dashboardId: String,
        @PathVariable("pipelineId") pipelineId: String,
        @RequestBody pipelineRequest: PipelineRequest
    ): Pipeline {
        return dashboardApplicationService.updatePipeline(dashboardId, pipelineId, pipelineRequest)
    }

    @GetMapping("/dashboard/{dashboardId}/pipeline/{pipelineId}")
    fun getPipeline(
        @PathVariable("dashboardId") dashboardId: String,
        @PathVariable("pipelineId") pipelineId: String
    ): Pipeline {
        return dashboardApplicationService.getPipeline(dashboardId, pipelineId)
    }

    @DeleteMapping("/dashboard/{dashboardId}/pipeline/{pipelineId}")
    @ResponseStatus(HttpStatus.OK)
    fun deletePipeline(
        @PathVariable("dashboardId") dashboardId: String,
        @PathVariable("pipelineId") pipelineId: String
    ) {
        dashboardApplicationService.deletePipeline(dashboardId, pipelineId)
    }

    @GetMapping("/dashboard/{dashboardId}/stage")
    fun getPipelineStages(@PathVariable("dashboardId") dashboardId: String): List<PipelineStagesResponse> {
        return dashboardApplicationService.getPipelineStages(dashboardId)
    }
}