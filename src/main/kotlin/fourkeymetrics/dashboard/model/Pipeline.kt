package fourkeymetrics.dashboard.model

import org.apache.logging.log4j.util.Strings
import org.springframework.data.annotation.Id

data class Pipeline(
    @Id
    val id: String = Strings.EMPTY,
    val name:String= Strings.EMPTY,
    val username: String = Strings.EMPTY,
    val credential: String = Strings.EMPTY,
    val url: String = Strings.EMPTY,
    var type: PipelineType = PipelineType.JENKINS
)
