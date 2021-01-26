package fourkeymetrics.operation

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

data class UpdatedBuildsResponse(val updateTimestamp: Long)

@RestController
class UpdatingController {
    @Autowired
    private lateinit var updatingService: UpdatingService

    @PostMapping("/api/build")
    fun updateBuilds(
        @RequestParam dashboardId: String,
        @RequestParam pipelineId: String,
    ): ResponseEntity<UpdatedBuildsResponse> {
        val updatedTimestamp = updatingService.update(dashboardId, pipelineId)

        return if (updatedTimestamp == null) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        } else {
            ResponseEntity.ok(UpdatedBuildsResponse(updatedTimestamp))
        }
    }
}
