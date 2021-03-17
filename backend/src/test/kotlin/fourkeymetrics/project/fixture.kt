package fourkeymetrics.project

import fourkeymetrics.project.controller.vo.request.BambooPipelineRequest
import fourkeymetrics.project.controller.vo.request.BambooVerificationRequest
import fourkeymetrics.project.controller.vo.request.JenkinsPipelineRequest
import fourkeymetrics.project.controller.vo.request.JenkinsVerificationRequest
import fourkeymetrics.project.model.Pipeline
import fourkeymetrics.project.model.PipelineType

fun buildJenkinsPipelineRequest() =
    JenkinsPipelineRequest(
        name = "pipeline",
        username = "username",
        credential = "credential",
        url = "url"
    )

fun buildBambooPipelineRequest() =
    BambooPipelineRequest(
        name = "pipeline",
        credential = "credential",
        url = "url"
    )

fun buildJenkinsPipelineVerificationRequest() = JenkinsVerificationRequest(
    url = "url",
    username = "username",
    credential = "credential"
)

fun buildBambooPipelineVerificationRequest() = BambooVerificationRequest(
    url = "url",
    credential = "credential",
)

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