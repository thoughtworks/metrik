package metrik.project.domain.service.jenkins.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import metrik.project.domain.model.Status
import org.apache.logging.log4j.util.Strings

@JsonIgnoreProperties(ignoreUnknown = true)
data class BuildSummaryCollectionDTO(var allBuilds: List<BuildSummaryDTO> = emptyList())

@JsonIgnoreProperties(ignoreUnknown = true)
data class BuildSummaryDTO(
    val number: Int = 0,
    val result: String? = Strings.EMPTY,
    val duration: Long = 0,
    val timestamp: Long = 0,
    val url: String = Strings.EMPTY,
    val changeSets: List<ChangeSetDTO> = emptyList()
) {
    fun getBuildExecutionStatus(): Status {
        return when (this.result) {
            null -> {
                Status.IN_PROGRESS
            }
            "SUCCESS" -> {
                Status.SUCCESS
            }
            "FAILURE" -> {
                Status.FAILED
            }
            else -> {
                Status.OTHER
            }
        }
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class ChangeSetDTO(val items: List<CommitDTO> = emptyList())

@JsonIgnoreProperties(ignoreUnknown = true)
data class CommitDTO(val commitId: String, val timestamp: Long, val date: String)

@JsonIgnoreProperties(ignoreUnknown = true)
data class BuildDetailsDTO(val stages: List<StageDTO> = emptyList())

@JsonIgnoreProperties(ignoreUnknown = true)
data class StageDTO(
    val name: String,
    val status: String?,
    val startTimeMillis: Long,
    val durationMillis: Long,
    val pauseDurationMillis: Long
) {
    fun getStageExecutionStatus(): Status {
        return when (this.status) {
            "SUCCESS" -> {
                Status.SUCCESS
            }
            "FAILED" -> {
                Status.FAILED
            }
            "IN_PROGRESS" -> {
                Status.IN_PROGRESS
            }
            else -> {
                Status.OTHER
            }
        }
    }
}
