package fourkeymetrics.dashboard.service

import fourkeymetrics.common.model.Build
import fourkeymetrics.dashboard.repository.BuildRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
abstract class PipelineService {
    @Autowired
    private lateinit var buildRepository: BuildRepository

    abstract fun syncBuilds(pipelineId: String): List<Build>

    abstract fun verifyPipelineConfiguration(url: String, username: String, credential: String)

    fun hasStageInTimeRange(
        pipelineId: String,
        targetStage: String,
        startTimestamp: Long,
        endTimestamp: Long
    ): Boolean {
        val allBuilds = buildRepository.getAllBuilds(pipelineId)

        return allBuilds
            .flatMap { it.stages }
            .filter { it.name == targetStage }
            .any {
                (it.startTimeMillis + it.durationMillis + it.pauseDurationMillis) in startTimestamp..endTimestamp
            }
    }

    fun getStagesSortedByName(pipelineId: String): List<String> {
        return buildRepository.getAllBuilds(pipelineId)
            .flatMap { it.stages }
            .map { it.name }
            .distinct()
            .sortedBy { it.toUpperCase() }
            .toList()
    }
}
