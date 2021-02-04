package fourkeymetrics.common.model

import org.apache.logging.log4j.util.Strings

enum class StageStatus {
    SUCCESS,
    FAILED,
    ABORTED,
    IN_PROGRESS,
    PAUSED_PENDING_INPUT
}

enum class BuildResult {
    SUCCESS,
    FAILURE,
    ABORTED,
    NOT_BUILT,
    UNSTABLE,
    CYCLE
}

class Stage(
        var name: String = Strings.EMPTY,
        var status: StageStatus = StageStatus.FAILED,
        var startTimeMillis: Long = 0,
        var durationMillis: Long = 0,
        var pauseDurationMillis: Long = 0
) {
    fun getStageDoneTime(): Long {
        return this.startTimeMillis + this.durationMillis + this.pauseDurationMillis
    }
}

data class Commit(
    var commitId: String = Strings.EMPTY,
    var timestamp: Long = 0,
    var date: String = Strings.EMPTY,
    var msg: String = Strings.EMPTY
)

class Build(
        var pipelineId: String = Strings.EMPTY, var number: Int = 0,
        var result: BuildResult? = BuildResult.FAILURE, var duration: Long = 0,
        var timestamp: Long = 0, var url: String = Strings.EMPTY,
        var stages: List<Stage> = emptyList(), var changeSets: List<Commit> = emptyList()
) {

    fun containsGivenDeploymentInGivenTimeRange(
            deployStageName: String,
            stageStatus: StageStatus,
            startTimestamp: Long,
            endTimestamp: Long
    ): Boolean {
        return this.stages.any {
            it.name == deployStageName
                    && it.status == stageStatus
                    && it.getStageDoneTime() in startTimestamp..endTimestamp
        }

    }

    fun containsGivenDeploymentBeforeGivenTimestamp(
            deployStageName: String,
            stageStatus: StageStatus,
            timestamp: Long
    ): Boolean {
        return this.stages.any {
            it.name == deployStageName
                    && it.status == stageStatus
                    && it.getStageDoneTime() < timestamp
        }
    }

    fun findGivenStage(deployStageName: String, stageStatus: StageStatus): Stage? {
        return this.stages.find {
            it.name == deployStageName && it.status == stageStatus
        }
    }
}


