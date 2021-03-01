package fourkeymetrics.project.service.jenkins.dto

import org.apache.logging.log4j.util.Strings

data class BuildSummaryCollectionDTO(var allBuilds: List<BuildSummaryDTO> = emptyList())

data class BuildSummaryDTO(val number: Int = 0, val result: String? = Strings.EMPTY,
                           val duration: Long = 0,
                           val timestamp: Long = 0,
                           val url: String = Strings.EMPTY,
                           val changeSets: List<ChangeSetDTO> = emptyList())

data class ChangeSetDTO(val items: List<CommitDTO> = emptyList())

data class CommitDTO(val commitId: String, val timestamp: Long, val date: String, val msg: String)

data class BuildDetailsDTO(val stages: List<StageDTO> = emptyList())

data class StageDTO(val name: String, val status: String, val startTimeMillis: Long,
                    val durationMillis: Long, val pauseDurationMillis: Long)
