package metrik

import io.restassured.RestAssured
import metrik.config.ApiTestConfiguration
import org.junit.jupiter.api.BeforeEach
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("apitest")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(ApiTestConfiguration::class)
internal class ApiTestBase {
    @LocalServerPort
    var springTestRandomServerPort = 0

    @BeforeEach
    fun setUp() {
        RestAssured.port = springTestRandomServerPort
    }
}