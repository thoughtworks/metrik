package metrik.project

import metrik.project.domain.model.Build
import metrik.project.domain.model.Commit
import metrik.project.domain.model.Pipeline
import metrik.project.domain.model.PipelineType
import metrik.project.domain.model.Stage
import metrik.project.domain.model.Status
import metrik.project.domain.service.githubactions.HeadCommit
import metrik.project.domain.service.githubactions.WorkflowRuns
import metrik.project.rest.vo.request.BambooPipelineRequest
import metrik.project.rest.vo.request.BambooVerificationRequest
import metrik.project.rest.vo.request.GithubActionVerificationRequest
import metrik.project.rest.vo.request.GithubActionsPipelineRequest
import metrik.project.rest.vo.request.JenkinsPipelineRequest
import metrik.project.rest.vo.request.JenkinsVerificationRequest
import metrik.project.rest.vo.response.SyncProgress
import org.mockito.kotlin.mock
import java.time.ZonedDateTime

const val pipelineID = "pipelineId"
const val projectId = "projectId"
const val name = "pipeline"
const val username = "username"
const val credential = "credential"
const val url = "http://localhost:80"
const val userInputURL = "http://localhost:80/test_project/test_repo"
const val branch = "main"
val mockEmitCb = mock<(SyncProgress) -> Unit>()
const val currentTimeStamp: Long = 1629203005000
const val previousTimeStamp: Long = 1619185260779
const val futureTimeStamp: Long = 1639185260779


val githubActionsWorkflow = WorkflowRuns(
    id = 123,
    name = "234",
    headBranch = "master",
    runNumber = 23456,
    status = "completed",
    conclusion = "SUCCESS",
    url = "12345",
    headCommit = HeadCommit(
        id = "1234",
        timestamp = ZonedDateTime.parse("2021-08-17T12:23:25Z")
    ),
    createdAt = ZonedDateTime.parse("2021-08-17T12:23:25Z"),
    updatedAt = ZonedDateTime.parse("2021-08-17T12:23:25Z")
)

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
    branch = "master",
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
