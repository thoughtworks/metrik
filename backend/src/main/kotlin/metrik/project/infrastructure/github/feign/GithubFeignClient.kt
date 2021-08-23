package metrik.project.infrastructure.github.feign

import metrik.project.infrastructure.github.feign.response.CommitResponse
import metrik.project.infrastructure.github.feign.response.MultipleRunResponse
import metrik.project.infrastructure.github.feign.response.SingleRunResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader

@FeignClient(
    value = "github-api",
    url = "https://api.github.com/repos"
)
interface GithubFeignClient {

    @GetMapping("/{owner}/{repo}/actions/runs?per_page={perPage}&page={pageIndex}")
    fun retrieveMultipleRuns(
        @RequestHeader("Authorization") authorizationHeader: String,
        @PathVariable("owner") owner: String,
        @PathVariable("repo") repo: String,
        @PathVariable("perPage") perPage: Int? = null,
        @PathVariable("pageIndex") pageIndex: Int? = null
    ): MultipleRunResponse

    @GetMapping("/{owner}/{repo}/actions/runs/{runId}")
    fun retrieveSingleRun(
        @RequestHeader("Authorization") authorizationHeader: String,
        @PathVariable("owner") owner: String,
        @PathVariable("repo") repo: String,
        @PathVariable("runId") runId: String,
    ): SingleRunResponse

    @GetMapping("/{owner}/{repo}/commits?since={since}&until={until}&sha={branch}&per_page={perPage}&page={pageIndex}")
    fun retrieveCommits(
        @RequestHeader("Authorization") authorizationHeader: String,
        @PathVariable("owner") owner: String,
        @PathVariable("repo") repo: String,
        @PathVariable("since") since: String? = null,
        @PathVariable("until") until: String? = null,
        @PathVariable("branch") branch: String? = null,
        @PathVariable("perPage") perPage: Int? = null,
        @PathVariable("pageIndex") pageIndex: Int? = null,
    ): List<CommitResponse>
}
