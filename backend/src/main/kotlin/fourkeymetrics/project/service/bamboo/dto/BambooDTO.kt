package fourkeymetrics.project.service.bamboo.dto

import org.apache.logging.log4j.util.Strings
import java.time.ZonedDateTime

data class BuildSummaryDTO(var results: BuildResultCollectionDTO)

data class BuildResultCollectionDTO(val result: List<Result>)

data class Result(val number: Int = 0,
                  val buildNumber: Int = 0,
                  val state: String? = Strings.EMPTY,
                  val buildState: String? = Strings.EMPTY)

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
        var buildCompletedTime: ZonedDateTime)

data class Stage(val stage: List<StageDTO>)

data class StageDTO(val name: String, val state: String?, val results : StageResult)

data class StageResult(val result : List<StageDetailResult>)

data class StageDetailResult(val buildStartedTime: ZonedDateTime?, var buildCompletedTime: ZonedDateTime?,
                             var buildDuration: Long)

data class ChangeSetDTO(val change: List<CommitDTO> = emptyList())

data class CommitDTO(val changesetId: String, val date: ZonedDateTime, val comment: String)

data class Link(val href : String)