package fourkeymetrics.model

import org.apache.logging.log4j.util.Strings

enum class BuildStatus {
  SUCCESS,
  FAILED,
  ABORTED
}

data class Stage(var name: String, var status: BuildStatus, var startTimeMillis: Long,
                 var durationMillis: Long, var pauseDurationMillis: Long) {
  constructor() : this(Strings.EMPTY, BuildStatus.FAILED, 0, 0, 0)
}

data class Commit(var commitId: String, var timestamp: Long, var date: String, var msg: String) {
  constructor() : this(Strings.EMPTY, 0, Strings.EMPTY, Strings.EMPTY)
}

data class Build(var pipelineId: Long, var number: Int, var result: BuildStatus,
                 var duration: Long, var timestamp: Long, var url: String, var stages: List<Stage>,
                 var changeSets: List<Commit>) {
  constructor() : this(0, 0, BuildStatus.FAILED, 0, 0,
      Strings.EMPTY, emptyList(), emptyList())
}
