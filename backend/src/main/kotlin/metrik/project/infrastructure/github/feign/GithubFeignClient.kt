package metrik.project.infrastructure.github.feign

import metrik.project.infrastructure.github.feign.response.BuildDetailResponse
import metrik.project.infrastructure.github.feign.response.BuildDetailResponse.WorkflowRuns
import metrik.project.infrastructure.github.feign.response.CommitResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@FeignClient(
    value = "github-api",
    url = "https://api.github.com/repos"
)
interface GithubFeignClient {

    @GetMapping("/{owner}/{repo}/actions/runs?per_page={perPage}&page={pageIndex}")
    fun retrieveMultipleRuns(
        @PathVariable("owner") owner: String,
        @PathVariable("repo") repo: String,
        @PathVariable("perPage") perPage: Int?,
        @PathVariable("pageIndex") pageIndex: Int?
    ): BuildDetailResponse

    @GetMapping("/{owner}/{repo}/actions/runs/{runId}")
    fun retrieveSingleRun(
        @PathVariable("owner") owner: String,
        @PathVariable("repo") repo: String,
        @PathVariable("runId") runId: String,
    ): WorkflowRuns

    @GetMapping("/{owner}/{repo}/commits?since={since}&until={until}&sha={branch}&per_page={perPage}&page={pageIndex}")
    fun retrieveCommits(
        @PathVariable("owner") owner: String,
        @PathVariable("repo") repo: String,
        @PathVariable("since") since: String?,
        @PathVariable("until") until: String?,
        @PathVariable("branch") branch: String?,
        @PathVariable("perPage") perPage: String?,
        @PathVariable("pageIndex") pageIndex: String?,
    ): CommitResponse


}