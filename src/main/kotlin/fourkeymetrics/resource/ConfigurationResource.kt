package fourkeymetrics.resource

import fourkeymetrics.model.Dashboard
import fourkeymetrics.model.PipelineConfiguration
import fourkeymetrics.service.ConfigurationService
import fourkeymetrics.vo.ConfigurationVo
import fourkeymetrics.vo.PipelineConfigurationVo
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

    // TODO: 2021/1/22 without test
    @PostMapping("api/pipeline/config")
    fun save(@RequestBody config: ConfigurationVo):Dashboard{
        return configurationService.save(config)
    }

    // TODO: 2021/1/22 without test
    @PutMapping("api/dashboard/{dashboardId}/pipeline/{pipelineId}/config/")
    fun update(@RequestBody config: PipelineConfigurationVo,
               @PathVariable("dashboardId") dashboardId:String,
               @PathVariable("pipelineId") pipelineId:String ):PipelineConfiguration{
      return  configurationService.update(config,dashboardId,pipelineId)
    }

}