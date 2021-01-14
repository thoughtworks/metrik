package fourkeymetrics.service

interface PipelineService {
    fun hasPipeline(pipelineName: String): Boolean

    fun hasStage(pipelineName: String, targetStage: String): Boolean

}