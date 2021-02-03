package fourkeymetrics.dashboard.controller.vo.response

import fourkeymetrics.dashboard.model.Dashboard
import fourkeymetrics.dashboard.model.Pipeline
import org.apache.logging.log4j.util.Strings


data class DashboardDetailResponse(
    val id: String = Strings.EMPTY,
    var name: String = Strings.EMPTY,
    val synchronizationTimestamp: Long? = null,
    var pipelines: List<PipelineResponse> = emptyList()
) {
    companion object {
        fun buildFrom(dashboard: Dashboard, pipelines: List<Pipeline>): DashboardDetailResponse {
            val dashboardDetailVo = DashboardDetailResponse(dashboard.id, dashboard.name)
            val pipelineVos = pipelines
                .map { PipelineResponse.buildFrom(it) }

            dashboardDetailVo.pipelines = pipelineVos
            return dashboardDetailVo
        }
    }
}
