package fourkeymetrics.pipeline

import fourkeymetrics.dashboardconfiguration.DashboardRepository
import fourkeymetrics.metric.model.Build
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
abstract class Pipeline {
    @Autowired
    private lateinit var dashboardRepository: DashboardRepository

    @Autowired
    private lateinit var buildRepository: BuildRepository

    fun hasPipeline(dashboardId: String, pipelineId: String): Boolean {
        return dashboardRepository.getPipelineConfiguration(dashboardId, pipelineId) != null
    }

    fun hasStageInTimeRange(pipelineId: String, targetStage: String, startTimestamp: Long,
                            endTimestamp: Long): Boolean {
        val allBuilds = buildRepository.getAllBuilds(pipelineId)

        return allBuilds
                .asSequence()
                .flatMap { it.stages.asSequence() }
                .filter {
                    (it.startTimeMillis + it.durationMillis + it.pauseDurationMillis) in startTimestamp..endTimestamp
                }
                .any { it.name == targetStage }
    }

    abstract fun fetchAllBuilds(dashboardId: String, pipelineId: String): List<Build>

    abstract fun verifyPipeline(url: String, username: String, credential: String)

}
