package fourkeymetrics.project.service.bamboo

import fourkeymetrics.common.model.Build
import fourkeymetrics.common.model.Status
import fourkeymetrics.exception.ApplicationException
import fourkeymetrics.project.model.Pipeline
import fourkeymetrics.project.service.PipelineService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange

@Service
class BambooPipelineService(@Autowired private var restTemplate: RestTemplate) : PipelineService() {
    override fun verifyPipelineConfiguration(pipeline: Pipeline) {
        val headers = setAuthHeader(pipeline.credential)
        val entity = HttpEntity<String>(headers)
        try {
            restTemplate.exchange<String>(
                "${pipeline.url}/rest/api/latest/project/", HttpMethod.GET, entity)
        } catch (ex: HttpServerErrorException) {
            throw ApplicationException(HttpStatus.SERVICE_UNAVAILABLE, "Verify website unavailable")
        } catch (ex: HttpClientErrorException) {
            throw ApplicationException(HttpStatus.BAD_REQUEST, "Verify failed")
        }
    }

    override fun syncBuilds(pipelineId: String): List<Build> {
        TODO("Not yet implemented")
    }

    override fun mapStageStatus(statusInPipeline: String?): Status {
        TODO("Not yet implemented")
    }

    override fun mapBuildStatus(statusInPipeline: String?): Status {
        TODO("Not yet implemented")
    }

    private fun setAuthHeader(credential: String): HttpHeaders {
        val headers = HttpHeaders()
        headers.setBearerAuth(credential)
        return headers
    }
}
