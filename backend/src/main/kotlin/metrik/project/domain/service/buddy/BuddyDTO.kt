package metrik.project.domain.service.buddy

import metrik.infrastructure.utlils.toTimestamp
import java.time.Duration
import java.time.OffsetDateTime

data class ProjectDTO(
    val name: String = "",
    val status: String = ""
)

data class PipelineDTO(
    val id: Int = 0,
    val project: ProjectDTO = ProjectDTO(),
)

data class ActionDTO(
    val name: String = ""
)

data class CommitDTO(
    val revision: String = "",
    val commitDate: OffsetDateTime = OffsetDateTime.MIN
) {
    fun getTimestamp(): Long = commitDate.toTimestamp()
    fun getDateString(): String = commitDate.toString()
}

data class ChangeSetDTO(
    val commits: List<CommitDTO> = emptyList()
)

data class ActionExecutionDTO(
    val status: String = "",
    val startDate: OffsetDateTime? = null,
    val finishDate: OffsetDateTime? = null,
    val action: ActionDTO = ActionDTO()
) {
    fun getDuration(): Long = if (startDate != null && finishDate != null)
        Duration.between(startDate, finishDate).toMillis() else 0

    fun getTimestamp(): Long = startDate?.let { it.toTimestamp() } ?: 0
}

data class ExecutionInfoDTO(
    val id: Long = 0,
    val url: String = "",
    val status: String = "",
    val branch: BranchDTO = BranchDTO(),
    val startDate: OffsetDateTime = OffsetDateTime.MIN,
    val finishDate: OffsetDateTime? = null,
    val toRevision: CommitDTO? = null,
    val fromRevision: CommitDTO? = null
) {
    fun getDuration(): Long = finishDate?.let { Duration.between(startDate, it).toMillis() } ?: 0
    fun getTimestamp(): Long = startDate.toTimestamp()
}

data class BranchDTO(val name: String = "")

data class ExecutionPageDTO(
    val page: Int = 1,
    val pageSize: Int = 20,
    val totalPageCount: Int = 1,
    val elementCount: Int = 0,
    val totalElementCount: Int = 0,
    val executions: List<ExecutionInfoDTO> = emptyList()
)

data class ExecutionDetailsDTO(
    val actionExecutions: List<ActionExecutionDTO> = emptyList()
)
