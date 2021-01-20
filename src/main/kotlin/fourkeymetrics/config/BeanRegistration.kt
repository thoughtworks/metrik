package fourkeymetrics.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
class BeanRegistration {
    @Bean
    fun restTemplate(): RestTemplate {
        return RestTemplate()
    }
}