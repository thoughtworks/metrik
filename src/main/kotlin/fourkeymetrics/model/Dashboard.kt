package fourkeymetrics.model

import org.apache.logging.log4j.util.Strings

data class PipelineConfiguration(
    val id: String = Strings.EMPTY,
    val username: String = Strings.EMPTY,
    val token: String = Strings.EMPTY,
    val url: String = Strings.EMPTY,
    var type: PipelineType = PipelineType.JENKINS
)

data class Dashboard(
    val id: String = Strings.EMPTY,
    val name: String = Strings.EMPTY,
    val pipelineConfigurations: List<PipelineConfiguration> = emptyList()
)