package metrik.project.rest.vo.response

import com.fasterxml.jackson.annotation.JsonInclude
import metrik.project.domain.model.Project
import metrik.project.domain.model.Pipeline
import org.apache.logging.log4j.util.Strings

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class ProjectDetailResponse(
    var id: String = Strings.EMPTY,
    var name: String = Strings.EMPTY,
    var synchronizationTimestamp: Long? = null,
    var pipelines: List<PipelineResponse> = emptyList()
) {
    constructor(project: Project, pipelines: List<Pipeline>) : this() {
        this.id = project.id
        this.name = project.name
        this.synchronizationTimestamp = project.synchronizationTimestamp
        this.pipelines = pipelines.map { PipelineResponse(it) }
    }
}

data class ProjectSummaryResponse(
    var id: String = Strings.EMPTY,
    var name: String = Strings.EMPTY,
) {
    constructor(project: Project) : this() {
        this.id = project.id
        this.name = project.name
    }
}

