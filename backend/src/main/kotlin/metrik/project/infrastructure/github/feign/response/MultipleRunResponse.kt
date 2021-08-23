package metrik.project.infrastructure.github.feign.response

import com.fasterxml.jackson.databind.PropertyNamingStrategy.SnakeCaseStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming
import java.time.ZonedDateTime


@JsonNaming(SnakeCaseStrategy::class)
data class MultipleRunResponse(
    val totalCount: Int,
    val workflowRuns: List<SingleRunResponse>
)