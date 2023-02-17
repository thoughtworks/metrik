package metrik.project.rest.applicationservice

import metrik.project.domain.model.Project
import metrik.project.domain.repository.BuildRepository
import metrik.project.domain.repository.PipelineRepository
import metrik.project.domain.repository.ProjectRepository
import metrik.project.exception.ProjectNameDuplicateException
import metrik.project.rest.vo.request.ProjectRequest
import metrik.project.rest.vo.request.UpdateProjectRequest
import metrik.project.rest.vo.response.ProjectDetailResponse
import metrik.project.rest.vo.response.ProjectResponse
import metrik.project.rest.vo.response.ProjectSummaryResponse
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProjectApplicationService {
    @Autowired
    private lateinit var pipelineApplicationService: PipelineApplicationService

    @Autowired
    private lateinit var projectRepository: ProjectRepository

    @Autowired
    private lateinit var pipelineRepository: PipelineRepository

    @Autowired
    private lateinit var buildRepository: BuildRepository

    @Transactional
    fun createProject(projectRequest: ProjectRequest): ProjectSummaryResponse {
        val projectName = projectRequest.projectName
        verifyProjectNameNotDuplicate(projectName)

        val projectId = ObjectId().toString()
        val pipelineId = ObjectId().toString()
        val pipeline = projectRequest.pipeline.toPipeline(projectId, pipelineId)
        pipelineApplicationService.verifyPipelineConfiguration(pipeline)

        val savedProject = projectRepository.save(Project(name = projectName, id = projectId))
        pipelineRepository.saveAll(listOf(pipeline))
        return ProjectSummaryResponse(savedProject)
    }

    fun updateProjectName(projectId: String, updateProjectRequest: UpdateProjectRequest): ProjectResponse {
        val projectName = updateProjectRequest.projectName
        verifyProjectNameNotDuplicate(projectName)
        val project = projectRepository.findById(projectId)
        project.name = projectName
        return ProjectResponse(projectRepository.save(project))
    }

    private fun verifyProjectNameNotDuplicate(projectName: String) {
        if (projectRepository.existWithGivenName(projectName)) {
            throw ProjectNameDuplicateException()
        }
    }

    fun getProjects(): List<ProjectResponse> {
        return projectRepository.findAll().map { ProjectResponse(it) }
    }

    @Transactional
    fun deleteProject(projectId: String) {
        val project = projectRepository.findById(projectId)
        val pipelines = pipelineRepository.findByProjectId(project.id)
        pipelines.forEach {
            pipelineRepository.deleteById(it.id)
            buildRepository.clear(it.id)
        }
        projectRepository.deleteById(project.id)
    }

    fun getProjectDetails(projectId: String): ProjectDetailResponse {
        val project = projectRepository.findById(projectId)
        val pipelines = pipelineRepository.findByProjectId(projectId)

        return ProjectDetailResponse(project, pipelines)
    }
}
