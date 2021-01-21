package fourkeymetrics.resource.metrics

import fourkeymetrics.model.MetricUnit
import fourkeymetrics.resource.metrics.representation.FourKeyMetricsResponse
import org.springframework.stereotype.Service

@Service
class MetricsApplicationService {

    fun retrieve4KeyMetrics(
        dashboardId: String,
        pipelineId: String,
        targetStage: String,
        startTimeMillis: Long,
        endTimeMillis: Long,
        unit: MetricUnit
    ): FourKeyMetricsResponse {
        TODO("Not yet implemented")
    }
}