package metrik.common.model

import org.apache.logging.log4j.util.Strings

enum class Status {
    SUCCESS,
    FAILED,
    IN_PROGRESS,
    OTHER
}

data class Stage(
    val name: String = Strings.EMPTY,
    val status: Status = Status.FAILED,
    val startTimeMillis: Long = 0,
    val durationMillis: Long = 0,
    val pauseDurationMillis: Long = 0,
    val completedTimeMillis: Long? = null,
) {
    fun getStageDoneTime(): Long {
        return this.completedTimeMillis ?: this.startTimeMillis + this.durationMillis + this.pauseDurationMillis
    }
}

data class Commit(
    val commitId: String = Strings.EMPTY,
    val timestamp: Long = 0,
    val date: String = Strings.EMPTY,
    val msg: String = Strings.EMPTY
)

data class Build(
    val pipelineId: String = Strings.EMPTY,
    val number: Int = 0,
    val result: Status? = null,
    val duration: Long = 0,
    val timestamp: Long = 0,
    val url: String = Strings.EMPTY,
    val stages: List<Stage> = emptyList(),
    val changeSets: List<Commit> = emptyList()
) {

    fun containsGivenDeploymentInGivenTimeRange(
        deployStageName: String,
        status: Status,
        startTimestamp: Long,
        endTimestamp: Long
    ): Boolean {
        return this.stages.any {
            it.name == deployStageName
                    && it.status == status
                    && it.getStageDoneTime() in startTimestamp..endTimestamp
        }

    }

    fun containsGivenDeploymentBeforeGivenTimestamp(
        deployStageName: String,
        status: Status,
        timestamp: Long
    ): Boolean {
        return this.stages.any {
            it.name == deployStageName
                    && it.status == status
                    && it.getStageDoneTime() < timestamp
        }
    }

    fun findGivenStage(deployStageName: String, status: Status): Stage? {
        return this.stages.find {
            it.name == deployStageName && it.status == status
        }
    }
}


