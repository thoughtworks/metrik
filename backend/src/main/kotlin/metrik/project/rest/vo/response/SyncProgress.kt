package metrik.project.rest.vo.response

data class SyncProgress(
    val pipelineId: String,
    val pipelineName: String,
    val progress: Int,
    val batchSize: Int,
    val step: Int? = null,
    val stepSize: Int? = null
) {
    override fun toString(): String {
        return "Pipeline [$pipelineId - $pipelineName]: $progress/$batchSize"
    }
}
