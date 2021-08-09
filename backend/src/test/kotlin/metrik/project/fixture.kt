package metrik.project

import metrik.project.TestConstants.bambooPipeline
import metrik.project.TestConstants.githubActionsPipeline
import metrik.project.TestConstants.jenkinsPipeline
import metrik.project.TestConstants.noopPipeline
import metrik.project.domain.model.Pipeline
import metrik.project.domain.model.PipelineType
import metrik.project.rest.vo.request.*



object TestConstants{
    private val pipelineID = "pipelineId"

    private val projectId = "projectId"

    private val name = "pipeline"

    private val username = "username"

    private val credential = "credential"

    private val url = "url"

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

}


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

