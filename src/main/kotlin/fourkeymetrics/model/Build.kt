package fourkeymetrics.model

import org.apache.logging.log4j.util.Strings

enum class BuildStatus {
    SUCCESS,
    FAILED,
    ABORTED
}

data class Stage(var status: BuildStatus, var name: String, var startTime: Long, var duration: Int) {
    constructor() : this(BuildStatus.FAILED, Strings.EMPTY, 0, 0)
}

data class Commit(var revisionNumber: String, var message: String, var timestamp: Long) {
    constructor() : this(Strings.EMPTY, Strings.EMPTY, 0)
}

data class Build(var pipelineID: String, var buildNumber: Int, var status: BuildStatus,
                 var timestamp: Long, var stages: List<Stage>, var commits: List<Commit>) {
    constructor() : this(Strings.EMPTY, 0, BuildStatus.FAILED, 0, emptyList(), emptyList())
}
