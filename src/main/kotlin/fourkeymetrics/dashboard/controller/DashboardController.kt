package fourkeymetrics.dashboard.controller

import fourkeymetrics.dashboard.controller.applicationservice.DashboardApplicationService
import fourkeymetrics.dashboard.controller.vo.request.DashboardRequest
import fourkeymetrics.dashboard.controller.vo.response.DashboardDetailResponse
import fourkeymetrics.dashboard.controller.vo.response.DashboardResponse
import fourkeymetrics.dashboard.controller.vo.response.PipelineStagesResponse
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
import javax.validation.Valid
import javax.validation.constraints.NotBlank

@RestController
@RequestMapping("/api")
class DashboardController {
    @Autowired
    private lateinit var dashboardApplicationService: DashboardApplicationService


    @GetMapping("/dashboard")
    @ResponseStatus(HttpStatus.OK)
    fun getDashboards(): List<DashboardResponse> {
        return dashboardApplicationService.getDashboards()
    }

    @GetMapping("/dashboard/{dashboardId}")
    @ResponseStatus(HttpStatus.OK)
    fun getDashboardDetails(@PathVariable dashboardId: String): DashboardDetailResponse {
        return dashboardApplicationService.getDashboardDetails(dashboardId)
    }

    @PostMapping("/dashboard")
    fun createDashboard(@RequestBody @Valid dashboardRequest: DashboardRequest): DashboardDetailResponse {
        return dashboardApplicationService.createDashboard(dashboardRequest)
    }

    @PutMapping("/dashboard/{dashboardId}")
    @ResponseStatus(HttpStatus.OK)
    fun updateDashboardName(@PathVariable dashboardId: String, @RequestBody @Valid @NotBlank dashboardName: String): DashboardResponse {
        return dashboardApplicationService.updateDashboardName(dashboardId, dashboardName)
    }

    @DeleteMapping("/dashboard/{dashboardId}")
    @ResponseStatus(HttpStatus.OK)
    fun deleteDashboard(@PathVariable dashboardId: String) {
        return dashboardApplicationService.deleteDashboard(dashboardId)
    }

    @GetMapping("/dashboard/{dashboardId}/pipelines-stages")
    fun getPipelineStages(@PathVariable("dashboardId") dashboardId: String): List<PipelineStagesResponse> {
        return dashboardApplicationService.getPipelineStages(dashboardId)
    }
}