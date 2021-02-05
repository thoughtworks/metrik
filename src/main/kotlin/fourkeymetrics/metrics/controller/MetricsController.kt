package fourkeymetrics.metrics.controller

import fourkeymetrics.metrics.controller.vo.FourKeyMetricsResponse
import fourkeymetrics.metrics.controller.vo.MetricsQueryRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
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
}
