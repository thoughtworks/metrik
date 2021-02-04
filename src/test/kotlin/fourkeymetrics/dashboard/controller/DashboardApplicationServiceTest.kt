package fourkeymetrics.dashboard.controller

import fourkeymetrics.MockitoHelper.anyObject
import fourkeymetrics.dashboard.controller.applicationservice.DashboardApplicationService
import fourkeymetrics.dashboard.controller.vo.request.DashboardRequest
import fourkeymetrics.dashboard.controller.vo.request.PipelineRequest
import fourkeymetrics.dashboard.model.Dashboard
import fourkeymetrics.dashboard.model.Pipeline
import fourkeymetrics.dashboard.model.PipelineType
import fourkeymetrics.dashboard.repository.DashboardRepository
import fourkeymetrics.dashboard.repository.PipelineRepository
import fourkeymetrics.dashboard.service.jenkins.JenkinsPipelineService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.anyList
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class DashboardApplicationServiceTest {
    @InjectMocks
    private lateinit var dashboardApplicationService: DashboardApplicationService

    @Mock
    private lateinit var jenkinsPipelineFacade: JenkinsPipelineService

    @Mock
    private lateinit var dashboardRepository: DashboardRepository

    @Mock
    private lateinit var pipelineRepository: PipelineRepository

    private var dashboardId = "601a2d059deac2220dd0756b"
    private var dashboardName = "first dashboard"
    private var expectedDashboard = Dashboard(dashboardId, dashboardName)
    private var pipelineId = "501a2d059deac2220dd0756f"
    private var pipelineName = "pipeline name"
    private var pipelineUrl = "htttp://localhost:8080/some-pipeline"
    private var pipelineUserName = "4km"
    private var pipelineCredential = "4km"
    private var expectedPipeline =
        Pipeline(
            pipelineId, dashboardId, pipelineName, pipelineUserName,
            pipelineCredential, pipelineUrl, PipelineType.JENKINS
        )


    @Test
    internal fun `test create dashboard successfully`() {
        `when`(dashboardRepository.existWithGivenName(dashboardName)).thenReturn(false)
        `when`(dashboardRepository.save(anyObject())).thenReturn(expectedDashboard)
        `when`(pipelineRepository.saveAll(anyList())).thenReturn(listOf(expectedPipeline))

        val dashboardResponse =
            dashboardApplicationService.createDashboard(DashboardRequest(dashboardName, PipelineRequest()))

        assertEquals(dashboardResponse.id, dashboardId)
    }
}