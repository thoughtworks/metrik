package metrik.metrics.rest

import metrik.metrics.rest.vo.FourKeyMetricsResponse
import metrik.metrics.rest.vo.MetricsQueryRequest
import metrik.metrics.rest.vo.PipelineStageRequest
import metrik.metrics.domain.model.MetricsUnit
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
class MetricsController {
    @Autowired
    private lateinit var metricsApplicationService: MetricsApplicationService

    @PostMapping("/api/pipeline/metrics")
    fun getFourKeyMetrics(@RequestBody metricsQueryRequest: MetricsQueryRequest): FourKeyMetricsResponse {
        return metricsApplicationService.retrieve4KeyMetrics(
            metricsQueryRequest.pipelineStages,
            metricsQueryRequest.startTime,
            metricsQueryRequest.endTime,
            metricsQueryRequest.unit,
        )
    }

    @GetMapping("/api/pipeline/metrics")
    fun getFourKeyMetrics(
        @RequestParam pipelineId: String,
        @RequestParam targetStage: String,
        @RequestParam startTime: Long,
        @RequestParam endTime: Long,
        @RequestParam unit: MetricsUnit
    ): FourKeyMetricsResponse {
        return metricsApplicationService.retrieve4KeyMetrics(
            listOf(PipelineStageRequest(pipelineId, targetStage)),
            startTime,
            endTime,
            unit
        )
    }
}
