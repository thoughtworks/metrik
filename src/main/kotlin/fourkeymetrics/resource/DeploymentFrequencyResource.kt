package fourkeymetrics.resource

import fourkeymetrics.pipeline.Pipeline
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
    private lateinit var pipeline: Pipeline

    @GetMapping("/api/deployment-frequency")
    fun getDeploymentCount(@RequestParam dashboardId: String,
                           @RequestParam targetStage: String, @RequestParam startTime: Long,
                           @RequestParam endTime: Long,
                           @RequestParam pipelineID: String): ResponseEntity<DeploymentFrequencyResponse> {
        if (isInvalidParameters(dashboardId, startTime, endTime, pipelineID, targetStage)) {
            return ResponseEntity.badRequest().build()
        }

        val deploymentCount = deploymentFrequencyService.getDeploymentCount(targetStage, pipelineID, startTime, endTime)

        return ResponseEntity.ok(DeploymentFrequencyResponse(deploymentCount))
    }

    private fun isInvalidParameters(dashboardId: String, startTime: Long, endTime: Long, pipelineID: String, targetStage: String): Boolean {
        return startTime > endTime ||
            !pipeline.hasPipeline(dashboardId, pipelineID) ||
            !pipeline.hasStageInLastBuild(pipelineID, targetStage)
    }
}
