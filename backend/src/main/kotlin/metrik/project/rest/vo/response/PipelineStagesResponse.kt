package metrik.project.rest.vo.response

data class PipelineStagesResponse(val pipelineId: String, val pipelineName: String, val stages: List<String>)
