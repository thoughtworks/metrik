package fourkeymetrics.project.controller.vo.response

import fourkeymetrics.project.model.Pipeline
import fourkeymetrics.project.model.PipelineType
import org.apache.logging.log4j.util.Strings

data class PipelineResponse(
    val id: String = Strings.EMPTY,
    val name: String = Strings.EMPTY,
    val username: String? = null,
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
