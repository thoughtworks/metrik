package metrik.project.domain.repository

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import metrik.project.domain.model.Execution
import metrik.project.domain.model.Stage
import metrik.project.domain.model.Status
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.context.annotation.Import
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.findAll
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.ZonedDateTime

@DataMongoTest
@ExtendWith(SpringExtension::class)
@Import(BuildRepository::class)
internal class BuildRepositoryTest {
    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    @Autowired
    private lateinit var buildRepository: BuildRepository

    private val objectMapper = jacksonObjectMapper()

    @BeforeEach
    fun setUp(@Autowired mongoTemplate: MongoTemplate) {
        val collectionName = "build"
        mongoTemplate.dropCollection(collectionName)
    }

    @Test
    fun `should save a bunch of build data`() {
        val collectionName = "build"

        val buildsToSave: List<Execution> =
            objectMapper.readValue(this.javaClass.getResource("/repository/builds-for-build-repo-3.json").readText())

        buildRepository.save(buildsToSave)

        val executions: List<Execution> = mongoTemplate.findAll(collectionName)

        assertThat(executions).hasSize(3)

        val ids = executions.map { it.pipelineId }

        val expectedIds = buildsToSave.map { it.pipelineId }

        assertTrue(ids.containsAll(expectedIds))
    }

    @Test
    fun `should overwrite build data given data is already exist in DB`() {
        val collectionName = "build"
        val firstExecution = Execution(number = 1)
        val secondExecution = Execution(number = 2)

        buildRepository.save(listOf(firstExecution))
        buildRepository.save(listOf(firstExecution, secondExecution))

        val executions: List<Execution> = mongoTemplate.findAll(collectionName)
        assertThat(executions).hasSize(2)
        assertThat(executions.any { it.number == firstExecution.number }).isTrue
        assertThat(executions.any { it.number == secondExecution.number }).isTrue
    }

    @Test
    fun `should remove collection data when given pipeline Id`() {
        val collectionName = "build"
        val pipelineId1 = "fake-pipelineId1"
        val pipelineId2 = "fake-pipelineId2"

        buildRepository.save(listOf(Execution(pipelineId = pipelineId1), Execution(pipelineId = pipelineId2)))
        buildRepository.clear(pipelineId1)

        val executions: List<Execution> = mongoTemplate.findAll(collectionName)
        assertThat(executions.first().pipelineId).isEqualTo(pipelineId2)
        assertThat(executions).hasSize(1)
    }

    @Test
    fun `should get all builds for multiple pipeline`() {
        val pipelineIds = listOf("1", "2")
        val collectionName = "build"

        val buildsToSaves: List<Execution> = listOf(Execution(pipelineId = "1"), Execution(pipelineId = "2"))

        buildsToSaves.forEach { mongoTemplate.save(it, collectionName) }

        assertThat(buildRepository.getAllBuilds(pipelineIds)).hasSize(2)
    }

    @Test
    fun `should get all builds belonging to this pipeline`() {
        val pipelineId = "fake pipeline"
        val collectionName = "build"

        val buildsToSave: List<Execution> =
            objectMapper.readValue(this.javaClass.getResource("/repository/builds-for-build-repo-1.json").readText())

        buildsToSave.forEach { mongoTemplate.save(it, collectionName) }

        assertThat(buildRepository.getAllBuilds(pipelineId)).hasSize(3)
    }

    @Test
    fun `should find build by number given build is exist in DB`() {
        val pipelineId = "fake-id"
        val firstExecution = Execution(number = 1, pipelineId = pipelineId)

        buildRepository.save(listOf(firstExecution))

        assertThat(buildRepository.getByBuildNumber(pipelineId, 1)).isNotNull
    }

    @Test
    fun `should get null when find by number given build is not exist in DB`() {
        val pipelineId = "fake-id"
        val firstExecution = Execution(number = 1, pipelineId = pipelineId)

        buildRepository.save(listOf(firstExecution))

        assertThat(buildRepository.getByBuildNumber(pipelineId, 2)).isNull()
    }

    @Test
    fun `should get builds with certain build status`() {
        val pipelineId = "fake-id"
        val collectionName = "build"
        val buildsToSaves = listOf(
            Execution(pipelineId, 1, Status.SUCCESS),
            Execution(pipelineId, 2, Status.SUCCESS),
            Execution(pipelineId, 5, Status.IN_PROGRESS)
        )

        buildsToSaves.forEach { mongoTemplate.save(it, collectionName) }

        assertEquals(2, buildRepository.getByBuildStatus(pipelineId, Status.SUCCESS).size)
        assertEquals(1, buildRepository.getByBuildStatus(pipelineId, Status.IN_PROGRESS).size)
        assertEquals(0, buildRepository.getByBuildStatus(pipelineId, Status.FAILED).size)
    }

