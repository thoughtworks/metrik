package fourkeymetrics.project.service

import fourkeymetrics.common.model.Status
import org.springframework.stereotype.Component

@Component
class NoopStatusConverter : ConvertStatus {
    override fun fromStageToUnifiedStatus(inputStageStatus: String?): Status {
        return Status.OTHER
    }

    override fun fromBuildToUnifiedStatus(inputBuildStatus: String?): Status {
        return Status.OTHER
    }
}