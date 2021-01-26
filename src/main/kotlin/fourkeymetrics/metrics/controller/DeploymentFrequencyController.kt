package fourkeymetrics.metrics.controller

import fourkeymetrics.dashboard.service.PipelineService
import fourkeymetrics.metrics.calculator.DeploymentFrequencyService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

data class DeploymentFrequencyResponse(val deploymentCount: Int)

@RestController
class DeploymentFrequencyController {
    @Autowired
    private lateinit var deploymentFrequencyService: DeploymentFrequencyService

    @Autowired
    private lateinit var pipelineService: PipelineService

    @GetMapping("/api/deployment-frequency")
    fun getDeploymentCount(@RequestParam dashboardId: String,
                           @RequestParam pipelineId: String,
                           @RequestParam startTimestamp: Long,
                           @RequestParam endTimestamp: Long,
                           @RequestParam targetStage: String): ResponseEntity<DeploymentFrequencyResponse> {
        if (isInvalidParameters(dashboardId, pipelineId, endTimestamp, startTimestamp, targetStage)) {
            return ResponseEntity.badRequest().build()
        }

        val deploymentCount = deploymentFrequencyService.getDeploymentCount(pipelineId, targetStage,
            startTimestamp, endTimestamp)

        return ResponseEntity.ok(DeploymentFrequencyResponse(deploymentCount))
    }

    private fun isInvalidParameters(dashboardId: String, pipelineId: String, endTimestamp: Long,
                                    startTimestamp: Long,
                                    targetStage: String): Boolean {
        return startTimestamp > endTimestamp ||
            !pipelineService.hasPipeline(dashboardId, pipelineId) ||
            !pipelineService.hasStageInTimeRange(pipelineId, targetStage, startTimestamp, endTimestamp)
    }
}
