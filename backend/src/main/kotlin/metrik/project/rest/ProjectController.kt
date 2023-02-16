package metrik.project.rest

import metrik.project.rest.applicationservice.ProjectApplicationService
import metrik.project.rest.vo.request.ProjectRequest
import metrik.project.rest.vo.request.UpdateProjectRequest
import metrik.project.rest.vo.response.ProjectDetailResponse
import metrik.project.rest.vo.response.ProjectResponse
import metrik.project.rest.vo.response.ProjectSummaryResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/api")
class ProjectController {
    @Autowired
    private lateinit var projectApplicationService: ProjectApplicationService

    @GetMapping("/project")
    @ResponseStatus(HttpStatus.OK)
    fun getProjects(): List<ProjectResponse> {
        return projectApplicationService.getProjects()
    }

    @GetMapping("/project/{projectId}")
    @ResponseStatus(HttpStatus.OK)
    fun getProjectDetails(@PathVariable projectId: String): ProjectDetailResponse {
        return projectApplicationService.getProjectDetails(projectId)
    }

    @PostMapping("/project")
    fun createProject(@RequestBody @Valid projectRequest: ProjectRequest): ProjectSummaryResponse {
        return projectApplicationService.createProject(projectRequest)
    }

    @PutMapping("/project/{projectId}")
    @ResponseStatus(HttpStatus.OK)
    fun updateProjectName(
        @PathVariable projectId: String,
        @RequestBody @Valid updateProjectRequest: UpdateProjectRequest
    ) {
        projectApplicationService.updateProjectName(projectId, updateProjectRequest)
    }

    @DeleteMapping("/project/{projectId}")
    @ResponseStatus(HttpStatus.OK)
    fun deleteProject(@PathVariable projectId: String) {
        return projectApplicationService.deleteProject(projectId)
    }
}
