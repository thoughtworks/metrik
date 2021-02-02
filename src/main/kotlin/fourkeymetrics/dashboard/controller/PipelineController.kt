package fourkeymetrics.dashboard.controller

import fourkeymetrics.dashboard.controller.applicationservice.PipelineApplicationService
import fourkeymetrics.dashboard.controller.vo.PipelineVerificationRequest
import fourkeymetrics.dashboard.controller.vo.PipelineVo
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
class PipelineController {
    @Autowired
    private lateinit var pipelineApplicationService: PipelineApplicationService


    @PostMapping("/pipeline/verify")
    @ResponseStatus(HttpStatus.OK)
    fun verifyPipeline(@RequestBody pipelineVerificationRequest: PipelineVerificationRequest) {
        pipelineApplicationService.verifyPipeline(pipelineVerificationRequest)
    }

    @PostMapping("/dashboard/{dashboardId}/pipeline")
    fun createPipeline(
        @PathVariable("dashboardId") dashboardId: String,
        @RequestBody pipeline: PipelineVo
    ): PipelineVo {
        return pipelineApplicationService.createPipeline(dashboardId, pipeline)
    }

    @PutMapping("/dashboard/{dashboardId}/pipeline/{pipelineId}")
    fun updatePipeline(
        @PathVariable("dashboardId") dashboardId: String,
        @PathVariable("pipelineId") pipelineId: String,
        @RequestBody pipeline: PipelineVo
    ): PipelineVo {
        return pipelineApplicationService.updatePipeline(dashboardId, pipelineId, pipeline)
    }

    @GetMapping("/dashboard/{dashboardId}/pipeline/{pipelineId}")
    fun getPipeline(
        @PathVariable("dashboardId") dashboardId: String,
        @PathVariable("pipelineId") pipelineId: String
    ): PipelineVo {
        return pipelineApplicationService.getPipeline(dashboardId, pipelineId)
    }

    @DeleteMapping("/dashboard/{dashboardId}/pipeline/{pipelineId}")
    @ResponseStatus(HttpStatus.OK)
    fun deletePipeline(
        @PathVariable("dashboardId") dashboardId: String,
        @PathVariable("pipelineId") pipelineId: String
    ) {
        pipelineApplicationService.deletePipeline(dashboardId, pipelineId)
    }
}