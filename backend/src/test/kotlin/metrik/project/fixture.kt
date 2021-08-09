package metrik.project

import metrik.project.domain.model.Pipeline
import metrik.project.domain.model.PipelineType
import metrik.project.rest.vo.request.*


private const val pipelineID = "pipelineId"
private const val projectId = "projectId"
private const val name = "pipeline"
private const val username = "username"
private const val credential = "credential"
private const val url = "url"

val jenkinsPipeline = Pipeline(
    id = pipelineID,
    projectId = projectId,
    name = name,
    username = username,
    credential = credential,
    url = url,
    type = PipelineType.JENKINS
)
val bambooPipeline = Pipeline(
    id = pipelineID,
    projectId = projectId,
    name = name,
    username = username,
    credential = credential,
    url = url,
    type = PipelineType.BAMBOO
)
val githubActionsPipeline = Pipeline(
    id = pipelineID,
    projectId = projectId,
    name = name,
    username = username,
    credential = credential,
    url = url,
    type = PipelineType.GITHUB_ACTIONS
)
val noopPipeline = Pipeline(
    id = pipelineID,
    projectId = projectId,
    name = name,
    username = username,
    credential = credential,
    url = url,
    type = PipelineType.NOT_SUPPORTED
)




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

fun buildGithubActionsPipelineRequest() =
    GithubActionsPipelineRequest(
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

fun buildGithubActionsPipelineVerificationRequest() = GithubActionVerificationRequest(
    url = "url",
    credential = "credential",
)

fun buildPipeline(type: PipelineType = PipelineType.NOT_SUPPORTED): Pipeline =
    when(type){
        PipelineType.JENKINS -> jenkinsPipeline.copy()
        PipelineType.BAMBOO -> bambooPipeline.copy()
        PipelineType.GITHUB_ACTIONS -> githubActionsPipeline.copy()
        else -> noopPipeline.copy()
    }

