package fourkeymetrics.dashboard.controller.vo.response

import fourkeymetrics.dashboard.model.Dashboard
import org.apache.logging.log4j.util.Strings


class DashboardResponse(
    val id: String = Strings.EMPTY,
    var name: String = Strings.EMPTY,
    val synchronizationTimestamp: Long? = null,
) {
    companion object {
        fun buildFrom(dashboard: Dashboard): DashboardResponse {
            return DashboardResponse(dashboard.id, dashboard.name, dashboard.synchronizationTimestamp)
        }
    }
}
