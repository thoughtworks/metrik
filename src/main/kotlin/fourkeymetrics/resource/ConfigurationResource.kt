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


}