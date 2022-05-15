package metrik.project.infrastructure.github.feign.response

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming
import metrik.project.domain.service.githubactions.GithubActionsRun
import org.apache.logging.log4j.util.Strings
import java.time.ZonedDateTime

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy::class)
data class SingleRunResponse(
    val id: Long = 0,
    val name: String = Strings.EMPTY,
    val headBranch: String = Strings.EMPTY,
    val runNumber: Int = 0,
    val status: String = Strings.EMPTY,
    val conclusion: String? = null,
    val url: String = Strings.EMPTY,
    val headCommit: HeadCommit = HeadCommit(),
    val createdAt: ZonedDateTime = ZonedDateTime.now(),
    val updatedAt: ZonedDateTime = ZonedDateTime.now(),
) {
    data class HeadCommit(
        val id: String = Strings.EMPTY,
        val timestamp: ZonedDateTime = ZonedDateTime.now()
    )

    fun toGithubActionsRun(): GithubActionsRun =
        GithubActionsRun(
            id = id,
            name = name,
            status = status,
            conclusion = conclusion,
            url = url,
            branch = headBranch,
            commitTimeStamp = headCommit.timestamp,
            createdTimestamp = createdAt,
            updatedTimestamp = updatedAt
        )
}
