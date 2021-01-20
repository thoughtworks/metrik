package fourkeymetrics.pipeline

import fourkeymetrics.model.Build
import fourkeymetrics.repository.BuildRepository
import fourkeymetrics.repository.DashboardRepository
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
        val buildsInTimeRange = buildRepository.getBuildsByTimePeriod(pipelineId,
            startTimestamp, endTimestamp)

        return buildsInTimeRange.isNotEmpty() && buildsInTimeRange.flatMap { it.stages }
            .find { it.name == targetStage } != null
    }

    abstract fun fetchAllBuilds(dashboardId: String, pipelineId: String): List<Build>

    abstract fun verifyPipeline(url:String,username:String,token:String): Boolean

}
