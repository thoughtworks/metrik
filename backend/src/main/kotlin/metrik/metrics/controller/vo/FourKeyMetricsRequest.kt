package metrik.metrics.controller.vo

import metrik.metrics.model.MetricsUnit

data class MetricsQueryRequest(
    val startTime: Long,
    val endTime: Long,
    val unit: MetricsUnit,
    val pipelineStages: List<PipelineStageRequest>
)

data class PipelineStageRequest(val pipelineId: String, val stage: String)
