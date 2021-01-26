package fourkeymetrics.datasource.dashboard.controller.vo

data class PipelineConfigurationRequest(
    val name:String,
    val url:String,
    val username:String,
    val token:String,
    val type:String
)