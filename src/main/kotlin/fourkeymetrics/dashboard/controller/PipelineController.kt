package fourkeymetrics.dashboard.controller

import fourkeymetrics.dashboard.controller.applicationservice.PipelineApplicationService
import fourkeymetrics.dashboard.controller.vo.request.PipelineRequest
import fourkeymetrics.dashboard.controller.vo.request.PipelineVerificationRequest
import fourkeymetrics.dashboard.controller.vo.response.PipelineResponse
import org.bson.types.ObjectId
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

@RestController
@RequestMapping("/api")
class PipelineController {
    @Autowired
    private lateinit var pipelineApplicationService: PipelineApplicationService

    @PostMapping("/pipeline/verify")
    @ResponseStatus(HttpStatus.OK)
    fun verifyPipeline(@RequestBody @Valid pipelineVerificationRequest: PipelineVerificationRequest) {
        pipelineApplicationService.verifyPipelineConfiguration(pipelineVerificationRequest.toPipeline())
    }

    @PostMapping("/dashboard/{dashboardId}/pipeline")
    @ResponseStatus(HttpStatus.CREATED)
    fun createPipeline(
        @PathVariable("dashboardId") dashboardId: String,
        @RequestBody @Valid pipelineRequest: PipelineRequest
    ): PipelineResponse {
        return PipelineResponse(
            pipelineApplicationService.createPipeline(
                pipelineRequest.toPipeline(
                    dashboardId,
                    ObjectId().toString()
                )
            )
        )
    }

    @PutMapping("/dashboard/{dashboardId}/pipeline/{pipelineId}")
    fun updatePipeline(
        @PathVariable("dashboardId") dashboardId: String,
        @PathVariable("pipelineId") pipelineId: String,
        @RequestBody @Valid pipelineRequest: PipelineRequest
    ): PipelineResponse {
        return PipelineResponse(
            pipelineApplicationService.updatePipeline(
                pipelineRequest.toPipeline(
                    dashboardId,
                    pipelineId
                )
            )
        )
    }

    @GetMapping("/dashboard/{dashboardId}/pipeline/{pipelineId}")
    fun getPipeline(
        @PathVariable("dashboardId") dashboardId: String,
        @PathVariable("pipelineId") pipelineId: String
    ): PipelineResponse {
        return PipelineResponse(pipelineApplicationService.getPipeline(dashboardId, pipelineId))
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