    @Test
    fun `should get the build with biggest build number`() {
        val pipelineId = "fake-id"
        val collectionName = "build"
        val buildsToSaves = listOf(Execution(pipelineId, 1), Execution(pipelineId, 2), Execution(pipelineId, 5))

        buildsToSaves.forEach { mongoTemplate.save(it, collectionName) }

        assertEquals(5, buildRepository.getMaxBuild(pipelineId)!!.number)
    }

    @Test
    fun `should get latest build`() {
        val pipelineId = "fake-id"
        val collectionName = "build"
        val buildsToSaves = listOf(
            Execution(pipelineId, 1, timestamp = 20201123),
            Execution(pipelineId, 2, timestamp = 20201122),
            Execution(pipelineId, 5, timestamp = 20191202),
            Execution(pipelineId, 6, timestamp = 0)
        )

        buildsToSaves.forEach { mongoTemplate.save(it, collectionName) }

        assertEquals(1, buildRepository.getLatestBuild(pipelineId)!!.number)
    }

    @Test
    fun `should get in-progress builds`() {
        val pipelineId = "fake-id"
        val collectionName = "build"
        val twoWeeks: Long = 14
        val now = ZonedDateTime.now().minusDays(twoWeeks).toEpochSecond()
        val inProgressExecution = Execution(
            pipelineId,
            1,
            result = Status.IN_PROGRESS,
            timestamp = now + 10
        )
        val buildsToSaves = listOf(
            inProgressExecution,
            Execution(
                pipelineId,
                2,
                result = Status.FAILED,
                timestamp = now + 10
            ),
            Execution(
                pipelineId,
                5,
                result = Status.SUCCESS,
                timestamp = now + 10
            ),
        )

        buildsToSaves.forEach { mongoTemplate.save(it, collectionName) }

        val builds = listOf(inProgressExecution)

        assertEquals(builds, buildRepository.getInProgressBuilds(pipelineId))
    }

    @Test
    fun `should get all build numbers that need a data synchronization given the most recent build number in Jenkins and Bamboo`() {
        val pipelineId = "fake-id"
        val collectionName = "build"
        val buildsToSaves = listOf(
            Execution(pipelineId, 1, Status.SUCCESS),
            Execution(pipelineId, 2, Status.SUCCESS),
            Execution(pipelineId, 3, Status.IN_PROGRESS, timestamp = ZonedDateTime.now().minusDays(15).toEpochSecond()),
            Execution(pipelineId, 4, Status.IN_PROGRESS, timestamp = ZonedDateTime.now().minusDays(13).toEpochSecond())
        )
        buildsToSaves.forEach { mongoTemplate.save(it, collectionName) }

        val givenMostRecentBuild: Long = 8
        assertEquals(
            listOf(4L, 5L, 6L, 7L, 8L),
            buildRepository.getBambooJenkinsBuildNumbersNeedSync(pipelineId, givenMostRecentBuild)
        )
    }

    @Test
    fun `should get closest build given a timestamp`() {
        val pipelineId = "fake-id"
        val collectionName = "build"
        val targetExecution =
            Execution(
                pipelineId,
                3,
                Status.SUCCESS,
                timestamp = ZonedDateTime.now().minusDays(14).toEpochSecond(),
                branch = "master"
            )
        val buildsToSaves = listOf(
            Execution(
                pipelineId,
                2,
                Status.SUCCESS,
                timestamp = ZonedDateTime.now().minusDays(12).toEpochSecond(),
                branch = "master"
            ),
            Execution(
                pipelineId,
                1,
                Status.SUCCESS,
                timestamp = ZonedDateTime.now().minusDays(15).toEpochSecond(),
                branch = "master"
            ),
            targetExecution
        )
        buildsToSaves.forEach { mongoTemplate.save(it, collectionName) }

        assertEquals(
            targetExecution,
            buildRepository.getPreviousBuild(
                pipelineId,
                ZonedDateTime.now().minusDays(13).toEpochSecond(),
                branch = "master"
            )
        )
    }

    @Test
    fun `should get build given no build is earlier`() {
        val pipelineId = "fake-id"
        val collectionName = "build"
        val buildsToSaves = listOf(

            Execution(
                pipelineId,
                2,
                Status.SUCCESS,
                timestamp = ZonedDateTime.now().minusDays(12).toEpochSecond(),
                branch = "master"
            ),
            Execution(
                pipelineId,
                1,
                Status.SUCCESS,
                timestamp = ZonedDateTime.now().minusDays(15).toEpochSecond(),
                branch = "master"
            ),
            Execution(
                pipelineId,
                3,
                Status.SUCCESS,
                timestamp = ZonedDateTime.now().minusDays(14).toEpochSecond(),
                branch = "master"
            )
        )
        buildsToSaves.forEach { mongoTemplate.save(it, collectionName) }

        assertNull(
            buildRepository.getPreviousBuild(
                pipelineId,
                ZonedDateTime.now().minusDays(16).toEpochSecond(),
                branch = "master"
            )
        )
    }

