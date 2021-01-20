package fourkeymetrics.resource

import fourkeymetrics.service.ConfigurationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class ConfigurationResource {

    @Autowired
    private lateinit var configurationService: ConfigurationService

    @GetMapping("/api/pipeline/verify")
    fun verifyPipeline(
        @RequestParam url: String,
        @RequestParam token: String,
        @RequestParam username: String,
        @RequestParam type: String
    ) {
        configurationService.verifyPipeline(url, username, token, type)
    }


}