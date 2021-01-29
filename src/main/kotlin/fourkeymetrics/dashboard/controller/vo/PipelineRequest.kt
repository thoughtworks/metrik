package fourkeymetrics.dashboard.controller.vo

import fourkeymetrics.dashboard.model.PipelineType

data class PipelineRequest(
    val name: String,
    val url: String,
    val username: String,
    val credential: String,
    val type: PipelineType,
)