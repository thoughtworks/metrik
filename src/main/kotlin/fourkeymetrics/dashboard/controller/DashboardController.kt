package fourkeymetrics.dashboard.controller

import fourkeymetrics.dashboard.model.Dashboard
import fourkeymetrics.dashboard.model.Pipeline
import fourkeymetrics.dashboard.controller.vo.DashboardRequest
import fourkeymetrics.dashboard.controller.vo.PipelineRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.PathVariable


@RestController
class DashboardController {

    @Autowired
    private lateinit var dashboardApplicationService: DashboardApplicationService

    @GetMapping("/api/pipeline/verify")
    fun verifyPipeline(
        @RequestParam url: String,
        @RequestParam username: String,
        @RequestParam credential: String,
        @RequestParam type: String
    ) {
        dashboardApplicationService.verifyPipeline(url, username, credential, type)
    }

    @GetMapping("/api/dashboards/{id}")


    @PostMapping("api/pipeline/config")
    fun save(@RequestBody config: DashboardRequest): Dashboard {
        return dashboardApplicationService.save(config)
    }

    @PutMapping("api/dashboard/{dashboardId}/pipeline/{pipelineId}/config/")
    fun update(@RequestBody config: PipelineRequest,
               @PathVariable("dashboardId") dashboardId:String,
               @PathVariable("pipelineId") pipelineId:String ): Pipeline {
      return  dashboardApplicationService.update(config,dashboardId,pipelineId)
    }

}