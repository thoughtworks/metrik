package metrik.project.domain.repository

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import metrik.project.domain.model.Build
import metrik.project.domain.model.Status
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
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

        val buildsToSave: List<Build> =
            objectMapper.readValue(this.javaClass.getResource("/repository/builds-for-build-repo-3.json").readText())

        buildRepository.save(buildsToSave)

        val builds: List<Build> = mongoTemplate.findAll(collectionName)

        assertThat(builds).hasSize(3)

        val ids = builds.map { it.pipelineId }

        val expectedIds = buildsToSave.map { it.pipelineId }

        assertTrue(ids.containsAll(expectedIds))
    }

    @Test
    fun `should overwrite build data given data is already exist in DB`() {
        val collectionName = "build"
        val firstBuild = Build(number = 1)
        val secondBuild = Build(number = 2)

        buildRepository.save(listOf(firstBuild))
        buildRepository.save(listOf(firstBuild, secondBuild))

        val builds: List<Build> = mongoTemplate.findAll(collectionName)
        assertThat(builds).hasSize(2)
        assertThat(builds.any { it.number == firstBuild.number }).isTrue
        assertThat(builds.any { it.number == secondBuild.number }).isTrue
    }

    @Test
    fun `should remove collection data when given pipeline Id`() {
        val collectionName = "build"
        val pipelineId1 = "fake-pipelineId1"
        val pipelineId2 = "fake-pipelineId2"

        buildRepository.save(listOf(Build(pipelineId = pipelineId1), Build(pipelineId = pipelineId2)))
        buildRepository.clear(pipelineId1)

        val builds: List<Build> = mongoTemplate.findAll(collectionName)
        assertThat(builds.first().pipelineId).isEqualTo(pipelineId2)
        assertThat(builds).hasSize(1)
    }

    @Test
    fun `should get all builds for multiple pipeline`() {
        val pipelineIds = listOf("1", "2")
        val collectionName = "build"

        val buildsToSave: List<Build> = listOf(Build(pipelineId = "1"), Build(pipelineId = "2"))

        buildsToSave.forEach { mongoTemplate.save(it, collectionName) }

        assertThat(buildRepository.getAllBuilds(pipelineIds)).hasSize(2)
    }

    @Test
    fun `should get all builds belonging to this pipeline`() {
        val pipelineId = "fake pipeline"
        val collectionName = "build"

        val buildsToSave: List<Build> =
            objectMapper.readValue(this.javaClass.getResource("/repository/builds-for-build-repo-1.json").readText())

        buildsToSave.forEach { mongoTemplate.save(it, collectionName) }

        assertThat(buildRepository.getAllBuilds(pipelineId)).hasSize(3)
    }

    @Test
    fun `should find build by number given build is exist in DB`() {
        val pipelineId = "fake-id"
        val firstBuild = Build(number = 1, pipelineId = pipelineId)

        buildRepository.save(listOf(firstBuild))

        assertThat(buildRepository.getByBuildNumber(pipelineId, 1)).isNotNull
    }

    @Test
    fun `should get null when find by number given build is not exist in DB`() {
        val pipelineId = "fake-id"
        val firstBuild = Build(number = 1, pipelineId = pipelineId)

        buildRepository.save(listOf(firstBuild))

        assertThat(buildRepository.getByBuildNumber(pipelineId, 2)).isNull()
    }

    @Test
    fun `should get builds with certain build status`() {
        val pipelineId = "fake-id"
        val collectionName = "build"
        val buildsToSave = listOf(
            Build(pipelineId, 1, Status.SUCCESS),
            Build(pipelineId, 2, Status.SUCCESS),
            Build(pipelineId, 5, Status.IN_PROGRESS)
        )

        buildsToSave.forEach { mongoTemplate.save(it, collectionName) }

        assertEquals(2, buildRepository.getByBuildStatus(pipelineId, Status.SUCCESS).size)
        assertEquals(1, buildRepository.getByBuildStatus(pipelineId, Status.IN_PROGRESS).size)
        assertEquals(0, buildRepository.getByBuildStatus(pipelineId, Status.FAILED).size)
    }

    @Test
    fun `should get the build with biggest build number`() {
        val pipelineId = "fake-id"
        val collectionName = "build"
        val buildsToSave = listOf(Build(pipelineId, 1), Build(pipelineId, 2), Build(pipelineId, 5))

        buildsToSave.forEach { mongoTemplate.save(it, collectionName) }

        assertEquals(5, buildRepository.getMaxBuild(pipelineId)!!.number)
    }

    @Test
    fun `should get latest build`() {
        val pipelineId = "fake-id"
        val collectionName = "build"
        val buildsToSave = listOf(
            Build(pipelineId, 1, timestamp = 20201123),
            Build(pipelineId, 2, timestamp = 20201122),
            Build(pipelineId, 5, timestamp = 20191202),
            Build(pipelineId, 6, timestamp = 0)
        )

        buildsToSave.forEach { mongoTemplate.save(it, collectionName) }

        assertEquals(1, buildRepository.getLatestBuild(pipelineId)!!.number)
    }

    @Test
    fun `should get in-progress builds`() {
        val pipelineId = "fake-id"
        val collectionName = "build"
        val twoWeeks: Long = 14
        val now = ZonedDateTime.now().minusDays(twoWeeks).toEpochSecond()
        val inProgressBuild = Build(
            pipelineId,
            1,
            result = Status.IN_PROGRESS,
            timestamp = now + 10
        )
        val buildsToSave = listOf(
            inProgressBuild,
            Build(
                pipelineId,
                2,
                result = Status.FAILED,
                timestamp = now + 10
            ),
            Build(
                pipelineId,
                5,
                result = Status.SUCCESS,
                timestamp = now + 10
            ),
        )

        buildsToSave.forEach { mongoTemplate.save(it, collectionName) }

        val builds = listOf(inProgressBuild)

        assertEquals(builds, buildRepository.getInProgressBuilds(pipelineId))
    }

    @Test
    fun `should get all build numbers that need a data synchronization given the most recent build number in Jenkins and Bamboo`() {
        val pipelineId = "fake-id"
        val collectionName = "build"
        val buildsToSave = listOf(
            Build(pipelineId, 1, Status.SUCCESS),
            Build(pipelineId, 2, Status.SUCCESS),
            Build(pipelineId, 3, Status.IN_PROGRESS, timestamp = ZonedDateTime.now().minusDays(15).toEpochSecond()),
            Build(pipelineId, 4, Status.IN_PROGRESS, timestamp = ZonedDateTime.now().minusDays(13).toEpochSecond())
        )
        buildsToSave.forEach { mongoTemplate.save(it, collectionName) }

        val givenMostRecentBuild = 8
        assertEquals(listOf(4, 5, 6, 7, 8), buildRepository.getBambooJenkinsBuildNumbersNeedSync(pipelineId, givenMostRecentBuild))
    }
}
