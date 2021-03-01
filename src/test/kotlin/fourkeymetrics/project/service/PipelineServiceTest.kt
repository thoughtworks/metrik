package fourkeymetrics.project.service

import fourkeymetrics.common.model.Build
import fourkeymetrics.common.model.Stage
import fourkeymetrics.common.model.Status
import fourkeymetrics.project.repository.BuildRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
internal class PipelineServiceTest {
    internal class FakePipelineService : PipelineService() {
        override fun syncBuilds(pipelineId: String): List<Build> {
            return emptyList()
        }

        override fun verifyPipelineConfiguration(url: String, username: String, credential: String) {
            println("fake implementation")
        }

        override fun mapStageStatus(statusInPipeline: String): Status {
            TODO("Not yet implemented")
        }

        override fun mapBuildStatus(statusInPipeline: String?): Status? {
            TODO("Not yet implemented")
        }
    }

    @Mock
    private lateinit var buildRepository: BuildRepository

    @InjectMocks
    private lateinit var pipelineService: FakePipelineService

    private val pipelineId = "pipelineId"

    @Test
    internal fun `should return true when hasStageInTimeRange() called given stage exists in builds filtered by time range`() {
        val targetStage = "deploy to dev"
        val startTimestamp = 100000L
        val endTimestamp = 200000L
        val lastBuild = Build(
            stages = listOf(
                Stage(name = targetStage, startTimeMillis = 100000L),
                Stage(name = "another stage", startTimeMillis = 100000L)
            )
        )
        `when`(buildRepository.getAllBuilds(pipelineId)).thenReturn(listOf(lastBuild))

        assertTrue(pipelineService.hasStageInTimeRange(pipelineId, targetStage, startTimestamp, endTimestamp))
    }

    @Test
    internal fun `should return false when hasStageInTimeRange() called given stage not exists in builds filtered by time range`() {
        val targetStage = "deploy to dev"
        val startTimestamp = 100000L
        val endTimestamp = 200000L
        val lastBuild = Build(
            stages = listOf(
                Stage(name = "clone"), Stage(name = "build"),
                Stage(
                    name = targetStage,
                    startTimeMillis = 150000L,
                    durationMillis = 30000L,
                    pauseDurationMillis = 50000L
                )
            )
        )
        `when`(buildRepository.getAllBuilds(pipelineId)).thenReturn(listOf(lastBuild))

        assertFalse(pipelineService.hasStageInTimeRange(pipelineId, targetStage, startTimestamp, endTimestamp))
    }

    @Test
    internal fun `should return false when hasStageInTimeRange() called given stage not exists in builds filtered by target stage name`() {
        val targetStage = "deploy to dev"
        val startTimestamp = 100000L
        val endTimestamp = 200000L
        val lastBuild = Build(
            stages = listOf(
                Stage(name = "another stage1", startTimeMillis = 100000L),
                Stage(name = "another stage2", startTimeMillis = 100000L)
            )
        )
        `when`(buildRepository.getAllBuilds(pipelineId)).thenReturn(listOf(lastBuild))

        assertFalse(pipelineService.hasStageInTimeRange(pipelineId, targetStage, startTimestamp, endTimestamp))
    }

    @Test
    internal fun `should return sorted stage name lists when getStagesSortedByName() called`() {
        val builds = listOf(
            Build(
                stages = listOf(
                    Stage(name = "clone"), Stage(name = "build"),
                    Stage(name = "zzz"), Stage(name = "amazing")
                )
            ),
            Build(
                stages = listOf(
                    Stage(name = "build"), Stage("good")
                )
            )
        )
        `when`(buildRepository.getAllBuilds(pipelineId)).thenReturn(builds)

        val result = pipelineService.getStagesSortedByName(pipelineId)

        assertEquals(5, result.size)
        assertEquals("amazing", result[0])
        assertEquals("build", result[1])
        assertEquals("clone", result[2])
        assertEquals("good", result[3])
        assertEquals("zzz", result[4])
    }
}