package fourkeymetrics.model

import org.apache.logging.log4j.util.Strings

enum class BuildStatus {
    SUCCESS,
    FAILED,
    ABORTED
}

data class Stage(
    var name: String = Strings.EMPTY,
    var status: BuildStatus = BuildStatus.FAILED,
    var startTimeMillis: Long = 0,
    var durationMillis: Long = 0,
    var pauseDurationMillis: Long = 0
)

data class Commit(
    var commitId: String = Strings.EMPTY,
    var timestamp: Long = 0,
    var date: String = Strings.EMPTY,
    var msg: String = Strings.EMPTY
)

data class Build(
    var pipelineId: Long = 0,
    var number: Int = 0,
    var result: BuildStatus = BuildStatus.FAILED,
    var duration: Long = 0,
    var timestamp: Long = 0,
    var url: String = Strings.EMPTY,
    var stages: List<Stage> = emptyList(),
    var changeSets: List<Commit> = emptyList()
)
