package fourkeymetrics.model

import org.apache.logging.log4j.util.Strings
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

data class PipelineConfiguration(
    @Id
    val id: String = Strings.EMPTY,
    val name:String= Strings.EMPTY,
    val username: String = Strings.EMPTY,
    val credential: String = Strings.EMPTY,
    val url: String = Strings.EMPTY,
    var type: PipelineType = PipelineType.JENKINS
)
@Document(collection = "dashboard")
data class Dashboard(
    @Id
    val id: String = Strings.EMPTY,
    var name: String = Strings.EMPTY,
    var pipelineConfigurations: List<PipelineConfiguration> = emptyList()
)