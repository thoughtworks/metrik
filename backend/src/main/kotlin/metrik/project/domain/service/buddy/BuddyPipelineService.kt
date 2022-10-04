package metrik.project.domain.service.buddy

import feign.FeignException
import metrik.project.domain.model.Commit
import metrik.project.domain.model.Execution
import metrik.project.domain.model.PipelineConfiguration
import metrik.project.domain.model.Stage
import metrik.project.domain.model.Status
import metrik.project.domain.repository.BuildRepository
import metrik.project.domain.service.PipelineService
import metrik.project.exception.PipelineConfigVerifyException
import metrik.project.infrastructure.buddy.feign.BuddyFeignClient
import metrik.project.rest.vo.response.SyncProgress
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.net.URI
import java.net.URL
import java.util.concurrent.atomic.AtomicInteger
import kotlin.streams.toList

@Service
class BuddyPipelineService(
    private val buddyFeignClient: BuddyFeignClient,
    private val buildRepository: BuildRepository
) : PipelineService {
    private val logger = LoggerFactory.getLogger(javaClass.name)

    @Synchronized
    @Suppress("MaxLineLength")
    override fun syncBuildsProgressively(pipeline: PipelineConfiguration, emitCb: (SyncProgress) -> Unit): List<Execution> {
        logger.info("Started data sync for Buddy pipeline [name: ${pipeline.name}, url: ${pipeline.url}]")
        val buildsNeedToSync = getExecutionInfoList(pipeline, emitCb)
            .parallelStream()
            .filter {
                val buildInDB = buildRepository.getByBuildNumber(pipeline.id, it.id)
                buildInDB == null || buildInDB.result == Status.IN_PROGRESS
            }
            .toList()

        val progressCounter = AtomicInteger(0)
        logger.info("For Buddy pipeline [${pipeline.id}] - found [${buildsNeedToSync.size}] builds need to be synced")
        val builds = buildsNeedToSync.parallelStream().map { executionInfo ->
            val executionDetails = getExecutionDetails(pipeline, executionInfo)
            val executionChangeSet = getExecutionChangeSet(pipeline, executionInfo)

            val convertedExecution = Execution(
                pipeline.id,
                executionInfo.id,
                executionInfo.getExecutionStatus(),
                executionInfo.getDuration(),
                executionInfo.getTimestamp(),
                executionInfo.url,
                executionInfo.branch.name,
                executionDetails.getExecutionStages(),
                executionChangeSet.commits.map {
                    Commit(it.revision, it.getTimestamp(), it.getDateString())
                }
            )

            emitCb(
                SyncProgress(
                    pipeline.id,
                    pipeline.name,
                    progressCounter.incrementAndGet(),
                    buildsNeedToSync.size,
                    executionDetailsStep,
                    stepSize
                )
            )
            logger.info("[${pipeline.id}] sync progress: [${progressCounter.get()}/${buildsNeedToSync.size}]")
            convertedExecution
        }.toList()

        buildRepository.save(builds)

        logger.info("For Buddy pipeline [${pipeline.id}] - Successfully synced [${buildsNeedToSync.size}] builds")
        return builds
    }

    override fun verifyPipelineConfiguration(pipeline: PipelineConfiguration) {
        try {
            val (projectName, pipelineId) = getProjectNameAndPipelineIdFromUrl(pipeline.url)
            val pipelineUrl = URI.create(pipeline.url)
            val buddyPipeline = buddyFeignClient.getPipeline(pipelineUrl, pipeline.credential)
            if (projectName != buddyPipeline.project.name || pipelineId != "${buddyPipeline.id}") {
                throw PipelineConfigVerifyException("Invalid pipeline resource")
            }
        } catch (ex: FeignException.FeignServerException) {
            throw PipelineConfigVerifyException("Verify website unavailable")
        } catch (ex: FeignException.FeignClientException) {
            throw PipelineConfigVerifyException("Verify failed")
        }
    }

    override fun getStagesSortedByName(pipelineId: String) =
        buildRepository.getAllBuilds(pipelineId)
            .flatMap { it.stages }
            .map { it.name }
            .distinct()
            .sortedBy { it.lowercase() }
            .toList()

    private fun getProjectNameAndPipelineIdFromUrl(url: String): Pair<String, String> {
        val components = URL(url).path.split("/")
        val projectName = components[components.size - projectNameOffset]
        val pipelineId = components.last()
        return Pair(projectName, pipelineId)
    }

    private fun getExecutionInfoList(
        pipeline: PipelineConfiguration,
        emitCb: (SyncProgress) -> Unit
    ): List<ExecutionInfoDTO> {
        val pipelineUrl = URI.create(pipeline.url)
        val progress = AtomicInteger()
        emitCb(
            SyncProgress(
                pipeline.id,
                pipeline.name,
                progress.get(),
                progress.get(),
                executionInfoStep,
                stepSize
            )
        )
        val firstPage = buddyFeignClient.getExecutionPage(pipelineUrl, pipeline.credential, 1)
        val totalElements = firstPage.totalElementCount.let { if (it > 0) it else firstPage.executions.size }
        return if (totalElements > 0) {
            emitCb(
                SyncProgress(
                    pipeline.id,
                    pipeline.name,
                    progress.addAndGet(firstPage.executions.size),
                    totalElements,
                    executionInfoStep,
                    stepSize
                )
            )
            val nextPages = (firstPage.page + 1..firstPage.totalPageCount)
                .toList()
                .parallelStream()
                .map { page ->
                    buddyFeignClient.getExecutionPage(pipelineUrl, pipeline.credential, page).also {
                        emitCb(
                            SyncProgress(
                                pipeline.id,
                                pipeline.name,
                                progress.addAndGet(it.executions.size),
                                totalElements,
                                executionInfoStep,
                                stepSize
                            )
                        )
                    }
                }
                .toList()
            nextPages.stream().map { it.executions }.reduce(firstPage.executions) { a, b -> a + b }
        } else emptyList()
    }

    @Suppress("MaxLineLength")
    private fun getExecutionDetails(pipeline: PipelineConfiguration, executionInfo: ExecutionInfoDTO): ExecutionDetailsDTO {
        val pipelineUrl = URI.create(pipeline.url)
        return buddyFeignClient.getExecution(pipelineUrl, pipeline.credential, executionInfo.id)
    }

    private fun getExecutionChangeSet(pipeline: PipelineConfiguration, executionInfo: ExecutionInfoDTO): ChangeSetDTO {
        return if (executionInfo.toRevision != null) {
            val projectUrl = URI.create(pipeline.url.substringBeforeLast('/').substringBeforeLast('/'))
            return buddyFeignClient.getComparison(
                projectUrl,
                pipeline.credential,
                executionInfo.fromRevision?.revision ?: "0".repeat(commitIdLength),
                executionInfo.toRevision.revision
            )
        } else ChangeSetDTO()
    }

    private fun convertBuddyStatusToExecutionStatus(status: String) = when (status) {
        "SUCCESSFUL" -> Status.SUCCESS
        "FAILED" -> Status.FAILED
        "INPROGRESS" -> Status.IN_PROGRESS
        else -> Status.OTHER
    }

    private fun ExecutionInfoDTO.getExecutionStatus() = convertBuddyStatusToExecutionStatus(status)
    private fun ActionExecutionDTO.getExecutionStatus() = convertBuddyStatusToExecutionStatus(status)

    private fun ExecutionDetailsDTO.getExecutionStages(): List<Stage> {
        return actionExecutions.map {
            Stage(
                it.action.name,
                it.getExecutionStatus(),
                it.getTimestamp(),
                it.getDuration()
            )
        }
    }

    private companion object {
        const val projectNameOffset = 3
        const val commitIdLength = 40
        const val executionInfoStep = 1
        const val executionDetailsStep = 2
        const val stepSize = 2
    }
}
