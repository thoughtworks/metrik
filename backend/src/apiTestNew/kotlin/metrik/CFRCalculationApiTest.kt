package metrik

import io.restassured.RestAssured
import io.restassured.http.ContentType
import metrik.base.ApiTestBase
import metrik.fixtures.cfrBuild1
import metrik.fixtures.cfrBuild10
import metrik.fixtures.cfrBuild11
import metrik.fixtures.cfrBuild12
import metrik.fixtures.cfrBuild2
import metrik.fixtures.cfrBuild3
import metrik.fixtures.cfrBuild4
import metrik.fixtures.cfrBuild5
import metrik.fixtures.cfrBuild6
import metrik.fixtures.cfrBuild7
import metrik.fixtures.cfrBuild8
import metrik.fixtures.cfrBuild9
import metrik.fixtures.cfrPipeline
import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class CFRCalculationApiTest : ApiTestBase() {
    @BeforeAll
    fun setUpBuildDataFixture() {
        // init pipeline config
        mongoTemplate.save(cfrPipeline)
        // init build data
        val buildCollectionName = "build"
        mongoTemplate.save(cfrBuild1, buildCollectionName)
        mongoTemplate.save(cfrBuild2, buildCollectionName)
        mongoTemplate.save(cfrBuild3, buildCollectionName)
        mongoTemplate.save(cfrBuild4, buildCollectionName)
        mongoTemplate.save(cfrBuild5, buildCollectionName)
        mongoTemplate.save(cfrBuild6, buildCollectionName)
        mongoTemplate.save(cfrBuild7, buildCollectionName)
        mongoTemplate.save(cfrBuild8, buildCollectionName)
        mongoTemplate.save(cfrBuild9, buildCollectionName)
        mongoTemplate.save(cfrBuild10, buildCollectionName)
        mongoTemplate.save(cfrBuild11, buildCollectionName)
        mongoTemplate.save(cfrBuild12, buildCollectionName)
    }

    @Test
    fun `should calculate CFR correctly for build which the committed date is out of date range but the deployment date is within date range should be counted when do CFR calculation`() {
        val requestBody = """
            {
                "endTime": 1610639999000,
                "pipelineStages": [
                    {
                        "pipelineId": "6012505c42fbb8439fc08b21",
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
            .body("changeFailureRate.summary.value", equalTo(16.67F))
            .body("changeFailureRate.summary.level", equalTo("HIGH"))
            .body("changeFailureRate.details[0].value", equalTo(16.67F))
    }

    @Test
    fun `should calculate CFR correctly for build which the targeted stage is aborted should not be counted in when do CFR calculation`() {
        val requestBody = """
            {
                "endTime": 1610035199000,
                "pipelineStages": [
                    {
                        "pipelineId": "6012505c42fbb8439fc08b21",
                        "stage": "Deploy to UAT"
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
            .body("changeFailureRate.summary.value", equalTo(25.0F))
            .body("changeFailureRate.summary.level", equalTo("HIGH"))
            .body("changeFailureRate.details[0].value", equalTo(25.0F))
    }

    @Test
    fun `should calculate CFR correctly for build which the previous stage failed but the targeted stage is success should still be counted in when do CFR calculation`() {
        val requestBody = """
            {
                "endTime": 1610639999000,
                "pipelineStages": [
                    {
                        "pipelineId": "6012505c42fbb8439fc08b21",
                        "stage": "Deploy to UAT"
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
            .body("changeFailureRate.summary.value", equalTo(20.0F))
            .body("changeFailureRate.summary.level", equalTo("HIGH"))
            .body("changeFailureRate.details[0].value", equalTo(20.0F))
    }

    @Test
    fun `should calculate CFR correctly for continuously failed builds should be counted correctly when do CRF calculation`() {
        val requestBody = """
            {
                "endTime": 1611331199000,
                "pipelineStages": [
                    {
                        "pipelineId": "6012505c42fbb8439fc08b21",
                        "stage": "Deploy to UAT"
                    }
                ],
                "startTime": 1610640000000,
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
            .body("changeFailureRate.summary.value", equalTo(40.0F))
            .body("changeFailureRate.summary.level", equalTo("MEDIUM"))
            .body("changeFailureRate.details[0].value", equalTo(40.0F))
    }

    @Test
    fun `should calculate CFR correctly for build which the commit date is within user selected date range but deployment is out of the range should not be counted in when do CRF calculation`() {
        val requestBody = """
            {
                "endTime": 1611849599000,
                "pipelineStages": [
                    {
                        "pipelineId": "6012505c42fbb8439fc08b21",
                        "stage": "Deploy to UAT"
                    }
                ],
                "startTime": 1610640000000,
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
            .body("changeFailureRate.summary.value", equalTo(40.0F))
            .body("changeFailureRate.summary.level", equalTo("MEDIUM"))
            .body("changeFailureRate.details[0].value", equalTo(40.0F))
    }

    @Test
    fun `should calculate CFR correctly by fortnightly and the time split works well ( data range contains completed fortnight)`() {
        val requestBody = """
            {
                "endTime": 1611849599000,
                "pipelineStages": [
                    {
                        "pipelineId": "6012505c42fbb8439fc08b21",
                        "stage": "Deploy to UAT"
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
            .body("changeFailureRate.summary.value", equalTo(30.0F))
            .body("changeFailureRate.summary.level", equalTo("MEDIUM"))
            .body("changeFailureRate.details[0].value", equalTo(20.0F))
            .body("changeFailureRate.details[1].value", equalTo(40.0F))
    }

    @Test
    fun `should calculate CFR correctly by monthly and the time split works well ( not cross a calendar month)`() {
        val requestBody = """
            {
                "endTime": 1611849599000,
                "pipelineStages": [
                    {
                        "pipelineId": "6012505c42fbb8439fc08b21",
                        "stage": "Deploy to UAT"
                    }
                ],
                "startTime": 1609430400000,
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
            .body("changeFailureRate.summary.value", equalTo(30.0F))
            .body("changeFailureRate.summary.level", equalTo("MEDIUM"))
            .body("changeFailureRate.details[0].value", equalTo(30.0F))
    }

    @Test
    fun `should calculate CFR correctly by monthly and the time split works well ( cross a calendar month)`() {
        val requestBody = """
            {
                "endTime": 1611849599000,
                "pipelineStages": [
                    {
                        "pipelineId": "6012505c42fbb8439fc08b21",
                        "stage": "Deploy to UAT"
                    }
                ],
                "startTime": 1606665600000,
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
            .body("changeFailureRate.summary.value", equalTo(30.0F))
            .body("changeFailureRate.summary.level", equalTo("MEDIUM"))
            .body("changeFailureRate.details[0].value", equalTo("NaN"))
            .body("changeFailureRate.details[1].value", equalTo("NaN"))
            .body("changeFailureRate.details[2].value", equalTo(30.0F))
    }
}
