package fourkeymetrics.resource.metrics

import fourkeymetrics.model.MetricUnit
import fourkeymetrics.resource.metrics.representation.FourKeyMetricsResponse
import org.springframework.stereotype.Service

@Service
class MetricsApplicationService {

    fun retrieve4KeyMetrics(
        pipelineId: String,
        targetStage: String,
        startTime: Long,
        endTime: Long,
        unit: MetricUnit
    ): FourKeyMetricsResponse {
        TODO("Not yet implemented")
    }
}