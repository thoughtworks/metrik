package metrik.project.domain.repository

import metrik.infrastructure.utlils.toTimestamp
import metrik.project.domain.model.Commit
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.context.annotation.Import
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.ZoneId
import java.time.ZonedDateTime

@DataMongoTest
@ExtendWith(SpringExtension::class)
@Import(CommitRepository::class)
internal class GithubCommitRepositoryTest {
    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    @Autowired
    private lateinit var commitRepository: CommitRepository
    private val collectionName = "commit"

    @BeforeEach
    fun setUp(@Autowired mongoTemplate: MongoTemplate) {
        mongoTemplate.dropCollection(collectionName)
    }

    @Test
    internal fun `should get latest commit given there are records in DB`() {
        val pipelineId = "test pipeline"
        val earlyTimeStamp = ZonedDateTime.of(2021, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault()).toTimestamp()
        val lateTimeStamp = ZonedDateTime.of(2021, 1, 2, 0, 0, 0, 0, ZoneId.systemDefault()).toTimestamp()

        mongoTemplate.save(
            Commit(
                timestamp = earlyTimeStamp,
                pipelineId = pipelineId
            ),
            collectionName
        )
        mongoTemplate.save(
            Commit(
                timestamp = lateTimeStamp,
                pipelineId = pipelineId
            ),
            collectionName
        )

        assertThat(commitRepository.getTheLatestCommit(pipelineId)!!.timestamp).isEqualTo(lateTimeStamp)
    }

    @Test
    internal fun `should find commit by time period`() {
        val pipelineId = "test pipeline"

        for (day in 1..5) {
            mongoTemplate.save(
                Commit(
                    timestamp = ZonedDateTime.of(2021, 1, day, 0, 0, 0, 0, ZoneId.systemDefault()).toTimestamp(),
                    pipelineId = pipelineId
                ),
                collectionName
            )
        }

        assertThat(
            commitRepository.findByTimePeriod(
                pipelineId,
                ZonedDateTime.of(2021, 1, 2, 0, 0, 0, 0, ZoneId.systemDefault()).toTimestamp(),
                ZonedDateTime.of(2021, 1, 4, 0, 0, 0, 0, ZoneId.systemDefault()).toTimestamp()
            ).size
        ).isEqualTo(3)
    }

    @Test
    internal fun `should detect duplication`() {
        val pipelineId = "test pipeline"
        mongoTemplate.save(
            Commit(
                commitId = "2",
                timestamp = ZonedDateTime.of(2021, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault()).toTimestamp(),
                pipelineId = pipelineId
            ),
            collectionName
        )

        val commitsToCheck = (1..3).map { metrik.project.domain.service.githubactions.GithubCommit(it.toString(), ZonedDateTime.now()) }
        assertTrue(commitRepository.hasDuplication(commitsToCheck))
    }

    @Test
    internal fun `should save or replace commits`() {
        val pipelineId = "test pipeline"
        mongoTemplate.save(Commit(pipelineId = pipelineId, commitId = "1"))

        val commitsToSave = (1..3).map { metrik.project.domain.service.githubactions.GithubCommit(it.toString(), ZonedDateTime.now()) }
        commitRepository.save(pipelineId, commitsToSave.toMutableList())

        assertThat(mongoTemplate.findAll(Commit::class.java).size).isEqualTo(3)
    }
}
