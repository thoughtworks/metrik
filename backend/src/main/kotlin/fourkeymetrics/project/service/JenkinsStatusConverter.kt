package fourkeymetrics.project.service

import fourkeymetrics.common.model.Status
import org.springframework.stereotype.Component

@Component
class JenkinsStatusConverter : ConvertStatus {
    override fun fromStageToUnifiedStatus(inputStageStatus: String?): Status {
        return when (inputStageStatus) {
            "SUCCESS" -> {
                Status.SUCCESS
            }
            "FAILED" -> {
                Status.FAILED
            }
            "IN_PROGRESS" -> {
                Status.IN_PROGRESS
            }
            else -> {
                Status.OTHER
            }
        }
    }

    override fun fromBuildToUnifiedStatus(inputBuildStatus: String?): Status {
        return when (inputBuildStatus) {
            null -> {
                Status.IN_PROGRESS
            }
            "SUCCESS" -> {
                Status.SUCCESS
            }
            "FAILURE" -> {
                Status.FAILED
            }
            else -> {
                Status.OTHER
            }
        }
    }
}