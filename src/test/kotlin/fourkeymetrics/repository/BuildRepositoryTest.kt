package fourkeymetrics.repository

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import fourkeymetrics.model.Build
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.context.annotation.Import
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.test.context.junit.jupiter.SpringExtension


@DataMongoTest
@ExtendWith(SpringExtension::class)
@Import(BuildRepository::class, ObjectMapper::class)
class BuildRepositoryTest {
    @Test
    internal fun `should get all builds`(@Autowired mongoTemplate: MongoTemplate,
                                         @Autowired buildRepository: BuildRepository,
                                         @Autowired objectMapper: ObjectMapper) {
        val pipelineId = "fake pipeline"
        val collectionName = "build"

        val buildsToSave: List<Build> = objectMapper.readValue(this.javaClass.getResource("/repository/builds-1.json").readText())

        buildsToSave.forEach { mongoTemplate.save(it, collectionName) }

        assertThat(buildRepository.getAllBuilds(pipelineId)).hasSize(3)
    }

    /**
     * test file: builds-2.json
     * build 1 : 2021-01-01, [build: SUCCESS]
     * build 2 : 2021-01-15, [build: SUCCESS]
     * build 3 : 2021-01-20, [build: SUCCESS]
     * build 4 : 2021-01-30, [build: SUCCESS]
     */
    @Test
    internal fun `should get builds by time period`(@Autowired mongoTemplate: MongoTemplate,
                                                    @Autowired buildRepository: BuildRepository,
                                                    @Autowired objectMapper: ObjectMapper) {
        val pipelineId = "fake pipeline"
        val collectionName = "build"
        val startTimestamp = 1610668800000L //2021-01-01
        val endTimestamp = 1611100800000L //2021-01-20

        val buildsToSave: List<Build> = objectMapper.readValue(this.javaClass.getResource("/repository/builds-2.json").readText())

        buildsToSave.forEach { mongoTemplate.save(it, collectionName) }

        assertThat(buildRepository.getBuildsByTimePeriod(pipelineId, startTimestamp, endTimestamp)).hasSize(3)
    }
}