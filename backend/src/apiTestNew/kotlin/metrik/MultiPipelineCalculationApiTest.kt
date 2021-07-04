package metrik

import io.restassured.RestAssured
import io.restassured.http.ContentType
import metrik.base.ApiTestBase
import metrik.fixtures.multiPipeline1
import metrik.fixtures.multiPipeline2
import metrik.fixtures.multiPipelineBuilds
import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class MultiPipelineCalculationApiTest : ApiTestBase() {
    @BeforeAll
    fun setUpBuildDataFixture() {
        // init pipeline config
        mongoTemplate.save(multiPipeline1)
        mongoTemplate.save(multiPipeline2)
        // init build data
        val buildCollectionName = "build"
        multiPipelineBuilds.forEach {
            mongoTemplate.save(it, buildCollectionName)
        }
    }

    @Test
    fun `multiple pipelines when several pipelines have deployments`() {
        val requestBody = """
            {
                "endTime": 1578844799000,
                "pipelineStages": [
                    {
                        "pipelineId": "600a701221048076f92c4e43",
                        "stage": "Deploy to UAT"
                   },
                   {
                       "pipelineId": "601a2f129deac2220dd07570",
                       "stage": "Deploy to Staging"
                   }
                ],
                "startTime": 1578499200000,
                "unit": "Monthly"
            }
        """.trimIndent()

        RestAssured
            .given()
            .contentType(ContentType.JSON)
            .body(requestBody)
            .post("/api/pipeline/metrics")
            .then()
            .statusCode(200)
            .body("deploymentFrequency.summary.value", equalTo(15.0F))
            .body("deploymentFrequency.summary.level", equalTo("HIGH"))
            .body("deploymentFrequency.details[0].value", equalTo(2))
            .body("leadTimeForChange.summary.value", equalTo(1.24F))
            .body("leadTimeForChange.summary.level", equalTo("HIGH"))
            .body("leadTimeForChange.details[0].value", equalTo(1.24F))
            .body("meanTimeToRestore.summary.value", equalTo(55.93F))
            .body("meanTimeToRestore.summary.level", equalTo("MEDIUM"))
            .body("meanTimeToRestore.details[0].value", equalTo(55.93F))
            .body("changeFailureRate.summary.value", equalTo(50.0F))
            .body("changeFailureRate.summary.level", equalTo("LOW"))
            .body("changeFailureRate.details[0].value", equalTo(50.0F))
    }

    @Test
    fun `multiple pipelines when none of them has deployment data`() {
        val requestBody = """
            {
                "endTime": 1578153599000,
                "pipelineStages": [
                    {
                        "pipelineId": "600a701221048076f92c4e43",
                        "stage": "Deploy to UAT"
                   },
                   {
                       "pipelineId": "601a2f129deac2220dd07570",
                       "stage": "Deploy to Staging"
                   }
                ],
                "startTime": 1577808000000,
                "unit": "Fortnightly"
            }
        """.trimIndent()

        RestAssured
            .given()
            .contentType(ContentType.JSON)
            .body(requestBody)
            .post("/api/pipeline/metrics")
            .then()
            .statusCode(200)
            .body("deploymentFrequency.summary.value", equalTo(0.0F))
            .body("deploymentFrequency.summary.level", equalTo("LOW"))
            .body("deploymentFrequency.details[0].value", equalTo(0))
            .body("leadTimeForChange.summary.value", equalTo("NaN"))
            .body("leadTimeForChange.summary.level", equalTo("INVALID"))
            .body("leadTimeForChange.details[0].value", equalTo("NaN"))
            .body("meanTimeToRestore.summary.value", equalTo("NaN"))
            .body("meanTimeToRestore.summary.level", equalTo("INVALID"))
            .body("meanTimeToRestore.details[0].value", equalTo("NaN"))
            .body("changeFailureRate.summary.value", equalTo("NaN"))
            .body("changeFailureRate.summary.level", equalTo("INVALID"))
            .body("changeFailureRate.details[0].value", equalTo("NaN"))
    }
}
