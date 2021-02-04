package fourkeymetrics.dashboard.controller.vo.response

import fourkeymetrics.dashboard.model.Dashboard
import org.apache.logging.log4j.util.Strings


class DashboardResponse(
    var id: String = Strings.EMPTY,
    var name: String = Strings.EMPTY,
    var synchronizationTimestamp: Long? = null,
) {
    constructor(dashboard: Dashboard) : this() {
        this.id = dashboard.id
        this.name = dashboard.name
        this.synchronizationTimestamp = dashboard.synchronizationTimestamp
    }

}
