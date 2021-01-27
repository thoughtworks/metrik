package fourkeymetrics.dashboard.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

data class UpdatedBuildsResponse(val updateTimestamp: Long)

@RestController
class SynchronizationController {
    @Autowired
    private lateinit var synchronizationService: SynchronizationService

    @PostMapping("/api/dashboard/{dashboardId}/synchronization")
    fun updateBuilds(@PathVariable dashboardId: String): ResponseEntity<UpdatedBuildsResponse> {
        val pipelineId = "fake-pipeline-id"
        val updatedTimestamp = synchronizationService.update(dashboardId, pipelineId)

        return if (updatedTimestamp == null) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        } else {
            ResponseEntity.ok(UpdatedBuildsResponse(updatedTimestamp))
        }
    }
}
