package fourkeymetrics.service

import fourkeymetrics.repository.BuildRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ChangeFailureRateService {
    @Autowired
    private lateinit var buildRepository: BuildRepository

    @Suppress("MagicNumber")
    fun getChangeFailureRate(
        pipelineId: String,
        targetStage: String,
        startTime: Long,
        endTime: Long
    ): Float {
        val allBuilds = buildRepository.getAllBuilds(pipelineId).sortedBy { it.timestamp }

        return 0.5F
    }
}