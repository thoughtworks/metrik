package fourkeymetrics.dashboard.controller.applicationservice

import fourkeymetrics.dashboard.controller.vo.request.PipelineRequest
import fourkeymetrics.dashboard.controller.vo.request.PipelineVerificationRequest
import fourkeymetrics.dashboard.controller.vo.response.PipelineResponse
import fourkeymetrics.dashboard.model.Pipeline
import fourkeymetrics.dashboard.model.PipelineType
import fourkeymetrics.dashboard.repository.DashboardRepository
import fourkeymetrics.dashboard.repository.PipelineRepository
import fourkeymetrics.dashboard.service.jenkins.JenkinsPipelineService
import fourkeymetrics.exception.ApplicationException
import fourkeymetrics.exception.BadRequestException
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@SuppressWarnings("TooManyFunctions")
@Service
class PipelineApplicationService {
    @Autowired
    private lateinit var jenkinsPipelineService: JenkinsPipelineService

    @Autowired
    private lateinit var pipelineRepository: PipelineRepository

    @Autowired
    private lateinit var dashboardRepository: DashboardRepository

    fun verifyPipeline(pipelineVerificationRequest: PipelineVerificationRequest) {
        with(pipelineVerificationRequest) {
            verifyPipeline(url, username, credential, type)
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

        val pipeline =
            with(pipelineRequest) { Pipeline(pipelineId, dashboardId, name, username, credential, url, type) }

        return PipelineResponse(pipelineRepository.save(pipeline))
    }

    fun getPipeline(dashboardId: String, pipelineId: String): PipelineResponse {
        verifyDashboardExist(dashboardId)
        val pipeline = pipelineRepository.findById(pipelineId)
        return PipelineResponse(pipeline)
    }

    // todo[Rong & Binfang]: need to delete builds
    fun deletePipeline(dashboardId: String, pipelineId: String) {
        verifyDashboardExist(dashboardId)
        verifyPipelineExist(pipelineId)
        pipelineRepository.deleteById(pipelineId)
    }

    private fun verifyPipeline(url: String, username: String, credential: String, type: PipelineType) {
        if (PipelineType.JENKINS == type) {
            jenkinsPipelineService.verifyPipelineConfiguration(url, username, credential)
        } else {
            throw ApplicationException(HttpStatus.BAD_REQUEST, "Pipeline type not support")
        }
    }

    private fun verifyDashboardExist(dashboardId: String) =
        dashboardRepository.findById(dashboardId)

    private fun verifyPipelineExist(pipelineId: String) =
        pipelineRepository.findById(pipelineId)
}