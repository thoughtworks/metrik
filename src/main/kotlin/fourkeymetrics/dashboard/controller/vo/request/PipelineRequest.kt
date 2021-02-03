package fourkeymetrics.dashboard.controller.vo.request

import fourkeymetrics.dashboard.model.PipelineType
import org.apache.logging.log4j.util.Strings

data class PipelineRequest(
    val name: String = Strings.EMPTY,
    val username: String = Strings.EMPTY,
    val credential: String = Strings.EMPTY,
    val url: String = Strings.EMPTY,
    var type: PipelineType = PipelineType.JENKINS
)