package fourkeymetrics.resource

import fourkeymetrics.service.BuildService
import fourkeymetrics.service.ConfigurationService
import fourkeymetrics.service.DeploymentFrequencyService
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
    private lateinit var configurationService: ConfigurationService

    @Autowired
    private lateinit var buildService: BuildService

    @GetMapping("/api/deployment-frequency")
    fun getDeploymentCount(@RequestParam pipelineID: String,
                           @RequestParam targetStage: String, @RequestParam startTime: Long,
                           @RequestParam endTime: Long): ResponseEntity<DeploymentFrequencyResponse> {
        if (isInvalidParameters(startTime, endTime, pipelineID, targetStage)) {
            return ResponseEntity.badRequest().build()
        }

        val deploymentCount = deploymentFrequencyService.getDeploymentCount(pipelineID, targetStage, startTime, endTime)

        return ResponseEntity.ok(DeploymentFrequencyResponse(deploymentCount))
    }

    private fun isInvalidParameters(startTime: Long, endTime: Long, pipelineID: String, targetStage: String): Boolean {
        return startTime > endTime ||
                !configurationService.hasPipeline(pipelineID) ||
                !buildService.hasStageInLastBuild(pipelineID, targetStage)
    }
}
