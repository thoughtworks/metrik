package fourkeymetrics.metrics.controller

import fourkeymetrics.metrics.model.MetricsUnit
import fourkeymetrics.metrics.controller.vo.FourKeyMetricsResponse
import fourkeymetrics.metrics.controller.vo.PipelineStageRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
class MetricsController {
    @Autowired
    private lateinit var metricsApplicationService: MetricsApplicationService

    @GetMapping("/api/pipeline/metrics")
    fun getFourKeyMetrics(
        @RequestParam startTime: Long,
        @RequestParam endTime: Long,
        @RequestParam unit: MetricsUnit,
        @RequestBody pipelineStages: List<PipelineStageRequest>
    ): FourKeyMetricsResponse {
        return metricsApplicationService.retrieve4KeyMetrics(
            pipelineStages,
            startTime,
            endTime,
            unit,
        )
    }
}
