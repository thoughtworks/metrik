package metrik.project

import metrik.project.domain.model.Build
import metrik.project.domain.model.Commit
import metrik.project.domain.model.Pipeline
import metrik.project.domain.model.PipelineType
import metrik.project.domain.model.Stage
import metrik.project.domain.model.Status
import metrik.project.rest.vo.request.BambooPipelineRequest
import metrik.project.rest.vo.request.BambooVerificationRequest
import metrik.project.rest.vo.request.GithubActionVerificationRequest
import metrik.project.rest.vo.request.GithubActionsPipelineRequest
import metrik.project.rest.vo.request.JenkinsPipelineRequest
import metrik.project.rest.vo.request.JenkinsVerificationRequest
import metrik.project.rest.vo.response.SyncProgress
import org.mockito.kotlin.mock

const val pipelineID = "pipelineId"
const val projectId = "projectId"
const val name = "pipeline"
const val username = "username"
const val credential = "credential"
const val url = "http://localhost:80"
const val userInputURL = "http://localhost:80/test_project/test_repo"
val mockEmitCb = mock<(SyncProgress) -> Unit>()

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
    url = userInputURL,
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

val stage = Stage(
    "CI",
    Status.SUCCESS,
    1628680261000,
    16000,
    0,
    1628680277000
)
val commit = Commit(
    "3986a82cf9f852e9938f7e7984d1e95742854baa",
    1628646391000,
    "2021-08-11T01:46:31Z[UTC]"
)

val githubActionsBuild = Build(
    pipelineId = pipelineID,
    number = 1111111111,
    result = Status.SUCCESS,
    duration = 16000,
    timestamp = 1628680261000,
    url = "http://localhost:80/test_project/test_repo/actions/runs/1111111111",
    stages = listOf(
        stage
    ),
    changeSets = listOf(
        commit
    )
)

val builds = listOf(
    Build(
        stages = listOf(
            Stage(name = "clone"), Stage(name = "build"),
            Stage(name = "zzz"), Stage(name = "amazing")
        )
    ),
    Build(
        stages = listOf(
            Stage(name = "build"), Stage("good")
        )
    )
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
    when (type) {
        PipelineType.JENKINS -> jenkinsPipeline.copy()
        PipelineType.BAMBOO -> bambooPipeline.copy()
        PipelineType.GITHUB_ACTIONS -> githubActionsPipeline.copy()
        else -> noopPipeline.copy()
    }
