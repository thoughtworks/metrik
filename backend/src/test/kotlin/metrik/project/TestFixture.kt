package metrik.project

import io.mockk.mockk
import metrik.project.domain.model.Commit
import metrik.project.domain.model.Execution
import metrik.project.domain.model.PipelineConfiguration
import metrik.project.domain.model.PipelineType
import metrik.project.domain.model.Stage
import metrik.project.domain.model.Status
import metrik.project.domain.service.githubactions.GithubActionsRun
import metrik.project.infrastructure.github.feign.response.BranchResponse
import metrik.project.rest.vo.request.BambooPipelineRequest
import metrik.project.rest.vo.request.BambooVerificationRequest
import metrik.project.rest.vo.request.GithubActionsPipelineRequest
import metrik.project.rest.vo.request.GithubActionsVerificationRequest
import metrik.project.rest.vo.request.JenkinsPipelineRequest
import metrik.project.rest.vo.request.JenkinsVerificationRequest
import metrik.project.rest.vo.response.SyncProgress
import java.time.ZonedDateTime

object TestFixture {
    const val pipelineId = "pipelineId"
    const val projectId = "projectId"
    const val name = "pipeline"
    const val username = "username"
    const val credential = "credential"
    const val url = "http://localhost:80"
    const val userInputURL = "https://api.github.com/repos/test_project/test_repo"
    const val branch = "master"
    val mockEmitCb = mockk<(SyncProgress) -> Unit>(relaxed = true)
    const val currentTimeStamp: Long = 1629203005000
    const val previousTimeStamp: Long = 1619185260779

    val branch1 = BranchResponse(
        "CI/CD pipeline"
    )
    val branch2 = BranchResponse(
        "main"
    )

    val githubActionsRun1 = GithubActionsRun(
        id = 123,
        name = "234",
        branch = "master",
        status = "completed",
        conclusion = "SUCCESS",
        url = "12345",
        commitTimeStamp = ZonedDateTime.parse("2021-08-17T12:23:25Z"),
        createdTimestamp = ZonedDateTime.parse("2021-08-17T12:23:25Z"),
        updatedTimestamp = ZonedDateTime.parse("2021-08-17T12:23:25Z")
    )

    val githubActionsRun2 = GithubActionsRun(
        id = 1111111111,
        name = "CI",
        branch = "feature/CI pipeline",
        status = "in_progress",
        conclusion = null,
        url = "http://localhost:80/test_project/test_repo/actions/runs/1111111111",
        commitTimeStamp = ZonedDateTime.parse("2021-08-11T01:46:31Z"),
        createdTimestamp = ZonedDateTime.parse("2021-08-11T11:11:01Z"),
        updatedTimestamp = ZonedDateTime.parse("2021-08-11T11:11:17Z")
    )

    val jenkinsPipeline = PipelineConfiguration(
        id = pipelineId,
        projectId = projectId,
        name = name,
        username = username,
        credential = credential,
        url = url,
        type = PipelineType.JENKINS
    )
    val bambooPipeline = PipelineConfiguration(
        id = pipelineId,
        projectId = projectId,
        name = name,
        username = username,
        credential = credential,
        url = url,
        type = PipelineType.BAMBOO
    )
    val githubActionsPipeline = PipelineConfiguration(
        id = pipelineId,
        projectId = projectId,
        name = name,
        credential = credential,
        url = userInputURL,
        type = PipelineType.GITHUB_ACTIONS
    )
    val noopPipeline = PipelineConfiguration(
        id = pipelineId,
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

    val githubActionsExecution = Execution(
        pipelineId = pipelineId,
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

    val executions = listOf(
        Execution(
            stages = listOf(
                Stage(name = "clone"), Stage(name = "build"),
                Stage(name = "zzz"), Stage(name = "amazing")
            )
        ),
        Execution(
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
            url = "https://github.com/test_project/test_repo"
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

    fun buildGithubActionsPipelineVerificationRequest() = GithubActionsVerificationRequest(
        url = "url",
        credential = "credential",
    )

    fun buildPipeline(type: PipelineType = PipelineType.NOT_SUPPORTED): PipelineConfiguration =
        when (type) {
            PipelineType.JENKINS -> jenkinsPipeline.copy()
            PipelineType.BAMBOO -> bambooPipeline.copy()
            PipelineType.GITHUB_ACTIONS -> githubActionsPipeline.copy()
            else -> noopPipeline.copy()
        }
}
