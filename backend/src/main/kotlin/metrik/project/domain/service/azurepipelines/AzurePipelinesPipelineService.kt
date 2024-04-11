package metrik.project.domain.service.azurepipelines

import feign.FeignException
import feign.codec.DecodeException
import metrik.project.domain.model.Execution
import metrik.project.domain.model.PipelineConfiguration
import metrik.project.domain.repository.BuildRepository
import metrik.project.domain.service.PipelineService
import metrik.project.domain.service.githubactions.GithubActionsPipelineService
import metrik.project.exception.PipelineConfigVerifyException
import metrik.project.infrastructure.azure.feign.AzureFeignClient
import metrik.project.rest.vo.response.SyncProgress
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.net.URL

@Service("azurePipelinesPipelineService")
class AzurePipelinesPipelineService(
    private val azureFeignClient: AzureFeignClient,
    private val buildRepository: BuildRepository,
) : PipelineService {
    private var logger = LoggerFactory.getLogger(javaClass.name)
    override fun syncBuildsProgressively(
        pipeline: PipelineConfiguration,
        emitCb: (SyncProgress) -> Unit
    ): List<Execution> {
        TODO("Not yet implemented")
    }

    override fun verifyPipelineConfiguration(pipeline: PipelineConfiguration) {
        logger.info(
            "Started verification for Azure Pipelines [name: ${pipeline.name}, url: ${pipeline.url}, " +
                    "type: ${pipeline.type}]"
        )

        try {
            val (organization, project) = getOrganizationProjectFromUrl(pipeline.url)
            azureFeignClient.retrieveMultiplePipelines(pipeline.credential, organization, project)
                ?: throw PipelineConfigVerifyException("Verify failed")
        } catch (ex: FeignException.FeignServerException) {
            throw PipelineConfigVerifyException("Verify website unavailable")
        } catch (ex: FeignException.FeignClientException) {
            throw PipelineConfigVerifyException("Verify failed")
        } catch (ex: FeignException) {
            throw PipelineConfigVerifyException("Verify failed")
        }
    }

    override fun getStagesSortedByName(pipelineId: String): List<String> {
        return buildRepository.getAllBuilds(pipelineId)
            .flatMap { it.stages }
            .map { it.name }
            .distinct()
            .sortedBy { it.uppercase() }
            .toList()
    }

    private fun getOrganizationProjectFromUrl(url: String): Pair<String, String> {
        val components = URL(url).path.split("/")
        val organization = components[components.size - organizationIndex]
        val project = components.last()
        return Pair(organization, project)
    }

    private companion object {
        const val organizationIndex = 2
    }
}