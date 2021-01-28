package fourkeymetrics.dashboard.service.jenkins.dto

import fourkeymetrics.common.model.BuildResult
import fourkeymetrics.common.model.StageStatus
import org.apache.logging.log4j.util.Strings

data class BuildSummaryCollectionDTO(var allBuilds: List<BuildSummaryDTO> = emptyList())

data class BuildSummaryDTO(val number: Int = 0, val result: BuildResult? = BuildResult.FAILURE,
                           val duration: Long = 0,
                           val timestamp: Long = 0,
                           val url: String = Strings.EMPTY,
                           val changeSets: List<ChangeSetDTO> = emptyList())

data class ChangeSetDTO(val items: List<CommitDTO> = emptyList())

data class CommitDTO(val commitId: String, val timestamp: Long, val date: String, val msg: String)

data class BuildDetailsDTO(val stages: List<StageDTO> = emptyList())

data class StageDTO(val name: String, val status: StageStatus, val startTimeMillis: Long,
                    val durationMillis: Long, val pauseDurationMillis: Long)
