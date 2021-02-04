package fourkeymetrics.dashboard.controller.vo.response

import fourkeymetrics.dashboard.model.Pipeline
import fourkeymetrics.dashboard.model.PipelineType
import org.apache.logging.log4j.util.Strings

data class PipelineResponse(
    val id: String = Strings.EMPTY,
    val name: String = Strings.EMPTY,
    val username: String = Strings.EMPTY,
    val credential: String = Strings.EMPTY,
    val url: String = Strings.EMPTY,
    var type: PipelineType = PipelineType.JENKINS
) {
    constructor(pipeline: Pipeline) : this(
        pipeline.id,
        pipeline.name,
        pipeline.username,
        pipeline.credential,
        pipeline.url,
        pipeline.type
    )
}
