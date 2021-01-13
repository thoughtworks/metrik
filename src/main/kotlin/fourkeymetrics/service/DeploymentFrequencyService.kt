package fourkeymetrics.service

import org.springframework.stereotype.Service

@Service
class DeploymentFrequencyService {

    fun getDeploymentCount(jobName: String, targetEnvironment: String, startTime: Long, endTime: Long): Int {
        TODO()
    }
}