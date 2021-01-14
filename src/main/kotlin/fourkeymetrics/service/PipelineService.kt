package fourkeymetrics.service

interface PipelineService {
    /**
     * Existence check for single branch pipeline
     */
    fun hasPipeline(pipelineName: String): Boolean

    /**
     * Existence check for multi branch pipeline
     */
    fun hasPipeline(pipelineName: String, branch: String): Boolean

    fun hasStage(pipelineName: String, targetStage: String): Boolean

}