package fourkeymetrics.datasource

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.context.annotation.Import
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.test.context.junit.jupiter.SpringExtension

@DataMongoTest
@ExtendWith(SpringExtension::class)
@Import(UpdateRepository::class)
internal class UpdateRepositoryTest {
    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    @Autowired
    private lateinit var updateRepository: UpdateRepository

    private val collectionName = "update"


    @BeforeEach
    internal fun setUp(@Autowired mongoTemplate: MongoTemplate) {
        mongoTemplate.dropCollection(collectionName)
    }

    @Test
    internal fun `should get last update record when there is a previous update`() {
        val dashboardId = "fake-dashboard-id"

        mongoTemplate.save(UpdateRecord(dashboardId, 100000000), collectionName)
        mongoTemplate.save(UpdateRecord(dashboardId, 300000000), collectionName)
        mongoTemplate.save(UpdateRecord(dashboardId, 200000000), collectionName)

        val lastUpdate = updateRepository.getLastUpdate(dashboardId)

        assertThat(lastUpdate).isNotNull
        assertThat(lastUpdate!!.updateTimestamp).isEqualTo(300000000)
    }

    @Test
    internal fun `should get null when there is no previous update`() {
        val dashboardId = "fake-dashboard-id"

        val lastUpdate = updateRepository.getLastUpdate(dashboardId)

        assertThat(lastUpdate).isNull()
    }

    @Test
    internal fun `should save update record`() {
        val dashboardId = "fake-dashboard-id"
        val record = UpdateRecord(
            dashboardId,
            300000000
        )

        updateRepository.save(record)

        val lastUpdate = mongoTemplate.findAll(UpdateRecord::class.java, collectionName)

        assertThat(lastUpdate).hasSize(1)
    }
}