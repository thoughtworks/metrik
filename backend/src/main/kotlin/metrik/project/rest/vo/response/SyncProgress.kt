package metrik.project.rest.vo.response

data class SyncProgress(
    val pipelineId: String,
    val pipelineName: String,
    val progress: Int,
    val batchSize: Int
) {
    override fun toString(): String {
        return "Pipeline [$pipelineId - $pipelineName]: $progress/$batchSize"
    }
}