package fourkeymetrics.dashboard.controller.vo

import fourkeymetrics.dashboard.model.Pipeline
import fourkeymetrics.dashboard.model.PipelineType
import org.apache.logging.log4j.util.Strings

class PipelineVo(
    val id: String = Strings.EMPTY,
    val name: String = Strings.EMPTY,
    val username: String = Strings.EMPTY,
    val credential: String = Strings.EMPTY,
    val url: String = Strings.EMPTY,
    var type: PipelineType = PipelineType.JENKINS
) {
    companion object {
        fun buildFrom(pipeline: Pipeline): PipelineVo {
            return PipelineVo(
                pipeline.id,
                pipeline.name,
                pipeline.username,
                pipeline.credential,
                pipeline.url,
                pipeline.type
            )
        }
    }
}
