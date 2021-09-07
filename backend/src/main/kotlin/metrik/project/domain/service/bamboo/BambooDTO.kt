package metrik.project.domain.service.bamboo.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import metrik.infrastructure.utlils.toTimestamp
import metrik.project.domain.model.Build
import metrik.project.domain.model.Commit
import metrik.project.domain.model.Status
import org.apache.logging.log4j.util.Strings
import org.slf4j.LoggerFactory
import java.time.ZonedDateTime

@JsonIgnoreProperties(ignoreUnknown = true)
data class BuildSummaryDTO(var results: BuildResultCollectionDTO)

@JsonIgnoreProperties(ignoreUnknown = true)
data class BuildResultCollectionDTO(val result: List<Result>)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Result(
    val number: Int = 0,
    val buildNumber: Int = 0,
    val state: String? = Strings.EMPTY,
    val buildState: String? = Strings.EMPTY
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class BuildDetailDTO(
    var state: String,
    var buildState: String,
    var number: Int,
    var buildNumber: Int,
    var buildDuration: Long,
    var buildStartedTime: ZonedDateTime?,
    var link: Link,
    var stages: Stage,
    var changes: ChangeSetDTO,
    var buildCompletedTime: ZonedDateTime?
) {
    private var logger = LoggerFactory.getLogger(this.javaClass.name)

    private fun getBuildExecutionStatus(): Status {
        return if (stages.stage.any { it.state == "Unknown" }) Status.IN_PROGRESS else
            when (this.buildState) {
                "Successful" -> {
                    Status.SUCCESS
                }
                "Failed" -> {
                    Status.FAILED
                }
                else -> {
                    Status.OTHER
                }
            }
    }

    private fun getBuildStartedTimestamp(): Long? {
        return when {
            buildStartedTime != null -> buildStartedTime!!.toTimestamp()
            buildCompletedTime != null -> buildCompletedTime!!.toTimestamp()
            else -> null
        }
    }

    fun convertToMetrikBuild(pipelineId: String): Build? {
        logger.info(
            "Bamboo converting: Started converting BuildDetailDTO [$this] for pipeline [$pipelineId]"
        )

        val buildTimestamp = getBuildStartedTimestamp() ?: return null

        try {
            val stages = stages.stage.mapNotNull {
                it.convertToMetrikBuildStage()
            }

            val build = Build(
                pipelineId,
                buildNumber,
                getBuildExecutionStatus(),
                buildDuration,
                buildTimestamp,
                link.href,
                stages = stages,
                changeSets = changes.change.map {
                    Commit(it.changesetId, it.getDateTimestamp(), it.date.toString(), it.comment)
                }
            )
            logger.info("Bamboo converting: Build converted result: [$build]")
            return build
        } catch (e: RuntimeException) {
            logger.error("Converting Bamboo DTO failed, DTO: [$this], exception: [$e]")
            throw e
        }
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class Stage(val stage: List<StageDTO>)

@JsonIgnoreProperties(ignoreUnknown = true)
data class StageDTO(
    val name: String,
    val state: String?,
    val results: StageResults
) {
    private var logger = LoggerFactory.getLogger(this.javaClass.name)

    private fun getStageExecutionStatus(): Status {
        return when (this.state) {
            "Successful" -> {
                Status.SUCCESS
            }
            "Failed" -> {
                Status.FAILED
            }
            else -> {
                Status.OTHER
            }
        }
    }

    fun convertToMetrikBuildStage(): metrik.project.domain.model.Stage? {
        logger.info("Bamboo converting: Started converting StageDTO [$this]")

        val isInProgress = state == "Unknown" || results.result.any {
            it.buildStartedTime == null || it.buildCompletedTime == null || it.buildDuration == null
        }
        val shouldSkip = results.result.isEmpty() || isInProgress
        if (shouldSkip) {
            return null
        }

        val startTimeMillis = results.result
            .map { result -> result.getStageStartedTimestamp() }
            .minOrNull()!!
        val completedTimeMillis = results.result
            .map { result -> result.getStageCompletedTimestamp() }
            .maxOrNull()!!
        val durationMillis: Long = completedTimeMillis - startTimeMillis
        val metrikStage = metrik.project.domain.model.Stage(
            name,
            getStageExecutionStatus(),
            startTimeMillis,
            durationMillis,
            0,
            completedTimeMillis
        )
        logger.info("Bamboo converting: Stage converted result: [$metrikStage]")

        return metrikStage
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class StageResults(val result: List<StageResultDetail>)

@JsonIgnoreProperties(ignoreUnknown = true)
data class StageResultDetail(
    val buildStartedTime: ZonedDateTime?,
    var buildCompletedTime: ZonedDateTime?,
    var buildDuration: Long?
) {
    fun getStageStartedTimestamp(): Long {
        return buildStartedTime!!.toTimestamp()
    }

    fun getStageCompletedTimestamp(): Long {
        return buildCompletedTime!!.toTimestamp()
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class ChangeSetDTO(val change: List<CommitDTO> = emptyList())

@JsonIgnoreProperties(ignoreUnknown = true)
data class CommitDTO(val changesetId: String, val date: ZonedDateTime, val comment: String) {
    fun getDateTimestamp(): Long {
        return date.toTimestamp()
    }
}
@JsonIgnoreProperties(ignoreUnknown = true)
data class Link(val href: String)
