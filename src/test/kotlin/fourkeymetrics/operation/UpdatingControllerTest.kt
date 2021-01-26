package fourkeymetrics.operation

import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@WebMvcTest(controllers = [UpdatingController::class])
@Import(UpdatingService::class)
class UpdatingControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var updatingService: UpdatingService

    @Test
    internal fun `should return update timestamp when update all build data success`() {
        val updatedTimestamp: Long = 12345

        `when`(updatingService.update()).thenReturn(updatedTimestamp)

        mockMvc.perform(MockMvcRequestBuilders.post("/api/build"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.updateTimestamp").value(updatedTimestamp))
    }

    @Test
    internal fun `should return 500 when update all build data failed`() {
        `when`(updatingService.update()).thenReturn(null)

        mockMvc.perform(MockMvcRequestBuilders.post("/api/build"))
            .andExpect(MockMvcResultMatchers.status().is5xxServerError)
    }
}