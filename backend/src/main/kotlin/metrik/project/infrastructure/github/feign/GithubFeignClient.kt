package metrik.project.infrastructure.github.feign

import metrik.project.infrastructure.github.feign.response.CommitResponse
import metrik.project.infrastructure.github.feign.response.MultipleRunResponse
import metrik.project.infrastructure.github.feign.response.SingleRunResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(
    value = "github-api",
    url = "https://api.github.com/repos"
)
interface GithubFeignClient {

    @GetMapping("/{owner}/{repo}/actions/runs")
    fun retrieveMultipleRuns(
        @RequestHeader("Authorization") authorizationHeader: String,
        @PathVariable("owner") owner: String,
        @PathVariable("repo") repo: String,
        @RequestParam("perPage", required = false) perPage: Int? = null,
        @RequestParam("pageIndex", required = false) pageIndex: Int? = null
    ): MultipleRunResponse

    @GetMapping("/{owner}/{repo}/actions/runs/{runId}")
    fun retrieveSingleRun(
        @RequestHeader("Authorization") authorizationHeader: String,
        @PathVariable("owner") owner: String,
        @PathVariable("repo") repo: String,
        @PathVariable("runId") runId: String,
    ): SingleRunResponse

    @GetMapping("/{owner}/{repo}/commits")
    fun retrieveCommits(
        @RequestHeader("Authorization") authorizationHeader: String,
        @PathVariable("owner") owner: String,
        @PathVariable("repo") repo: String,
        @RequestParam("since", required = false) since: String? = null,
        @RequestParam("until", required = false) until: String? = null,
        @RequestParam("branch", required = false) branch: String? = null,
        @RequestParam("perPage", required = false) perPage: Int? = null,
        @RequestParam("pageIndex", required = false) pageIndex: Int? = null,
    ): List<CommitResponse>
}
