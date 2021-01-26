package fourkeymetrics.datasource.pipeline.builddata

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import fourkeymetrics.metric.model.Build
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
    @BeforeEach
    internal fun setUp(@Autowired mongoTemplate: MongoTemplate) {
        val collectionName = "build"
        mongoTemplate.dropCollection(collectionName)
    }

    @Test
    internal fun `should get all builds belonging to this pipeline`(@Autowired mongoTemplate: MongoTemplate,
                                         @Autowired buildRepository: BuildRepository,
                                         @Autowired objectMapper: ObjectMapper) {
        val pipelineId = "fake pipeline"
        val collectionName = "build"

        val buildsToSave: List<Build> = objectMapper.readValue(this.javaClass.getResource("/repository/builds-for-build-repo-1.json").readText())

        buildsToSave.forEach { mongoTemplate.save(it, collectionName) }

        assertThat(buildRepository.getAllBuilds(pipelineId)).hasSize(3)
    }

    @Test
    internal fun `should save a bunch of build data`(@Autowired mongoTemplate: MongoTemplate,
                                                     @Autowired buildRepository: BuildRepository,
                                                     @Autowired objectMapper: ObjectMapper) {
        val collectionName = "build"

        val buildsToSave: List<Build> = objectMapper.readValue(this.javaClass.getResource("/repository/builds-for-build-repo-3.json").readText())

        buildRepository.save(buildsToSave)

        val builds: List<Build> = mongoTemplate.findAll(collectionName)

        assertThat(builds).hasSize(3)

        val ids = builds.map { it.pipelineId }

        val expectedIds = buildsToSave.map { it.pipelineId }

        assertTrue(ids.containsAll(expectedIds))
    }

    @Test
    internal fun `should remove all collection data`(@Autowired mongoTemplate: MongoTemplate,
                                                     @Autowired buildRepository: BuildRepository,
                                                     @Autowired objectMapper: ObjectMapper) {
        val collectionName = "build"
        val buildsToSave: List<Build> = objectMapper.readValue(this.javaClass.getResource("/repository/builds-for-build-repo-2.json").readText())
        buildsToSave.forEach { mongoTemplate.save(it, collectionName) }

        buildRepository.clear()

        val builds: List<Build> = mongoTemplate.findAll(collectionName)
        assertThat(builds).hasSize(0)
    }
}