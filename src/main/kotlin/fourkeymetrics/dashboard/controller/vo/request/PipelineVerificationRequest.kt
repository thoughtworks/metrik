package fourkeymetrics.dashboard.controller.vo.request

import fourkeymetrics.dashboard.model.PipelineType

data class PipelineVerificationRequest(
    val url: String,
    val username: String,
    val credential: String,
    val type: PipelineType,
)