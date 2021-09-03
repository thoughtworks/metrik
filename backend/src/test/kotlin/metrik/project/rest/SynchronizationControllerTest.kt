package metrik.project.rest

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.justRun
import metrik.exception.ApplicationException
import metrik.project.rest.applicationservice.SynchronizationApplicationService
import org.apache.logging.log4j.util.Strings
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@WebMvcTest(controllers = [SynchronizationController::class])
@Import(SynchronizationApplicationService::class)
internal class SynchronizationControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var synchronizationApplicationService: SynchronizationApplicationService

    @Test
    fun `should return update timestamp when update all build data success`() {
        val updatedTimestamp: Long = 12345
        val projectId = "fake-project-id"

        every { synchronizationApplicationService.synchronize(projectId) } returns updatedTimestamp

        mockMvc.perform(MockMvcRequestBuilders.post("/api/project/$projectId/synchronization"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.synchronizationTimestamp").value(updatedTimestamp))
    }

    @Test
    fun `should return a non-blocking event stream when attempt to sync build data in background`() {
        val updatedTimestamp: Long = 12345
        val projectId = "fake-project-id"

        every { synchronizationApplicationService.synchronize(projectId, any()) } returns updatedTimestamp

        val mvcResult: MvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/project/$projectId/sse-sync"))
            .andExpect(MockMvcResultMatchers.request().asyncStarted())
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()

        mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(TEXT_EVENT_STREAM_VALUE))
    }

    @Test
    fun `should return 500 when update all build data failed`() {
        val projectId = "fake-project-id"

        justRun { synchronizationApplicationService.synchronize(projectId) }

        mockMvc.perform(MockMvcRequestBuilders.post("/api/project/$projectId/synchronization"))
            .andExpect(MockMvcResultMatchers.status().is5xxServerError)
    }

    @Test
    fun `should return last update timestamp`() {
        val updatedTimestamp: Long = 12345
        val projectId = "fake-project-id"

        every { synchronizationApplicationService.getLastSyncTimestamp(projectId) } returns updatedTimestamp

        mockMvc.perform(MockMvcRequestBuilders.get("/api/project/$projectId/synchronization/"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.synchronizationTimestamp").value(updatedTimestamp))
    }

    @Test
    fun `should return null when last sync timestamp not exist`() {
        val projectId = "fake-project-id"

        every { synchronizationApplicationService.getLastSyncTimestamp(projectId) } returns null

        mockMvc.perform(MockMvcRequestBuilders.get("/api/project/$projectId/synchronization"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.synchronizationTimestamp").value(null))
    }

    @Test
    fun `should return 404 given project id not exist when synchronization`() {
        val projectId = "fake-project-id"

        every { synchronizationApplicationService.synchronize(projectId) } throws ApplicationException(
            HttpStatus.NOT_FOUND,
            Strings.EMPTY
        )

        mockMvc.perform(MockMvcRequestBuilders.post("/api/project/$projectId/synchronization"))
            .andExpect(MockMvcResultMatchers.status().isNotFound)
    }

    @Test
    fun `should return 404 given project id not exist when get synchronization record`() {
        val projectId = "fake-project-id"

        every { synchronizationApplicationService.getLastSyncTimestamp(projectId) } throws ApplicationException(
            HttpStatus.NOT_FOUND,
            Strings.EMPTY
        )

        mockMvc.perform(MockMvcRequestBuilders.get("/api/project/$projectId/synchronization"))
            .andExpect(MockMvcResultMatchers.status().isNotFound)
    }
}
