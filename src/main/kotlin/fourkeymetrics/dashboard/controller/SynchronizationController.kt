package fourkeymetrics.dashboard.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

data class SynchronizationRecordResponse(val synchronizationTimestamp: Long?)

@RestController
class SynchronizationController {
    @Autowired
    private lateinit var synchronizationApplicationService: SynchronizationApplicationService

    @PostMapping("/api/dashboard/{dashboardId}/synchronization")
    fun updateBuilds(@PathVariable dashboardId: String): ResponseEntity<SynchronizationRecordResponse> {
        val updatedTimestamp = synchronizationApplicationService.synchronize(dashboardId)

        return if (updatedTimestamp == null) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        } else {
            ResponseEntity.ok(SynchronizationRecordResponse(updatedTimestamp))
        }
    }

    @GetMapping("/api/dashboard/{dashboardId}/synchronization")
    fun getLastSynchronization(@PathVariable dashboardId: String): ResponseEntity<SynchronizationRecordResponse> {
        val lastSyncTimestamp = synchronizationApplicationService.getLastSyncTimestamp(dashboardId)

        return ResponseEntity.ok(SynchronizationRecordResponse(lastSyncTimestamp))
    }
}
