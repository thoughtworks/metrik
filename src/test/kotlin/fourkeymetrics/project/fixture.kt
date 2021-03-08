package fourkeymetrics.project

import fourkeymetrics.project.controller.vo.request.PipelineRequest
import fourkeymetrics.project.controller.vo.request.PipelineVerificationRequest
import fourkeymetrics.project.controller.vo.response.PipelineResponse
import fourkeymetrics.project.model.Pipeline
import fourkeymetrics.project.model.PipelineType

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

fun buildPipeline(type: PipelineType = PipelineType.JENKINS): Pipeline {
    if (type == PipelineType.JENKINS) {
        return Pipeline(
            id = "pipelineId",
            projectId = "projectId",
            name = "pipeline",
            username = "username",
            credential = "credential",
            url = "url",
            type = PipelineType.JENKINS
        ).copy()
    } else {
        return Pipeline(
            id = "pipelineId",
            projectId = "projectId",
            name = "pipeline",
            credential = "credential",
            url = "url",
            type = PipelineType.BAMBOO
        ).copy()
    }
}