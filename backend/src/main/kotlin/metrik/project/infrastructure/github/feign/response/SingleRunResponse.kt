package metrik.project.infrastructure.github.feign.response

import java.time.ZonedDateTime

data class SingleRunResponse(
    val id: Int,
    val name: String,
    val headBranch: String,
    val runNumber: Int,
    val status: String,
    val conclusion: String?,
    val url: String,
    val headCommit: HeadCommit,
    val createdAt: ZonedDateTime,
    val updatedAt: ZonedDateTime,
) {
    data class HeadCommit(
        val id: String,
        val timestamp: ZonedDateTime
    )
}