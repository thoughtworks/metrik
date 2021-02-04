package fourkeymetrics.dashboard.controller.vo.response

import fourkeymetrics.dashboard.model.Dashboard
import fourkeymetrics.dashboard.model.Pipeline
import org.apache.logging.log4j.util.Strings


data class DashboardDetailResponse(
    var id: String = Strings.EMPTY,
    var name: String = Strings.EMPTY,
    var synchronizationTimestamp: Long? = null,
    var pipelines: List<PipelineResponse> = emptyList()
) {
    constructor(dashboard: Dashboard, pipelines: List<Pipeline>) : this() {
        this.id = dashboard.id
        this.name = dashboard.name
        this.synchronizationTimestamp = dashboard.synchronizationTimestamp
        this.pipelines = pipelines.map { PipelineResponse(it) }
    }
}
