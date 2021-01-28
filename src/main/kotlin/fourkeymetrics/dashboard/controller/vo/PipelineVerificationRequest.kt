package fourkeymetrics.dashboard.controller.vo

import fourkeymetrics.dashboard.model.PipelineType

data class PipelineVerificationRequest(
    val type: PipelineType,
    val url: String,
    val username: String,
    val credential: String,
)