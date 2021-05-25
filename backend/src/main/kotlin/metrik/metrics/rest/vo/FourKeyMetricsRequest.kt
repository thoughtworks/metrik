package metrik.metrics.rest.vo

import metrik.metrics.domain.model.MetricsUnit

data class MetricsQueryRequest(
    val startTime: Long,
    val endTime: Long,
    val unit: MetricsUnit,
    val pipelineStages: List<PipelineStageRequest>
)

data class PipelineStageRequest(val pipelineId: String, val stage: String)
