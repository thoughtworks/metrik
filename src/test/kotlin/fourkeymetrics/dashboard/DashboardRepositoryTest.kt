package fourkeymetrics.dashboard

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import fourkeymetrics.dashboard.model.Dashboard
import fourkeymetrics.dashboard.model.PipelineType
import fourkeymetrics.dashboard.repository.DashboardRepository
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
@Import(DashboardRepository::class, ObjectMapper::class)
internal class DashboardRepositoryTest {
    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    @Autowired
    private lateinit var dashboardRepository: DashboardRepository

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    internal fun `should get pipeline configuration given pipeline already exist in DB`() {
        val dashboardId = "dashboardId"
        val pipelineId = "pipelineId"
        val collectionName = "dashboard"

        val dashboardToSave: List<Dashboard> = objectMapper.readValue(
            this.javaClass.getResource("/repository/dashboards-1.json").readText()
        )

        dashboardToSave.forEach { mongoTemplate.save(it, collectionName) }

        val configuration = dashboardRepository.getPipelineConfiguration(dashboardId, pipelineId)

        assertThat(configuration).isNotNull
        assertThat(configuration!!.url).isEqualTo("test.com")
        assertThat(configuration.credential).isEqualTo("fake-credential")
        assertThat(configuration.username).isEqualTo("username")
        assertThat(configuration.type).isEqualTo(PipelineType.JENKINS)
    }

    @Test
    internal fun `should get null given dashboard not exists in DB`() {
        val dashboardId = "dashboardId"
        val pipelineId = "pipelineId"

        val configuration = dashboardRepository.getPipelineConfiguration(dashboardId, pipelineId)

        assertThat(configuration).isNull()
    }

    @Test
    internal fun `should get null given pipeline not exists in DB`() {
        val dashboardId = "fake-dashboard"
        val pipelineId = "fake-pipeline"
        val collectionName = "dashboard"

        val dashboardToSave: List<Dashboard> = objectMapper.readValue(
            this.javaClass.getResource("/repository/dashboards-2.json").readText()
        )

        dashboardToSave.forEach { mongoTemplate.save(it, collectionName) }

        val configuration = dashboardRepository.getPipelineConfiguration(dashboardId, pipelineId)

        assertThat(configuration).isNull()
    }

    @Test
    internal fun `should get dashboard by ID`() {
        val dashboardId = "fake-dashboard"
        val collectionName = "dashboard"

        mongoTemplate.save(Dashboard(id = dashboardId), collectionName)

        val dashboard = dashboardRepository.getDashBoardDetailById(dashboardId)

        assertThat(dashboard).isNotNull
    }

    @Test
    internal fun `should get last sync record`() {
        val dashboardId = "fake-dashboard"
        val collectionName = "dashboard"
        val syncTimestamp = 1000000000000

        mongoTemplate.save(Dashboard(id = dashboardId, synchronizationTimestamp = syncTimestamp), collectionName)

        val actualSyncTimestamp = dashboardRepository.getLastSyncRecord(dashboardId)

        assertThat(actualSyncTimestamp).isEqualTo(syncTimestamp)
    }

    @Test
    internal fun `should update sync timestamp`() {
        val dashboardId = "fake-dashboard"
        val collectionName = "dashboard"
        val previousTimestamp = 1000000000000
        val newTimestamp = 2000000000000

        mongoTemplate.save(Dashboard(id = dashboardId, synchronizationTimestamp = previousTimestamp), collectionName)

        val actualSyncTimestamp = dashboardRepository.updateSynchronizationTime(dashboardId, newTimestamp)

        assertThat(actualSyncTimestamp).isEqualTo(newTimestamp)
    }
}