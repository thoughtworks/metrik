package metrik

import io.restassured.RestAssured
import io.restassured.http.ContentType
import metrik.base.ApiTestBase
import metrik.project.domain.model.Pipeline
import metrik.project.domain.model.PipelineType
import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class PipelineApiTest : ApiTestBase() {
    @BeforeAll
    fun setUpPipelineTestData() {
        mongoTemplate.save(
            Pipeline(
                id = "601cbb3425c1392117aa053b",
                projectId = "601cbae825c1392117aa0429",
                name = "df",
                username = "E9fnMY3UGE6Oms35JzLGgQ==",
                credential = "FVSR/5o1BYh6dJQBeQaNvQ==",
                url = "http://localhost:8001/job/4km-df/",
                type = PipelineType.JENKINS
            )
        )
        mongoTemplate.save(
            Pipeline(
                id = "601cbae825c1392117aa0429",
                projectId = "601cbae825c1392117aa0429",
                name = "cfr",
                username = "E9fnMY3UGE6Oms35JzLGgQ==",
                credential = "FVSR/5o1BYh6dJQBeQaNvQ==",
                url = "http://localhost:8001/job/4km-cfr/",
                type = PipelineType.JENKINS
            )
        )
    }

//    @Test
//    fun `should verify and save a pipeline config successfully`() {
//        val createPipelineConfigRequest = """
//            {
//              "url": "http://localhost:8001/job/testtttt/",
//              "type": "JENKINS",
//              "name": "testtttt",
//              "username": "testttt",
//              "credential": "testtttt"
//            }
//        """.trimIndent()
//
//        RestAssured
//            .given()
//            .contentType(ContentType.JSON)
//            .body(createPipelineConfigRequest)
//            .post("/api/project/601cbae825c1392117aa0429/pipeline")
//            .then()
//            .statusCode(200)
//    }

    @Test
    fun `should get pipeline config details with a valid ID`() {
        RestAssured
            .given()
            .contentType(ContentType.JSON)
            .get("/api/project/601cbae825c1392117aa0429/pipeline/601cbb3425c1392117aa053b")
            .then()
            .statusCode(200)
            .body("id", `is`("601cbb3425c1392117aa053b"))
            .body("name", `is`("df"))
            .body("username", `is`("E9fnMY3UGE6Oms35JzLGgQ=="))
            .body("credential", `is`("FVSR/5o1BYh6dJQBeQaNvQ=="))
            .body("url", `is`("http://localhost:8001/job/4km-df/"))
            .body("type", `is`("JENKINS"))
    }
}
