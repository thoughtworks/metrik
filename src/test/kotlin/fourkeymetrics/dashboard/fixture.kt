package fourkeymetrics.dashboard

import fourkeymetrics.dashboard.controller.vo.request.PipelineRequest
import fourkeymetrics.dashboard.controller.vo.request.PipelineVerificationRequest
import fourkeymetrics.dashboard.controller.vo.response.PipelineResponse
import fourkeymetrics.dashboard.model.PipelineType

fun buildPipelineRequest() =
    PipelineRequest(name = "pipeline", username = "username", credential = "credential", url = "url").copy()

fun buildPipelineResponse(): PipelineResponse = PipelineResponse(
    id = "pipelineId",
    name = "name",
    username = "username",
    credential = "credential",
    url = "url"
).copy()

fun buildPipelineVerificationRequest() = PipelineVerificationRequest(
    "url",
    "username",
    "credential",
    PipelineType.JENKINS
).copy()