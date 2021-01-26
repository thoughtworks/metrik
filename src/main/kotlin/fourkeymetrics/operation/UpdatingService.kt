package fourkeymetrics.operation

import fourkeymetrics.datasource.UpdateRecord
import fourkeymetrics.datasource.UpdateRepository
import fourkeymetrics.datasource.pipeline.builddata.BuildRepository
import fourkeymetrics.datasource.pipeline.builddata.Pipeline
import fourkeymetrics.metric.model.Build
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class UpdatingService {
    companion object {
        private const val TWO_WEEKS_TIMESTAMP = 14 * 24 * 60 * 60 * 1000L
    }

    @Autowired
    private lateinit var buildRepository: BuildRepository

    @Autowired
    private lateinit var updateRepository: UpdateRepository

    @Autowired
    private lateinit var pipeline: Pipeline


    fun update(dashboardId: String, pipelineId: String): Long? {
        val lastUpdate = updateRepository.getLastUpdate()
        val currentTimeMillis = System.currentTimeMillis()
        val builds: List<Build>

        try {
            builds = if (lastUpdate == null) {
                pipeline.fetchBuilds(dashboardId, pipelineId, 0)
            } else {
                pipeline.fetchBuilds(
                    dashboardId,
                    pipelineId,
                    lastUpdate.updateTimestamp - TWO_WEEKS_TIMESTAMP
                )
            }
        } catch (e: Exception) {
            return null
        }

        buildRepository.save(builds)
        updateRepository.save(UpdateRecord(currentTimeMillis))

        return currentTimeMillis
    }
}