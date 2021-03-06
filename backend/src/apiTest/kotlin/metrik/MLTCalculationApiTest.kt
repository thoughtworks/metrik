package metrik

import io.restassured.RestAssured
import io.restassured.http.ContentType
import metrik.base.ApiTestBase
import metrik.fixtures.ltBuilds
import metrik.fixtures.ltPipelines
import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class MLTCalculationApiTest : ApiTestBase() {
    @BeforeAll
    fun setUpBuildDataFixture() {
        // init pipeline config
        ltPipelines.forEach {
            mongoTemplate.save(it)
        }
        // init build data
        val buildCollectionName = "build"
        ltBuilds.forEach {
            mongoTemplate.save(it, buildCollectionName)
        }
    }

    @Test
    fun `The deployment deployment done time is within the selected date range and the stage status is success should be counted in`() {
        val requestBody = """
            {
                "endTime": 1598388400000,
                "pipelineStages": [
                    {
                        "pipelineId": "6012505c42fbb8439fc08e17",
                        "stage": "deploy to prod"
                    }
                ],
                "startTime": 1598288400000,
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
            .body("leadTimeForChange.summary.value", equalTo(5.08F))
            .body("leadTimeForChange.summary.level", equalTo("HIGH"))
            .body("leadTimeForChange.details[0].value", equalTo(5.08F))
    }

    @Test
    fun `The deployment deployment done time is within the selected date range but the stage status is failed should not be counted in`() {
        val requestBody = """
            {
                "endTime": 1598388400000,
                "pipelineStages": [
                    {
                        "pipelineId": "6012505c42fbb8439fc08e16",
                        "stage": "deploy to prod"
                    }
                ],
                "startTime": 1598288400000,
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
            .body("leadTimeForChange.summary.value", equalTo("NaN"))
            .body("leadTimeForChange.summary.level", equalTo("INVALID"))
            .body("leadTimeForChange.details[0].value", equalTo("NaN"))
    }

    @Test
    fun `The deployment deployment done time is within the selected date range but the stage status is other should not be counted in`() {
        val requestBody = """
            {
                "endTime": 1598388400000,
                "pipelineStages": [
                    {
                        "pipelineId": "6012505c42fbb8439fc08e15",
                        "stage": "deploy to prod"
                    }
                ],
                "startTime": 1598288400000,
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
            .body("leadTimeForChange.summary.value", equalTo("NaN"))
            .body("leadTimeForChange.summary.level", equalTo("INVALID"))
            .body("leadTimeForChange.details[0].value", equalTo("NaN"))
    }

    @Test
    fun `No deployment happened during the selected time, MLT should be NaN`() {
        val requestBody = """
            {
                "endTime": 1598488400000,
                "pipelineStages": [
                    {
                        "pipelineId": "6012505c42fbb8439fc08e14",
                        "stage": "deploy to prod"
                    }
                ],
                "startTime": 1598388400000,
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
            .body("leadTimeForChange.summary.value", equalTo("NaN"))
            .body("leadTimeForChange.summary.level", equalTo("INVALID"))
            .body("leadTimeForChange.details[0].value", equalTo("NaN"))
    }

    @Test
    fun `The previous deployment failed, The next deployment should include the commits`() {
        val requestBody = """
            {
                "endTime": 1598292000300,
                "pipelineStages": [
                    {
                        "pipelineId": "6012505c42fbb8439fc08e13",
                        "stage": "deploy to prod"
                    }
                ],
                "startTime": 1598288400300,
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
            .body("leadTimeForChange.summary.value", equalTo(0.08F))
            .body("leadTimeForChange.summary.level", equalTo("ELITE"))
            .body("leadTimeForChange.details[0].value", equalTo(0.08F))
    }

    @Test
    fun `One deployment which contains several commits , the MLT should be accounted separately, and be averaged accordingly`() {
        val requestBody = """
            {
                "endTime": 1598292000300,
                "pipelineStages": [
                    {
                        "pipelineId": "6012505c42fbb8439fc08e12",
                        "stage": "deploy to prod"
                    }
                ],
                "startTime": 1598288400300,
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
            .body("leadTimeForChange.summary.value", equalTo(3.08F))
            .body("leadTimeForChange.summary.level", equalTo("HIGH"))
            .body("leadTimeForChange.details[0].value", equalTo(3.08F))
    }

    @Test
    fun `The deployment which the code commit time is within the selected date range but the deployment time not, the MLT is NaN`() {
        val requestBody = """
            {
                "endTime": 1598284800000,
                "pipelineStages": [
                    {
                        "pipelineId": "6012505c42fbb8439fc08e12",
                        "stage": "deploy to prod"
                    }
                ],
                "startTime": 1597852800000,
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
            .body("leadTimeForChange.summary.value", equalTo("NaN"))
            .body("leadTimeForChange.summary.level", equalTo("INVALID"))
            .body("leadTimeForChange.details[0].value", equalTo("NaN"))
    }

    @Test
    fun `The deployment which the deployment time is within the selected date range but the code commit time not, The MLT should be counted in `() {
        val requestBody = """
            {
                "endTime": 1598292000200,
                "pipelineStages": [
                    {
                        "pipelineId": "6012505c42fbb8439fc08e12",
                        "stage": "deploy to prod"
                    }
                ],
                "startTime": 1598288400000,
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
            .body("leadTimeForChange.summary.value", equalTo(3.08F))
            .body("leadTimeForChange.summary.level", equalTo("HIGH"))
            .body("leadTimeForChange.details[0].value", equalTo(3.08F))
    }

    @Test
    fun `The first Unit has the target stage, but the second Unit does not, The first value should be correct, The second value should be NaN`() {
        val requestBody = """
            {
                "endTime": 1599667200000,
                "pipelineStages": [
                    {
                        "pipelineId": "6012505c42fbb8439fc08e12",
                        "stage": "deploy to prod"
                    }
                ],
                "startTime": 1597852800000,
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
            .body("leadTimeForChange.summary.value", equalTo(3.08F))
            .body("leadTimeForChange.summary.level", equalTo("HIGH"))
            .body("leadTimeForChange.details[0].value", equalTo(3.08F))
            .body("leadTimeForChange.details[0].startTimestamp", equalTo(1597852800000L))
            .body("leadTimeForChange.details[0].endTimestamp", equalTo(1598543999999L))
            .body("leadTimeForChange.details[1].value", equalTo("NaN"))
            .body("leadTimeForChange.details[1].startTimestamp", equalTo(1598544000000L))
            .body("leadTimeForChange.details[1].endTimestamp", equalTo(1599667200000L))
    }
}
