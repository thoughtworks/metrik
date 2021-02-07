package fourkeymetrics.dashboard.controller

import com.fasterxml.jackson.databind.ObjectMapper
import fourkeymetrics.dashboard.buildPipelineRequest
import fourkeymetrics.dashboard.controller.applicationservice.DashboardApplicationService
import fourkeymetrics.dashboard.controller.vo.request.DashboardRequest
import fourkeymetrics.dashboard.controller.vo.response.DashboardDetailResponse
import fourkeymetrics.dashboard.controller.vo.response.DashboardResponse
import fourkeymetrics.dashboard.controller.vo.response.PipelineResponse
import fourkeymetrics.dashboard.controller.vo.response.PipelineStagesResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(controllers = [DashboardController::class])
class DashboardControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var dashboardApplicationService: DashboardApplicationService

    private val dashboardId = "dashboardId"
    private val pipelineId = "pipelineId"
    private val dashboardName = "dashboardName"
    private val pipelineName = "pipelineName"
    private lateinit var dashboardDetailsResponse: DashboardDetailResponse

    @BeforeEach
    internal fun setUp() {
        dashboardDetailsResponse = DashboardDetailResponse(
            dashboardId,
            dashboardName,
            pipelines = listOf(PipelineResponse(pipelineId, pipelineName))
        )
    }

    @Test
    internal fun `should get dashboards successfully `() {

        `when`(dashboardApplicationService.getDashboards()).thenReturn(
            listOf(
                DashboardResponse(
                    dashboardId,
                    dashboardName
                )
            )
        )

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/dashboard")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$[0].id").value(dashboardId))
            .andExpect(jsonPath("$[0].name").value(dashboardName))
    }

    @Test
    internal fun `should get dashboard details successfully `() {

        `when`(dashboardApplicationService.getDashboardDetails(dashboardId)).thenReturn(dashboardDetailsResponse)

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/dashboard/${dashboardId}")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(dashboardId))
            .andExpect(jsonPath("$.name").value(dashboardName))
            .andExpect(jsonPath("$.pipelines[0].id").value(pipelineId))
            .andExpect(jsonPath("$.pipelines[0].name").value(pipelineName))
    }

    @Test
    internal fun `should create dashboard and pipeline `() {
        val dashboardRequest = DashboardRequest(dashboardName, buildPipelineRequest())
        `when`(dashboardApplicationService.createDashboard(dashboardRequest)).thenReturn(dashboardDetailsResponse)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/dashboard")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectMapper().writeValueAsString(dashboardRequest))
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(dashboardId))
            .andExpect(jsonPath("$.name").value(dashboardName))
            .andExpect(jsonPath("$.pipelines[0].id").value(pipelineId))
            .andExpect(jsonPath("$.pipelines[0].name").value(pipelineName))
    }

    @Test
    internal fun `should update dashboard name `() {
        val dashboardNewName = "dashboardNewName"
        `when`(dashboardApplicationService.updateDashboardName(dashboardId, dashboardNewName)).thenReturn(
            DashboardResponse(dashboardId, dashboardNewName)
        )

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/dashboard/${dashboardId}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(dashboardNewName)
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(dashboardId))
            .andExpect(jsonPath("$.name").value(dashboardNewName))
    }

    @Test
    internal fun `should delete dashboard `() {
        Mockito.doNothing().`when`(dashboardApplicationService).deleteDashboard(dashboardId)
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/dashboard/$dashboardId")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk)
    }

    @Test
    internal fun `should get dashbaord pipeline stages `() {
        val pipelineStagesResponse = PipelineStagesResponse(pipelineId, pipelineName, listOf("some stage"))
        `when`(dashboardApplicationService.getPipelineStages(dashboardId)).thenReturn(listOf(pipelineStagesResponse))
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/dashboard/$dashboardId/pipelines-stages")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$[0].pipelineId").value(pipelineId))
            .andExpect(jsonPath("$[0].pipelineName").value(pipelineName))
    }
}