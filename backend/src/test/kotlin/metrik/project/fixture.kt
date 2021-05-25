package metrik.project

import metrik.project.rest.vo.request.BambooPipelineRequest
import metrik.project.rest.vo.request.BambooVerificationRequest
import metrik.project.rest.vo.request.JenkinsPipelineRequest
import metrik.project.rest.vo.request.JenkinsVerificationRequest
import metrik.project.domain.model.Pipeline
import metrik.project.domain.model.PipelineType

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