package fourkeymetrics.dashboard.controller

import fourkeymetrics.MockitoHelper.anyObject
import fourkeymetrics.MockitoHelper.argThat
import fourkeymetrics.dashboard.buildPipelineRequest
import fourkeymetrics.dashboard.controller.applicationservice.DashboardApplicationService
import fourkeymetrics.dashboard.controller.applicationservice.PipelineApplicationService
import fourkeymetrics.dashboard.controller.vo.request.DashboardRequest
import fourkeymetrics.dashboard.controller.vo.request.PipelineRequest
import fourkeymetrics.dashboard.exception.DashboardNameDuplicateException
import fourkeymetrics.dashboard.model.Dashboard
import fourkeymetrics.dashboard.model.Pipeline
import fourkeymetrics.dashboard.model.PipelineType
import fourkeymetrics.dashboard.repository.DashboardRepository
import fourkeymetrics.dashboard.repository.PipelineRepository
import fourkeymetrics.dashboard.service.jenkins.JenkinsPipelineService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.anyList
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpStatus

@ExtendWith(MockitoExtension::class)
class DashboardApplicationServiceTest {
    @InjectMocks
    private lateinit var dashboardApplicationService: DashboardApplicationService

    @Mock
    private lateinit var jenkinsPipelineService: JenkinsPipelineService

    @Mock
    private lateinit var pipelineApplicationService: PipelineApplicationService

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

        val pipeline = buildPipelineRequest()
        val dashboardResponse =
            dashboardApplicationService.createDashboard(DashboardRequest(dashboardName, pipeline))

        assertEquals(dashboardResponse.id, dashboardId)
        verify(pipelineApplicationService).verifyPipelineConfiguration(argThat {
            assertEquals(pipeline.url, it.url)
            assertEquals(pipeline.username, it.username)
            assertEquals(pipeline.credential, it.credential)
            true
        })
    }

    @Test
    internal fun `create dashboard will fail if dashboard name already exist`() {
        `when`(dashboardRepository.existWithGivenName(dashboardName)).thenReturn(true)

        val exception = assertThrows<DashboardNameDuplicateException> {
            dashboardApplicationService.createDashboard(DashboardRequest(dashboardName, PipelineRequest()))
        }

        assertEquals(exception.httpStatus, HttpStatus.BAD_REQUEST)
        assertEquals(exception.message, "Dashboard name duplicate")
    }

    @Test
    internal fun `update dashboard name`() {
        `when`(dashboardRepository.findById(dashboardId)).thenReturn(expectedDashboard)
        `when`(dashboardRepository.save(expectedDashboard)).thenReturn(expectedDashboard)

        val newDashboardName = "new dashboard name"
        val updatedDashboard = dashboardApplicationService.updateDashboardName(dashboardId, newDashboardName)

        assertEquals(updatedDashboard.name, newDashboardName)
    }

    @Test
    internal fun `test delete pipeline`() {
        dashboardApplicationService.deleteDashboard(dashboardId)

        verify(dashboardRepository, times(1)).deleteById(dashboardId)
        verify(pipelineRepository, times(1)).deleteByDashboardId(dashboardId)
    }

    @Test
    internal fun `test get pipeline stages`() {
        val pipeline1Id = "pipeline1Id"
        val pipeline2Id = "pipeline2Id"
        val pipeline3Id = "pipeline3Id"
        val pipeline1Name = "bpipelineName"
        val pipeline2Name = "ApipelineName"
        val pipeline3Name = "apipelineName"
        val pipeline1StageName = "pipeline1StageName"
        val pipeline2StageName = "pipeline2StageName"
        val pipeline3StageName = "pipeline3StageName"
        val pipeline1 = Pipeline(pipeline1Id, dashboardId, pipeline1Name)
        val pipeline2 = Pipeline(pipeline2Id, dashboardId, pipeline2Name)
        val pipeline3 = Pipeline(pipeline3Id, dashboardId, pipeline3Name)

        `when`(dashboardRepository.findById(dashboardId)).thenReturn(expectedDashboard)
        `when`(pipelineRepository.findByDashboardId(dashboardId)).thenReturn(listOf(pipeline1, pipeline2, pipeline3))
        `when`(jenkinsPipelineService.getStagesSortedByName(pipeline1Id)).thenReturn(listOf(pipeline1StageName))
        `when`(jenkinsPipelineService.getStagesSortedByName(pipeline2Id)).thenReturn(listOf(pipeline2StageName))
        `when`(jenkinsPipelineService.getStagesSortedByName(pipeline3Id)).thenReturn(listOf(pipeline3StageName))

        val pipelineStages = dashboardApplicationService.getPipelineStages(dashboardId)

        assertEquals(pipelineStages.size, 3)
        assertEquals(pipelineStages[0].pipelineId, pipeline2Id)
        assertEquals(pipelineStages[1].pipelineId, pipeline3Id)
        assertEquals(pipelineStages[2].pipelineId, pipeline1Id)
    }

    @Test
    internal fun `test get pipeline details`() {
        `when`(dashboardRepository.findById(dashboardId)).thenReturn(expectedDashboard)
        `when`(pipelineRepository.findByDashboardId(dashboardId)).thenReturn(listOf(expectedPipeline))

        val dashboardDetails = dashboardApplicationService.getDashboardDetails(dashboardId)

        assertEquals(dashboardDetails.id, dashboardId)
        assertEquals(dashboardDetails.name, dashboardName)
        assertEquals(dashboardDetails.pipelines[0].id, pipelineId)
        assertEquals(dashboardDetails.pipelines[0].name, pipelineName)
    }

    @Test
    internal fun `test get dashboard list`() {
        `when`(dashboardRepository.findAll()).thenReturn(listOf(expectedDashboard))

        val dashboards = dashboardApplicationService.getDashboards()

        assertEquals(dashboards.size, 1)
        assertEquals(dashboards[0].id, dashboardId)
        assertEquals(dashboards[0].name, dashboardName)
    }
}