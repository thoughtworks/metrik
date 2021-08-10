package metrik.base

import io.restassured.RestAssured
import metrik.config.ApiTestConfiguration
import metrik.project.domain.model.Project
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.context.annotation.Import
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("apitest")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(ApiTestConfiguration::class)
@AutoConfigureDataMongo
internal class ApiTestBase {
    @LocalServerPort
    var springTestRandomServerPort = 0

    @Autowired
    lateinit var mongoTemplate: MongoTemplate

    @BeforeEach
    fun setUp() {
        RestAssured.port = springTestRandomServerPort

        mongoTemplate.save(
            Project(
                id = "601cbae825c1392117aa0429",
                name = "4-key",
                synchronizationTimestamp = 1580709600000L
            )
        )
    }
}
