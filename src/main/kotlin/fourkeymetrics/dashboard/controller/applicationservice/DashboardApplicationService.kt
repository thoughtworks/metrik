package fourkeymetrics.dashboard.controller.applicationservice

import fourkeymetrics.dashboard.controller.vo.DashboardDetailVo
import fourkeymetrics.dashboard.controller.vo.DashboardRequest
import fourkeymetrics.dashboard.controller.vo.DashboardVo
import fourkeymetrics.dashboard.controller.vo.PipelineStagesResponse
import fourkeymetrics.dashboard.exception.DashboardNameDuplicateException
import fourkeymetrics.dashboard.model.Dashboard1
import fourkeymetrics.dashboard.model.Pipeline
import fourkeymetrics.dashboard.repository.DashboardRepository1
import fourkeymetrics.dashboard.repository.PipelineRepository
import fourkeymetrics.dashboard.service.jenkins.JenkinsPipelineService
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@SuppressWarnings("TooManyFunctions")
@Service
class DashboardApplicationService {
    @Autowired
    private lateinit var jenkinsPipelineService: JenkinsPipelineService

    @Autowired
    private lateinit var dashboardRepository: DashboardRepository1

    @Autowired
    private lateinit var pipelineRepository: PipelineRepository


    @Transactional
    fun createDashboardAndPipeline(dashboardRequest: DashboardRequest): DashboardDetailVo {
        val dashboardName = dashboardRequest.dashboardName
        if (dashboardRepository.dashboardWithGivenNameExist(dashboardName)) {
            throw DashboardNameDuplicateException()
        }

        val dashboard = dashboardRepository.save(
            Dashboard1(
                name = dashboardName,
                id = ObjectId().toString(),
            )
        )
        val pipelines = with(dashboardRequest.pipeline) {
            listOf(Pipeline(ObjectId().toString(), dashboard.id, name, username, credential, url, type))

        }
        return DashboardDetailVo.buildFrom(dashboard, pipelineRepository.saveAll(pipelines))
    }

    fun updateDashboardName(dashboardId: String, dashboardName: String): DashboardVo {
        val dashboard = dashboardRepository.findById(dashboardId)
        dashboard.name = dashboardName
        return DashboardVo.buildFrom(dashboardRepository.save(dashboard))
    }


    fun getDashboards(): List<DashboardVo> {
        return dashboardRepository.findAll().map { DashboardVo.buildFrom(it) }
    }

    @Transactional
    fun deleteDashboard(dashboardId: String) {
        pipelineRepository.deleteByDashboardId(dashboardId)
        dashboardRepository.deleteById(dashboardId)
    }

    fun getPipelinesStages(dashboardId: String): List<PipelineStagesResponse> {
        val dashboard = dashboardRepository.findById(dashboardId)

        return jenkinsPipelineService.getPipelineStages(dashboard.id)
    }


    fun getDashboard(dashboardId: String): DashboardVo {
        val dashboard = dashboardRepository.findById(dashboardId)

        return DashboardVo.buildFrom(dashboard)
    }

}