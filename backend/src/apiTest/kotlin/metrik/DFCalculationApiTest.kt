package metrik

import io.restassured.RestAssured
import io.restassured.http.ContentType
import metrik.base.ApiTestBase
import metrik.fixtures.dfExecution1
import metrik.fixtures.dfExecution2
import metrik.fixtures.dfExecution3
import metrik.fixtures.dfPipeline
import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class DFCalculationApiTest : ApiTestBase() {
    @BeforeAll
    fun setUpBuildDataFixture() {
        // init pipeline config
        mongoTemplate.save(dfPipeline)
        // init build data
        val buildCollectionName = "build"
        mongoTemplate.save(dfExecution1, buildCollectionName)
        mongoTemplate.save(dfExecution2, buildCollectionName)
        mongoTemplate.save(dfExecution3, buildCollectionName)
    }

    @Test
    fun `targeted stage status is successful and time is within the selected date range then stage should be counted in`() {
        val requestBody = """
            {
                "endTime": 1609516799000,
                "pipelineStages": [
                    {
                        "pipelineId": "601cbb3425c1392117aa053b",
                        "stage": "Deploy to DEV"
                    }
                ],
                "startTime": 1609430400000,
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
            .body("deploymentFrequency.summary.value", equalTo(14.0F))
            .body("deploymentFrequency.summary.level", equalTo("HIGH"))
            .body("deploymentFrequency.details[0].value", equalTo(1))
    }

    @Test
    fun `targeted stage status is successful but time is not within the selected date range then stage should not be counted in`() {
        val requestBody = """
            {
                "endTime": 1609459200000,
                "pipelineStages": [
                    {
                        "pipelineId": "601cbb3425c1392117aa053b",
                        "stage": "Deploy to DEV"
                    }
                ],
                "startTime": 1609430400000,
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
    }

    @Test
    fun `targeted stage status is failure and selected time is within the build date range then stage should not be counted in`() {
        val requestBody = """
            {
                "endTime": 1609470000000,
                "pipelineStages": [
                    {
                        "pipelineId": "601cbb3425c1392117aa053b",
                        "stage": "Deploy to DEV"
                    }
                ],
                "startTime": 1609466400000,
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
    }

    @Test
    fun `targeted stage status is aborted and selected time is within the build date range then stage should not be counted in`() {
        val requestBody = """
            {
                "endTime": 1609470000000,
                "pipelineStages": [
                    {
                        "pipelineId": "601cbb3425c1392117aa053b",
                        "stage": "Deploy to DEV"
                    }
                ],
                "startTime": 1609466400000,
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
    }

    @Test
    fun `targeted stage start time is within the selected time range but finishing time is not within selected time range then stage should not be counted in`() {
        val requestBody = """
            {
                "endTime": 1609462832000,
                "pipelineStages": [
                    {
                        "pipelineId": "601cbb3425c1392117aa053b",
                        "stage": "Deploy to DEV"
                    }
                ],
                "startTime": 1609462500000,
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
    }

    @Test
    fun `targeted stages contains success, failure and aborted during selected time range then only success stage should be counted in`() {
        val requestBody = """
            {
                "endTime": 1609516799000,
                "pipelineStages": [
                    {
                        "pipelineId": "601cbb3425c1392117aa053b",
                        "stage": "Deploy to DEV"
                    }
                ],
                "startTime": 1609430400000,
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
            .body("deploymentFrequency.summary.value", equalTo(14.0F))
            .body("deploymentFrequency.summary.level", equalTo("HIGH"))
            .body("deploymentFrequency.details[0].value", equalTo(1))
    }
}
