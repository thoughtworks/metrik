package fourkeymetrics.resource.configuration

import fourkeymetrics.model.Dashboard
import fourkeymetrics.model.PipelineConfiguration
import fourkeymetrics.service.ConfigurationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.PathVariable


@RestController
class ConfigurationResource {

    @Autowired
    private lateinit var configurationService: ConfigurationService

    @GetMapping("/api/pipeline/verify")
    fun verifyPipeline(
        @RequestParam url: String,
        @RequestParam username: String,
        @RequestParam credential: String,
        @RequestParam type: String
    ) {
        configurationService.verifyPipeline(url, username, credential, type)
    }

    @PostMapping("api/pipeline/config")
    fun save(@RequestBody config: DashboardConfigurationRequest):Dashboard{
        return configurationService.save(config)
    }

    @PutMapping("api/dashboard/{dashboardId}/pipeline/{pipelineId}/config/")
    fun update(@RequestBody config: PipelineConfigurationRequest,
               @PathVariable("dashboardId") dashboardId:String,
               @PathVariable("pipelineId") pipelineId:String ):PipelineConfiguration{
      return  configurationService.update(config,dashboardId,pipelineId)
    }

}