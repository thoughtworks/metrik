package fourkeymetrics.datasource.pipeline.configuration

import fourkeymetrics.datasource.pipeline.configuration.model.Dashboard
import fourkeymetrics.datasource.pipeline.configuration.model.PipelineConfiguration
import fourkeymetrics.datasource.pipeline.configuration.vo.DashboardConfigurationRequest
import fourkeymetrics.datasource.pipeline.configuration.vo.PipelineConfigurationRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.PathVariable


@RestController
class ConfigurationController {

    @Autowired
    private lateinit var configurationApplicationService: ConfigurationApplicationService

    @GetMapping("/api/pipeline/verify")
    fun verifyPipeline(
        @RequestParam url: String,
        @RequestParam username: String,
        @RequestParam credential: String,
        @RequestParam type: String
    ) {
        configurationApplicationService.verifyPipeline(url, username, credential, type)
    }

    @PostMapping("api/pipeline/config")
    fun save(@RequestBody config: DashboardConfigurationRequest): Dashboard {
        return configurationApplicationService.save(config)
    }

    @PutMapping("api/dashboard/{dashboardId}/pipeline/{pipelineId}/config/")
    fun update(@RequestBody config: PipelineConfigurationRequest,
               @PathVariable("dashboardId") dashboardId:String,
               @PathVariable("pipelineId") pipelineId:String ): PipelineConfiguration {
      return  configurationApplicationService.update(config,dashboardId,pipelineId)
    }

}