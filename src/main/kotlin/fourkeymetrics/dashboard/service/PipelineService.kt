package fourkeymetrics.dashboard.service

import fourkeymetrics.common.model.Build
import fourkeymetrics.dashboard.controller.vo.PipelineStagesResponse
import fourkeymetrics.dashboard.model.Pipeline
import fourkeymetrics.dashboard.repository.BuildRepository
import fourkeymetrics.dashboard.repository.DashboardRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
abstract class PipelineService {
    @Autowired
    private lateinit var dashboardRepository: DashboardRepository

    @Autowired
    private lateinit var buildRepository: BuildRepository

    fun hasPipeline(dashboardId: String, pipelineId: String): Boolean {
        return dashboardRepository.getPipelineConfiguration(dashboardId, pipelineId) != null
    }

    fun hasStageInTimeRange(
        pipelineId: String, targetStage: String, startTimestamp: Long,
        endTimestamp: Long
    ): Boolean {
        val allBuilds = buildRepository.getAllBuilds(pipelineId)

        return allBuilds
            .asSequence()
            .flatMap { it.stages.asSequence() }
            .filter {
                (it.startTimeMillis + it.durationMillis + it.pauseDurationMillis) in startTimestamp..endTimestamp
            }
            .any { it.name == targetStage }
    }

    abstract fun syncBuilds(dashboardId: String, pipelineId: String): List<Build>

    abstract fun verifyPipelineConfiguration(url: String, username: String, credential: String)

    fun getPipelineStages(dashboardId: String): List<PipelineStagesResponse> {
        val pipelines: List<Pipeline> = dashboardRepository.getPipelinesByDashboardId(dashboardId).sortedBy { it.name }

        val pipelineStages = mutableListOf<PipelineStagesResponse>()
        for (pipeline in pipelines) {
            val builds: List<Build> = buildRepository.getAllBuilds(pipeline.id)
            val stages = builds.flatMap { it.stages }.map { it.name }.distinct().sorted().toList()

            pipelineStages.add(PipelineStagesResponse(pipeline.name, stages))
        }

        return pipelineStages
    }
}
