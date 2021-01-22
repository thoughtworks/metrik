package fourkeymetrics.resource

import fourkeymetrics.model.Dashboard
import fourkeymetrics.model.PipelineConfiguration
import fourkeymetrics.service.ConfigurationService
import fourkeymetrics.vo.ConfigurationVo
import fourkeymetrics.vo.PipelineConfigurationVo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

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
    fun save(@RequestBody config: ConfigurationVo):Dashboard{
        return configurationService.save(config)
    }

    @PutMapping("api/dashboard/{dashboardId}/pipeline/{pipelineId}/config/")
    fun update(@RequestBody config: PipelineConfigurationVo,@PathVariable("dashboardId") dashboardId:String,@PathVariable("pipelineId") pipelineId:String ):PipelineConfiguration{
      return  configurationService.update(config,dashboardId,pipelineId)
    }

}