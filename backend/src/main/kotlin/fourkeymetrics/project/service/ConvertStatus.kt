package fourkeymetrics.project.service

import fourkeymetrics.common.model.Status

interface ConvertStatus {

    fun fromStageToUnifiedStatus(inputStageStatus: String?): Status

    fun fromBuildToUnifiedStatus(inputBuildStatus: String?): Status

}