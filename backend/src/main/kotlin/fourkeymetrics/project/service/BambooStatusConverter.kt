package fourkeymetrics.project.service

import fourkeymetrics.common.model.Status
import org.springframework.stereotype.Component

@Component
class BambooStatusConverter : ConvertStatus {
    override fun fromStageToUnifiedStatus(inputStageStatus: String?): Status {
        return when (inputStageStatus) {
            "Successful" -> {
                Status.SUCCESS
            }
            "Failed" -> {
                Status.FAILED
            }
            else -> {
                Status.OTHER
            }
        }
    }

    override fun fromBuildToUnifiedStatus(inputBuildStatus: String?): Status {
        return when (inputBuildStatus) {
            "Successful" -> {
                Status.SUCCESS
            }
            "Failed" -> {
                Status.FAILED
            }
            else -> {
                Status.OTHER
            }
        }
    }
}