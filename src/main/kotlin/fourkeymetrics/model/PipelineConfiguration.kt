package fourkeymetrics.model

import org.apache.logging.log4j.util.Strings

data class PipelineConfiguration(
    val userName: String = Strings.EMPTY,
    val token: String = Strings.EMPTY,
    val url: String = Strings.EMPTY
)
