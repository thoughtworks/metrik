package metrik.config

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

data class TestBean(val foo: String)

@TestConfiguration
class ApiTestConfiguration {
    @Bean
    fun testBean(): TestBean = TestBean("bar")
}