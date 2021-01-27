package fourkeymetrics.datasource.pipeline.builddata.controller

import fourkeymetrics.dashboard.controller.SynchronizationApplicationService
import fourkeymetrics.dashboard.controller.SynchronizationController
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@WebMvcTest(controllers = [SynchronizationController::class])
@Import(SynchronizationApplicationService::class)
class SynchronizationControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var synchronizationApplicationService: SynchronizationApplicationService

    @Test
    internal fun `should return update timestamp when update all build data success`() {
        val updatedTimestamp: Long = 12345
        val dashboardId = "fake-dashboard-id"

        `when`(synchronizationApplicationService.synchronize(dashboardId)).thenReturn(updatedTimestamp)
        `when`(synchronizationApplicationService.isDashboardExist(dashboardId)).thenReturn(true)

        mockMvc.perform(MockMvcRequestBuilders.post("/api/dashboard/$dashboardId/synchronization"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.synchronizationTimestamp").value(updatedTimestamp))
    }

    @Test
    internal fun `should return 500 when update all build data failed`() {
        val dashboardId = "fake-dashboard-id"

        `when`(synchronizationApplicationService.synchronize(dashboardId)).thenReturn(null)
        `when`(synchronizationApplicationService.isDashboardExist(dashboardId)).thenReturn(true)

        mockMvc.perform(MockMvcRequestBuilders.post("/api/dashboard/$dashboardId/synchronization"))
            .andExpect(MockMvcResultMatchers.status().is5xxServerError)
    }

    @Test
    internal fun `should return last update timestamp`() {
        val updatedTimestamp: Long = 12345
        val dashboardId = "fake-dashboard-id"

        `when`(synchronizationApplicationService.getLastSyncTimestamp(dashboardId)).thenReturn(updatedTimestamp)
        `when`(synchronizationApplicationService.isDashboardExist(dashboardId)).thenReturn(true)

        mockMvc.perform(MockMvcRequestBuilders.get("/api/dashboard/$dashboardId/synchronization/"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.synchronizationTimestamp").value(updatedTimestamp))
    }

    @Test
    internal fun `should return null when last sync timestamp not exist`() {
        val dashboardId = "fake-dashboard-id"

        `when`(synchronizationApplicationService.getLastSyncTimestamp(dashboardId)).thenReturn(null)
        `when`(synchronizationApplicationService.isDashboardExist(dashboardId)).thenReturn(true)

        mockMvc.perform(MockMvcRequestBuilders.get("/api/dashboard/$dashboardId/synchronization"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.synchronizationTimestamp").value(null))
    }

    @Test
    internal fun `should return 400 given dashboard id not exist when synchronization`() {
        val dashboardId = "fake-dashboard-id"

        `when`(synchronizationApplicationService.isDashboardExist(dashboardId)).thenReturn(false)

        mockMvc.perform(MockMvcRequestBuilders.post("/api/dashboard/$dashboardId/synchronization"))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    internal fun `should return 400 given dashboard id not exist when get synchronization record`() {
        val dashboardId = "fake-dashboard-id"

        `when`(synchronizationApplicationService.isDashboardExist(dashboardId)).thenReturn(false)

        mockMvc.perform(MockMvcRequestBuilders.get("/api/dashboard/$dashboardId/synchronization"))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
    }
}