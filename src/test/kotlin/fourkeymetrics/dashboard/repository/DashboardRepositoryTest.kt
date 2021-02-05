package fourkeymetrics.dashboard.repository

import fourkeymetrics.dashboard.exception.DashboardNotFoundException
import fourkeymetrics.dashboard.model.Dashboard
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.context.annotation.Import
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.findById
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit.jupiter.SpringExtension

@DataMongoTest
@ExtendWith(SpringExtension::class)
@Import(DashboardRepository::class)
internal class DashboardRepositoryTest {
    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    @Autowired
    private lateinit var dashboardRepository: DashboardRepository

    private val dashboardName = "dashboard name"
    private val dashboardId = "dashboard id"
    private val synchronisationTime = 98708L
    private val dashboard = Dashboard(dashboardId, dashboardName, synchronisationTime)

    @BeforeEach
    internal fun setUp(@Autowired mongoTemplate: MongoTemplate) {
        val collectionName = "dashboard"
        mongoTemplate.dropCollection(collectionName)
    }

    @Test
    internal fun `test existByName will false true if dashboard with name not exist`() {
        mongoTemplate.save(dashboard)

        assertFalse(dashboardRepository.existWithGivenName("some name"))
    }

    @Test
    internal fun `test existByName will return true if dashboard with name not exist`() {
        mongoTemplate.save(dashboard)

        assertTrue(dashboardRepository.existWithGivenName(dashboardName))
    }

    @Test
    internal fun `test find by id will return dashboard`() {
        mongoTemplate.save(dashboard)

        val dashboard = dashboardRepository.findById(dashboardId)

        assertEquals(dashboard.id, dashboardId)
        assertEquals(dashboard.name, dashboardName)
        assertEquals(dashboard.synchronizationTimestamp, synchronisationTime)
    }

    @Test
    internal fun `test find by id will throw dashboardNotFoundException if dashboard not exist`() {
        mongoTemplate.save(dashboard)

        val exception: DashboardNotFoundException = assertThrows { dashboardRepository.findById("some id") }

        assertEquals(exception.httpStatus, HttpStatus.NOT_FOUND)
    }

    @Test
    internal fun `test save dashboard`() {
        dashboardRepository.save(dashboard)

        val foundDashboard = mongoTemplate.findById(dashboardId, Dashboard::class.java)

        assertEquals(foundDashboard?.id, dashboardId)
        assertEquals(foundDashboard?.name, dashboardName)
        assertEquals(foundDashboard?.synchronizationTimestamp, synchronisationTime)
    }

    @Test
    internal fun `test findAll dashboard`() {
        mongoTemplate.save(dashboard)

        val dashboards = dashboardRepository.findAll()

        assertEquals(dashboards.size, 1)
        assertEquals(dashboards[0].id, dashboardId)
        assertEquals(dashboards[0].name, dashboardName)
    }

    @Test
    internal fun `test delete dashboard by id`() {
        mongoTemplate.save(dashboard)
        dashboardRepository.deleteById(dashboardId)

        val foundDashboard = mongoTemplate.findById(dashboardId, Dashboard::class.java)

        assertNull(foundDashboard)
    }

    @Test
    internal fun `test update synchronisation time`() {
        val newSynchronisationTime = 13432L
        mongoTemplate.save(dashboard)

        val updateSynchronizationTime =
            dashboardRepository.updateSynchronizationTime(dashboardId, newSynchronisationTime)

        assertEquals(updateSynchronizationTime, newSynchronisationTime)


        val findById = mongoTemplate.findById(dashboardId, Dashboard::class.java)

        assertEquals(findById?.synchronizationTimestamp, newSynchronisationTime)
    }
}