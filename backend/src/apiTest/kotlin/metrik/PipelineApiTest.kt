package metrik

import io.restassured.RestAssured
import io.restassured.http.ContentType
import metrik.base.ApiTestBase
import metrik.project.domain.model.PipelineConfiguration
import metrik.project.domain.model.PipelineType
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
internal class PipelineApiTest : ApiTestBase() {
    @BeforeAll
    fun setUpPipelineTestData() {
        mongoTemplate.save(
            PipelineConfiguration(
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
            PipelineConfiguration(
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

    @Test
    @Order(1)
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

    @Test
    @Order(2)
    fun `should throw 400 error when pipeline config type if unrecognized pipeline type received`() {
        val requestBody = """
            {
                "type": "CIRCLE_CI",
                "url": "https://circleci.com/thoughtworks/metrik",
                "credential": "circleci_token"
            }
        """.trimIndent()

        RestAssured
            .given()
            .contentType(ContentType.JSON)
            .body(requestBody)
            .post("/api/pipeline/verify")
            .then()
            .statusCode(400)
    }

    //  Right now the following test can always pass, because the config type will always fallback to the default empty implementation of PipelineService.
    //  Once GithubActions implementation is added, these tests should be enabled immediately with necessary mocks. Consider using wiremock to stub the Github response.
    @Disabled
    @Test
    @Order(2)
    fun `should verify a Github Actions pipeline config successfully`() {
        val requestBody = """
            {
                "type": "GITHUB_ACTIONS",
                "url": "https://github.com/thoughtworks/metrik",
                "credential": "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"
            }
        """.trimIndent()

        RestAssured
            .given()
            .contentType(ContentType.JSON)
            .body(requestBody)
            .post("/api/pipeline/verify")
            .then()
            .statusCode(200)
    }

    @Disabled
    @Test
    @Order(3)
    fun `should verify and save a pipeline config successfully`() {
        val requestBody = """
            {
                "name": "a github actions project",
                "type": "GITHUB_ACTIONS",
                "url": "https://github.com/thoughtworks/metrik",
                "credential": "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"
            }
        """.trimIndent()

        RestAssured
            .given()
            .contentType(ContentType.JSON)
            .body(requestBody)
            .post("/api/project/601cbae825c1392117aa0429/pipeline")
            .then()
            .statusCode(201)
    }

    @Disabled
    @Test
    @Order(4)
    fun `should get all pipelines configured for the project with a valid project ID`() {
        RestAssured
            .given()
            .contentType(ContentType.JSON)
            .get("/api/project/601cbae825c1392117aa0429/pipelines-stages")
            .then()
            .statusCode(200)
            .body("[0].pipelineId", notNullValue())
            .body("[0].pipelineName", `is`("a github actions project"))
            .body("[0].stages", notNullValue())
            .body("[1].pipelineId", `is`("601cbae825c1392117aa0429"))
            .body("[1].pipelineName", `is`("cfr"))
            .body("[1].stages", notNullValue())
            .body("[2].pipelineId", `is`("601cbb3425c1392117aa053b"))
            .body("[2].pipelineName", `is`("df"))
            .body("[2].stages", notNullValue())
    }
}
