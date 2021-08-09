package metrik.project.domain.service.githubactions

import metrik.infrastructure.utlils.RequestUtil
import metrik.infrastructure.utlils.RequestUtil.getDomain
import metrik.project.domain.model.Build
import metrik.project.domain.model.Pipeline
import metrik.project.domain.repository.BuildRepository
import metrik.project.domain.service.PipelineService
import metrik.project.exception.PipelineConfigVerifyException
import metrik.project.rest.vo.response.SyncProgress
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange

@Service("githubActionsPipelineService")
class GithubActionsPipelineService(
    @Autowired
    private var restTemplate: RestTemplate,
    @Autowired
    private var buildRepository: BuildRepository
) : PipelineService{
    private var logger = LoggerFactory.getLogger(this.javaClass.name)

    override fun syncBuildsProgressively(pipeline: Pipeline, emitCb: (SyncProgress) -> Unit): List<Build> {
        TODO("Not yet implemented")
    }

    override fun verifyPipelineConfiguration(pipeline: Pipeline) {
        logger.info("Started verification for Github Actions pipeline [$pipeline]")
        val headers = RequestUtil.buildHeaders(
            mapOf(
                Pair("Authorization", "Bearer ${pipeline.credential}"),
                Pair("Connection", "close")
            )
        )
        val entity = HttpEntity<String>(headers)

        try {
            val url = "${getDomain(pipeline.url)}$urlSuffix"
            logger.info("Github Actions verification - Sending request to [$url] with entity [$entity]")
            val responseEntity = restTemplate.exchange<String>(url, HttpMethod.GET, entity)
            logger.info("Github Actions verification - Response from [$url]: $responseEntity")
        } catch (ex: HttpServerErrorException) {
            throw PipelineConfigVerifyException("Verify website unavailable")
        } catch (ex: HttpClientErrorException) {
            throw PipelineConfigVerifyException("Verify failed")
        }

    }

    override fun getStagesSortedByName(pipelineId: String): List<String> {
        TODO("Not yet implemented")
    }

    private companion object{
        const val urlSuffix = "/actions/runs?per_page=1"
    }

}