package metrik.project.controller.applicationservice

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