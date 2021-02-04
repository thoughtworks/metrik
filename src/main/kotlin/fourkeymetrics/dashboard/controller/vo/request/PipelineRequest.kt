package fourkeymetrics.dashboard.controller.vo.request

import fourkeymetrics.dashboard.model.Pipeline
import fourkeymetrics.dashboard.model.PipelineType
import org.apache.logging.log4j.util.Strings

data class PipelineRequest(
    val name: String = Strings.EMPTY,
    val username: String = Strings.EMPTY,
    val credential: String = Strings.EMPTY,
    val url: String = Strings.EMPTY,
    var type: PipelineType = PipelineType.JENKINS
) {
    fun toPipeline(dashboardId: String, pipelineId: String): Pipeline {
        return with(this) { Pipeline(pipelineId, dashboardId, name, username, credential, url, type) }
    }
}