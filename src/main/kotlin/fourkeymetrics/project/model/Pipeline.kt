package fourkeymetrics.project.model

import org.apache.logging.log4j.util.Strings
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "pipeline")
data class Pipeline(
    @Id
    val id: String = Strings.EMPTY,
    var projectId: String = Strings.EMPTY,
    val name: String = Strings.EMPTY,
    val username: String = Strings.EMPTY,
    var credential: String = Strings.EMPTY,
    val url: String = Strings.EMPTY,
    var type: PipelineType = PipelineType.JENKINS,
)
