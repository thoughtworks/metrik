package fourkeymetrics.dashboard.controller.vo

data class PipelineRequest(
    val name:String,
    val url:String,
    val username:String,
    val token:String,
    val type:String
)