package fourkeymetrics.datasource.pipeline.configuration.vo

data class PipelineConfigurationRequest(
    val name:String,
    val url:String,
    val username:String,
    val token:String,
    val type:String
)