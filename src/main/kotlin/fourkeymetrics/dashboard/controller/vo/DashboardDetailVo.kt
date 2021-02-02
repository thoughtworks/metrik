package fourkeymetrics.dashboard.controller.vo

import fourkeymetrics.dashboard.model.Dashboard1
import fourkeymetrics.dashboard.model.Pipeline
import org.apache.logging.log4j.util.Strings


class DashboardDetailVo(
    val id: String = Strings.EMPTY,
    var name: String = Strings.EMPTY,
    val synchronizationTimestamp: Long? = null,
    var pipelines: List<PipelineVo> = emptyList()
) {
    companion object {
        fun buildFrom(dashboard: Dashboard1, pipelines: List<Pipeline>): DashboardDetailVo {
            val dashboardDetailVo = DashboardDetailVo(dashboard.id, dashboard.name)
            val pipelineVos = pipelines
                .map { PipelineVo.buildFrom(it) }

            dashboardDetailVo.pipelines = pipelineVos
            return dashboardDetailVo
        }
    }
}
