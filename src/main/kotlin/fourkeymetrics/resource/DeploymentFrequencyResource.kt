package fourkeymetrics.resource

import fourkeymetrics.service.DeploymentFrequencyService
import fourkeymetrics.service.JenkinsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

data class DeploymentFrequencyResponse(val deploymentCount: Int)

@RestController
class DeploymentFrequencyResource {
    @Autowired
    private lateinit var deploymentFrequencyService: DeploymentFrequencyService

    @Autowired
    private lateinit var jenkinsService: JenkinsService

    @GetMapping("/api/deployment-frequency")
    fun getDeploymentCount(@RequestParam pipelineName: String, @RequestParam targetStage: String,
                           @RequestParam startTime: Long, @RequestParam endTime: Long): ResponseEntity<DeploymentFrequencyResponse> {
        if (isInvalidParameters(startTime, endTime, pipelineName, targetStage)) {
            return ResponseEntity.badRequest().build()
        }

        val deploymentCount = deploymentFrequencyService.getDeploymentCount(pipelineName, targetStage, startTime, endTime)

        return ResponseEntity.ok(DeploymentFrequencyResponse(deploymentCount))
    }

    private fun isInvalidParameters(startTime: Long, endTime: Long, pipelineName: String, targetStage: String) =
            startTime > endTime || !jenkinsService.hasPipeline(pipelineName) || !jenkinsService.hasStage(pipelineName, targetStage)
}