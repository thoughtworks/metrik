package fourkeymetrics.project.repository

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import fourkeymetrics.common.model.Build
import org.assertj.core.api.Assertions.assertThat
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


@DataMongoTest
@ExtendWith(SpringExtension::class)
@Import(BuildRepository::class, ObjectMapper::class)
class BuildRepositoryTest {
    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    @Autowired
    private lateinit var buildRepository: BuildRepository

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @BeforeEach
    internal fun setUp(@Autowired mongoTemplate: MongoTemplate) {
        val collectionName = "build"
        mongoTemplate.dropCollection(collectionName)
    }

    @Test
    internal fun `should get all builds belonging to this pipeline`() {
        val pipelineId = "fake pipeline"
        val collectionName = "build"

        val buildsToSave: List<Build> =
            objectMapper.readValue(this.javaClass.getResource("/repository/builds-for-build-repo-1.json").readText())

        buildsToSave.forEach { mongoTemplate.save(it, collectionName) }

        assertThat(buildRepository.getAllBuilds(pipelineId)).hasSize(3)
    }

    @Test
    internal fun `should save a bunch of build data`() {
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
    internal fun `should overwrite build data given data is already exist in DB`() {
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
    internal fun `should find build by number given build is exist in DB`() {
        val pipelineId = "fake-id"
        val firstBuild = Build(number = 1, pipelineId = pipelineId)

        buildRepository.save(listOf(firstBuild))

        assertThat(buildRepository.findByBuildNumber(pipelineId, 1)).isNotNull
    }

    @Test
    internal fun `should get null when find by number given build is not exist in DB`() {
        val pipelineId = "fake-id"
        val firstBuild = Build(number = 1, pipelineId = pipelineId)

        buildRepository.save(listOf(firstBuild))

        assertThat(buildRepository.findByBuildNumber(pipelineId, 2)).isNull()
    }

    @Test
    internal fun `should remove collection data when given pipeline Id`() {
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
    internal fun `should get all builds for multiple pipeline`() {
        val pipelineIds = listOf("1", "2")
        val collectionName = "build"

        val buildsToSave: List<Build> = listOf(Build(pipelineId = "1"), Build(pipelineId = "2"))

        buildsToSave.forEach { mongoTemplate.save(it, collectionName) }

        assertThat(buildRepository.getAllBuilds(pipelineIds)).hasSize(2)
    }
}