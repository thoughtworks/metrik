package metrik.project.infrastructure.jenkins.feign

import metrik.project.domain.service.jenkins.BuildDetailsDTO
import metrik.project.domain.service.jenkins.BuildSummaryCollectionDTO
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import java.net.URI

@FeignClient(
    value = "jenkins-api",
    url = "https://this-is-a-placeholder.com"
)
interface JenkinsFeignClient {
    @GetMapping(path = ["/wfapi/"])
    fun verifyJenkinsUrl(
        baseUrl: URI,
        @RequestHeader("Authorization") authorizationHeader: String
    )

    @GetMapping(path = ["api/json"])
    fun retrieveBuildSummariesFromJenkins(
        baseUrl: URI,
        @RequestHeader("Authorization") authorizationHeader: String,
        @RequestParam(
            "tree",
            required = false
        ) tree: String = "allBuilds[building,number,result,timestamp,duration,url," +
                "changeSets[items[commitId,timestamp,msg,date]]]"

    ): BuildSummaryCollectionDTO?

    @GetMapping(path = ["{buildSummaryNumber}/wfapi/describe"])
    fun retrieveBuildDetailsFromJenkins(
        baseUrl: URI,
        @RequestHeader("Authorization") authorizationHeader: String,
        @PathVariable("buildSummaryNumber") buildSummaryNumber: Long
    ): BuildDetailsDTO?
}