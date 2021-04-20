package fourkeymetrics.project.controller.applicationservice

data class SyncProgress(
    val pipelineId: String,
    val progress: Int,
    val batchSize: Int
) {
    override fun toString(): String {
        return "Pipeline [$pipelineId]: $progress/$batchSize"
    }
}