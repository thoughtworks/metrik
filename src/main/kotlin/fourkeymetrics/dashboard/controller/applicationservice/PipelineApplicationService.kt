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

    // todo[Rong & Binfang]: need to verify whether the pipleline name already exist
    fun createPipeline(dashboardId: String, pipelineResponse: PipelineRequest): PipelineResponse {
        val dashboard = dashboardRepository.findById(dashboardId)

        val pipeline = Pipeline(
            ObjectId().toString(),
            dashboard.id,
            pipelineResponse.name,
            pipelineResponse.username,
            pipelineResponse.credential,
            pipelineResponse.url,
            pipelineResponse.type
        )

        return PipelineResponse.buildFrom(pipelineRepository.save(pipeline))
    }

    fun updatePipeline(dashboardId: String, pipelineId: String, pipelineResponse: PipelineRequest): PipelineResponse {
        findAndValidatePipeline(dashboardId, pipelineId)

        val pipeline =
            with(pipelineResponse) { Pipeline(pipelineId, dashboardId, name, username, credential, url, type) }

        return PipelineResponse.buildFrom(pipelineRepository.save(pipeline))
    }

    fun getPipeline(dashboardId: String, pipelineId: String): PipelineResponse {
        val pipeline = findAndValidatePipeline(dashboardId, pipelineId)
        return PipelineResponse.buildFrom(pipeline)
    }


    // todo[Rong & Binfang]: need to delete builds
    fun deletePipeline(dashboardId: String, pipelineId: String) {
        findAndValidatePipeline(dashboardId, pipelineId)
        pipelineRepository.deleteById(pipelineId)
    }


    private fun verifyPipeline(url: String, username: String, credential: String, type: PipelineType) {
        if (PipelineType.JENKINS == type) {
            jenkinsPipelineService.verifyPipelineConfiguration(url, username, credential)
        } else {
            throw ApplicationException(HttpStatus.BAD_REQUEST, "Pipeline type not support")
        }
    }

    private fun findAndValidatePipeline(
        dashboardId: String,
        pipelineId: String
    ): Pipeline {
        dashboardRepository.findById(dashboardId)
        return pipelineRepository.findById(pipelineId)
    }

}