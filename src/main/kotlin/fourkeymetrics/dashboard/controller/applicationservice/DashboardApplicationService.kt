package fourkeymetrics.dashboard.controller.applicationservice

import fourkeymetrics.dashboard.controller.vo.request.DashboardRequest
import fourkeymetrics.dashboard.controller.vo.response.DashboardDetailResponse
import fourkeymetrics.dashboard.controller.vo.response.DashboardResponse
import fourkeymetrics.dashboard.controller.vo.response.PipelineStagesResponse
import fourkeymetrics.dashboard.exception.DashboardNameDuplicateException
import fourkeymetrics.dashboard.model.Dashboard
import fourkeymetrics.dashboard.repository.DashboardRepository
import fourkeymetrics.dashboard.repository.PipelineRepository
import fourkeymetrics.dashboard.service.jenkins.JenkinsPipelineService
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DashboardApplicationService {
    @Autowired
    private lateinit var jenkinsPipelineService: JenkinsPipelineService

    @Autowired
    private lateinit var pipelineApplicationService: PipelineApplicationService

    @Autowired
    private lateinit var dashboardRepository: DashboardRepository

    @Autowired
    private lateinit var pipelineRepository: PipelineRepository

    @Transactional
    fun createDashboard(dashboardRequest: DashboardRequest): DashboardDetailResponse {
        val dashboardName = dashboardRequest.dashboardName
        if (dashboardRepository.existWithGivenName(dashboardName)) {
            throw DashboardNameDuplicateException()
        }

        val dashboard = dashboardRepository.save(Dashboard(name = dashboardName, id = ObjectId().toString()))
        val pipeline = dashboardRequest.pipeline.toPipeline(dashboard.id, ObjectId().toString())
        pipelineApplicationService.verifyPipelineConfiguration(pipeline)

        return DashboardDetailResponse(dashboard, pipelineRepository.saveAll(listOf(pipeline)))
    }

    fun updateDashboardName(dashboardId: String, dashboardName: String): DashboardResponse {
        val dashboard = dashboardRepository.findById(dashboardId)
        dashboard.name = dashboardName
        return DashboardResponse(dashboardRepository.save(dashboard))
    }

    fun getDashboards(): List<DashboardResponse> {
        return dashboardRepository.findAll().map { DashboardResponse(it) }
    }

    @Transactional
    fun deleteDashboard(dashboardId: String) {
        pipelineRepository.deleteByDashboardId(dashboardId)
        dashboardRepository.deleteById(dashboardId)
    }

    fun getPipelineStages(dashboardId: String): List<PipelineStagesResponse> {
        verifyDashboardExist(dashboardId)
        return pipelineRepository.findByDashboardId(dashboardId)
            .map {
                PipelineStagesResponse(it.id, it.name, jenkinsPipelineService.getStagesSortedByName(it.id))
            }
            .sortedBy { it.pipelineName.toUpperCase() }
    }

    private fun verifyDashboardExist(dashboardId: String) {
        dashboardRepository.findById(dashboardId)
    }

    fun getDashboardDetails(dashboardId: String): DashboardDetailResponse {
        val dashboard = dashboardRepository.findById(dashboardId)
        val pipelines = pipelineRepository.findByDashboardId(dashboardId)

        return DashboardDetailResponse(dashboard, pipelines)
    }
}