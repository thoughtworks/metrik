package fourkeymetrics.project.controller

import fourkeymetrics.exception.ApplicationException
import fourkeymetrics.project.controller.applicationservice.SyncProgress
import fourkeymetrics.project.controller.applicationservice.SynchronizationApplicationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.util.concurrent.Executors

data class SynchronizationRecordResponse(val synchronizationTimestamp: Long?)

private const val PROGRESS_UPDATE_EVENT = "PROGRESS_UPDATE_EVENT"
private const val COMPLETE_STREAM_EVENT = "COMPLETE_STREAM_EVENT"

@RestController
class SynchronizationController {
    @Autowired
    private lateinit var synchronizationApplicationService: SynchronizationApplicationService

    @PostMapping("/api/project/{projectId}/synchronization")
    fun updateBuilds(@PathVariable projectId: String): ResponseEntity<SynchronizationRecordResponse> {
        val updatedTimestamp = synchronizationApplicationService.synchronize(projectId)

        return if (updatedTimestamp == null) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        } else {
            ResponseEntity.ok(SynchronizationRecordResponse(updatedTimestamp))
        }
    }

    @GetMapping("/api/project/{projectId}/sse-sync")
    fun sseSynchronization(@PathVariable projectId: String): SseEmitter {
        val emitter = SseEmitter(Companion.SSE_CONNECTION_TIMEOUT)
        val emitCb: (SyncProgress) -> Unit =
            { progress ->
                emitter.send(
                    SseEmitter.event()
                        .name(PROGRESS_UPDATE_EVENT)
                        .data(progress)
                        .id(progress.pipelineName)
                )
            }
        val sseMvcExecutor = Executors.newSingleThreadExecutor()

        sseMvcExecutor.execute {
            try {
                synchronizationApplicationService.synchronize(projectId, emitCb)
                emitter.send(
                    SseEmitter.event()
                        .name(COMPLETE_STREAM_EVENT)
                        .data(COMPLETE_STREAM_EVENT)
                )
                emitter.complete()
            } catch (ex: ApplicationException) {
                emitter.completeWithError(ex)
                throw(ex)
            }
        }

        return emitter
    }

    @GetMapping("/api/project/{projectId}/synchronization")
    fun getLastSynchronization(@PathVariable projectId: String): ResponseEntity<SynchronizationRecordResponse> {
        val lastSyncTimestamp = synchronizationApplicationService.getLastSyncTimestamp(projectId)

        return ResponseEntity.ok(SynchronizationRecordResponse(lastSyncTimestamp))
    }

    companion object {
        const val SSE_CONNECTION_TIMEOUT: Long = 1000 * 60 * 10
    }
}
