package fourkeymetrics.project.controller.vo.response

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import fourkeymetrics.project.model.Project
import fourkeymetrics.project.model.Pipeline
import org.apache.logging.log4j.util.Strings


@JsonSerialize(include=JsonSerialize.Inclusion.NON_EMPTY)
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

