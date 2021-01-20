package fourkeymetrics.pipeline

import fourkeymetrics.model.Build
import fourkeymetrics.repository.BuildRepository
import fourkeymetrics.repository.PipelineRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
abstract class Pipeline {
    @Autowired
    private lateinit var pipelineRepository: PipelineRepository

    @Autowired
    private lateinit var buildRepository: BuildRepository

    fun hasPipeline(dashboardId: String, pipelineId: String): Boolean {
        return pipelineRepository.getPipeline(dashboardId, pipelineId) != null
    }

    fun hasStageInLastBuild(pipelineId: String, targetStage: String): Boolean {
        val lastBuild = buildRepository.getLastBuild(pipelineId)

        return lastBuild != null && lastBuild.stages.find { it.name == targetStage } != null
    }

    abstract fun fetchAllBuilds(dashboardId: String, pipelineId: String): List<Build>
}
