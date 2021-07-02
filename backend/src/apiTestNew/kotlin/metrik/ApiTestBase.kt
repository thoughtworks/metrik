package metrik

import io.restassured.RestAssured
import metrik.config.ApiTestConfiguration
import metrik.project.domain.model.Pipeline
import metrik.project.domain.model.PipelineType
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
    private lateinit var mongoTemplate: MongoTemplate

    @BeforeEach
    fun setUp() {
        RestAssured.port = springTestRandomServerPort

        val project = Project("601cbae825c1392117aa0429", "4-key", 1580709600000L)
        mongoTemplate.save(project)

        val pipeline = Pipeline(
            "601cbb3425c1392117aa053b",
            "601cbae825c1392117aa0429",
            "dfservice",
            "E9fnMY3UGE6Oms35JzLGgQ==",
            "FVSR/5o1BYh6dJQBeQaNvQ==",
            "http://localhost:8001/job/4km-df/",
            PipelineType.JENKINS
        )
        mongoTemplate.save(pipeline)
    }
}
