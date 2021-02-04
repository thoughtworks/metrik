package fourkeymetrics.dashboard.controller.applicationservice

import fourkeymetrics.dashboard.controller.vo.request.PipelineRequest
import fourkeymetrics.dashboard.controller.vo.request.PipelineVerificationRequest
import fourkeymetrics.dashboard.controller.vo.response.PipelineResponse
import fourkeymetrics.dashboard.model.PipelineType
import fourkeymetrics.dashboard.repository.BuildRepository
import fourkeymetrics.dashboard.repository.DashboardRepository
import fourkeymetrics.dashboard.repository.PipelineRepository
import fourkeymetrics.dashboard.service.jenkins.JenkinsPipelineService
import fourkeymetrics.exception.BadRequestException
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PipelineApplicationService {
    @Autowired
    private lateinit var jenkinsPipelineService: JenkinsPipelineService

    @Autowired
    private lateinit var pipelineRepository: PipelineRepository

    @Autowired
    private lateinit var dashboardRepository: DashboardRepository

    @Autowired
    private lateinit var buildRepository: BuildRepository

    fun verifyPipelineConfiguration(pipelineVerificationRequest: PipelineVerificationRequest) {
        if (PipelineType.JENKINS == pipelineVerificationRequest.type) {
            jenkinsPipelineService.verifyPipelineConfiguration(
                pipelineVerificationRequest.url,
                pipelineVerificationRequest.username,
                pipelineVerificationRequest.credential
            )
        } else {
            throw BadRequestException("Pipeline type not support")
        }
    }

    fun createPipeline(dashboardId: String, pipelineRequest: PipelineRequest): PipelineResponse {
        verifyDashboardExist(dashboardId)

        if (pipelineRepository.pipelineExistWithNameAndDashboardId(pipelineRequest.name, dashboardId)) {
            throw BadRequestException("Pipeline name already exist")
        } else {
            val pipeline = pipelineRequest.toPipeline(dashboardId, ObjectId().toString())
            pipelineRepository.save(pipeline)
            return PipelineResponse(pipeline)
        }
    }

    fun updatePipeline(dashboardId: String, pipelineId: String, pipelineRequest: PipelineRequest): PipelineResponse {
        verifyDashboardExist(dashboardId)
        verifyPipelineExist(pipelineId)

        val pipeline = pipelineRequest.toPipeline(dashboardId, pipelineId)
        return PipelineResponse(pipelineRepository.save(pipeline))
    }

    fun getPipeline(dashboardId: String, pipelineId: String): PipelineResponse {
        verifyDashboardExist(dashboardId)
        val pipeline = pipelineRepository.findById(pipelineId)
        return PipelineResponse(pipeline)
    }

    fun deletePipeline(dashboardId: String, pipelineId: String) {
        verifyDashboardExist(dashboardId)
        verifyPipelineExist(pipelineId)
        pipelineRepository.deleteById(pipelineId)
        buildRepository.clear(pipelineId)
    }

    private fun verifyDashboardExist(dashboardId: String) =
        dashboardRepository.findById(dashboardId)

    private fun verifyPipelineExist(pipelineId: String) =
        pipelineRepository.findById(pipelineId)
}