package metrik.project.infrastructure.jenkins

import metrik.project.domain.service.jenkins.dto.BuildDetailsDTO
import metrik.project.domain.service.jenkins.dto.BuildSummaryCollectionDTO
import metrik.project.infrastructure.jenkins.feign.JenkinsFeignClient
import org.springframework.stereotype.Component
import java.net.URI

@Component
class JenkinsClient(
    private val jenkinsFeignClient: JenkinsFeignClient
) {
    fun verifyJenkinsUrl(
        url: URI,
        token: String
    ) {
        jenkinsFeignClient.verifyJenkinsUrl(url, token)
    }

    fun retrieveBuildSummariesFromJenkins(
        url: URI,
        token: String
    ): BuildSummaryCollectionDTO {
        return jenkinsFeignClient.retrieveBuildSummariesFromJenkins(url, token)
    }

    fun retrieveBuildDetailsFromJenkins(
        url: URI,
        token: String,
        buildSummaryNumber: Int
    ): BuildDetailsDTO {
        return jenkinsFeignClient.retrieveBuildDetailsFromJenkins(
            url, token, buildSummaryNumber
        )
    }
}