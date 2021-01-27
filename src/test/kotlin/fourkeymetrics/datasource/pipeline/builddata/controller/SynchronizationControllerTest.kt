package fourkeymetrics.datasource.pipeline.builddata.controller

import fourkeymetrics.dashboard.controller.SynchronizationController
import fourkeymetrics.dashboard.controller.SynchronizationService
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
@Import(SynchronizationService::class)
class SynchronizationControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var synchronizationService: SynchronizationService

    @Test
    internal fun `should return update timestamp when update all build data success`() {
        val updatedTimestamp: Long = 12345
        val dashboardId = "fake-dashboard-id"
        val pipelineId = "fake-pipeline-id"

        `when`(synchronizationService.update(dashboardId, pipelineId)).thenReturn(updatedTimestamp)

        mockMvc.perform(MockMvcRequestBuilders.post("/api/dashboard/$dashboardId/synchronization"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.updateTimestamp").value(updatedTimestamp))
    }

    @Test
    internal fun `should return 500 when update all build data failed`() {
        val dashboardId = "fake-dashboard-id"
        val pipelineId = "fake-pipeline-id"

        `when`(synchronizationService.update(dashboardId, pipelineId)).thenReturn(null)

        mockMvc.perform(MockMvcRequestBuilders.post("/api/dashboard/$dashboardId/synchronization"))
            .andExpect(MockMvcResultMatchers.status().is5xxServerError)
    }
}