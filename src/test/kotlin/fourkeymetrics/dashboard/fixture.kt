package fourkeymetrics.dashboard

import fourkeymetrics.dashboard.controller.vo.request.PipelineRequest
import fourkeymetrics.dashboard.controller.vo.request.PipelineVerificationRequest
import fourkeymetrics.dashboard.controller.vo.response.PipelineResponse
import fourkeymetrics.dashboard.model.Pipeline
import fourkeymetrics.dashboard.model.PipelineType

fun buildPipelineRequest() =
    PipelineRequest(
        name = "pipeline",
        username = "username",
        credential = "credential",
        url = "url",
        PipelineType.JENKINS.toString()
    ).copy()

fun buildPipelineResponse() = PipelineResponse(
    id = "pipelineId",
    name = "name",
    username = "username",
    credential = "credential",
    url = "url"
).copy()

fun buildPipelineVerificationRequest() = PipelineVerificationRequest(
    url = "url",
    username = "username",
    credential = "credential",
    type = PipelineType.JENKINS.toString()
).copy()

fun buildPipeline() = Pipeline(
    id = "pipelineId",
    dashboardId = "dashboardId",
    name = "pipeline",
    username = "username",
    credential = "credential",
    url = "url",
    type = PipelineType.JENKINS
).copy()