    @Test
    fun `should get earlier build from correct branch`() {
        val pipelineId = "fake-id"
        val collectionName = "build"
        val targetExecution =
            Execution(
                pipelineId,
                3,
                Status.SUCCESS,
                timestamp = ZonedDateTime.now().minusDays(14).toEpochSecond(),
                branch = "master"
            )
        val buildsToSaves = listOf(
            Execution(
                pipelineId,
                2,
                Status.SUCCESS,
                timestamp = ZonedDateTime.now().minusDays(12).toEpochSecond(),
                branch = "feature"
            ),
            Execution(
                pipelineId,
                1,
                Status.SUCCESS,
                timestamp = ZonedDateTime.now().minusDays(15).toEpochSecond(),
                branch = "feature"
            ),
            targetExecution
        )
        buildsToSaves.forEach { mongoTemplate.save(it, collectionName) }

        assertEquals(
            targetExecution,
            buildRepository.getPreviousBuild(
                pipelineId,
                ZonedDateTime.now().minusDays(10).toEpochSecond(),
                branch = "master"
            )
        )
    }

    @Test
    fun `should get latest deploy timestamp when it exists`() {
        val pipelineId = "fake-id"
        val collectionName = "build"
        val buildsToSave = listOf(
            Execution(
                pipelineId,
                1,
                timestamp = 20201123,
                stages = listOf(Stage(status = Status.IN_PROGRESS, startTimeMillis = 20201123))
            ),
            Execution(
                pipelineId,
                2,
                timestamp = 20201122,
                stages = listOf(Stage(status = Status.SUCCESS, startTimeMillis = 20201122))
            ),
            Execution(
                pipelineId,
                5,
                timestamp = 20201121,
                stages = listOf(Stage(status = Status.FAILED, startTimeMillis = 20201121))
            ),
            Execution(
                pipelineId,
                6,
                timestamp = 20201120,
                stages = listOf(Stage(status = Status.OTHER, startTimeMillis = 20201120))
            )
        )

        buildsToSave.forEach { mongoTemplate.save(it, collectionName) }

        assertEquals(20201122, buildRepository.getLatestDeployTimestamp(pipelineId))
    }

    @Test
    fun `should get earliest build timestamp when latest deploy timestamp not exist`() {
        val pipelineId = "fake-id"
        val collectionName = "build"
        val buildsToSave = listOf(
            Execution(
                pipelineId,
                1,
                timestamp = 20201123,
                stages = listOf(Stage(status = Status.IN_PROGRESS, startTimeMillis = 20201123))
            ),
            Execution(
                pipelineId,
                2,
                timestamp = 20201122,
                stages = listOf(Stage(status = Status.IN_PROGRESS, startTimeMillis = 20201122))
            ),
            Execution(
                pipelineId,
                5,
                timestamp = 20201121,
                stages = listOf(Stage(status = Status.IN_PROGRESS, startTimeMillis = 20201121))
            ),
            Execution(
                pipelineId,
                6,
                timestamp = 20201120,
                stages = listOf(Stage(status = Status.IN_PROGRESS, startTimeMillis = 20201120))
            )
        )

        buildsToSave.forEach { mongoTemplate.save(it, collectionName) }

        assertEquals(20201120, buildRepository.getLatestDeployTimestamp(pipelineId))
    }

    @Test
    fun `should get 0 when latest deploy timestamp and earliest build timestamp not exist`() {
        val pipelineId = "fake-id"
        val collectionName = "build"
        val buildsToSave = listOf(
            Execution(pipelineId, 1, stages = listOf(Stage(status = Status.IN_PROGRESS, startTimeMillis = 20201123))),
            Execution(pipelineId, 2, stages = listOf(Stage(status = Status.IN_PROGRESS, startTimeMillis = 20201122))),
            Execution(pipelineId, 5, stages = listOf(Stage(status = Status.IN_PROGRESS, startTimeMillis = 20201121))),
            Execution(pipelineId, 6, stages = listOf(Stage(status = Status.IN_PROGRESS, startTimeMillis = 20201120)))
        )

        buildsToSave.forEach { mongoTemplate.save(it, collectionName) }

        assertEquals(0, buildRepository.getLatestDeployTimestamp(pipelineId))
    }
}
