package metrik

import io.restassured.RestAssured
import io.restassured.http.ContentType
import metrik.base.ApiTestBase
import metrik.fixtures.mttrBuilds
import metrik.fixtures.mttrPipeline1
import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class MTTRCalculationApiTest : ApiTestBase() {
    @BeforeAll
    fun setUpBuildDataFixture() {
        // init pipeline config
        mongoTemplate.save(mttrPipeline1)
        // init build data
        val buildCollectionName = "build"
        mttrBuilds.forEach {
            mongoTemplate.save(it, buildCollectionName)
        }
    }

    @Test
    fun `should calculate MTTR correctly for date range within which the 1st build is successful but the last build before the date range is failed`() {
        val requestBody = """
            {
                "endTime": 1578239999000,
                "pipelineStages": [
                    {
                        "pipelineId": "6018c32f42fbb8439fc08b24",
                        "stage": "Deploy to UAT"
                    }
                ],
                "startTime": 1578102000000,
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
            .body("meanTimeToRestore.summary.value", equalTo(0.74F))
            .body("meanTimeToRestore.summary.level", equalTo("ELITE"))
            .body("meanTimeToRestore.details[0].value", equalTo(0.74F))
    }

    @Test
    fun `should calculate MTTR correctly for date range within which the 1st build is successful but the last build & the 2nd last build before the date range are both failed`() {
        val requestBody = """
            {
                "endTime": 1578499199000,
                "pipelineStages": [
                    {
                        "pipelineId": "6018c32f42fbb8439fc08b24",
                        "stage": "Deploy to UAT"
                    }
                ],
                "startTime": 1578355200000,
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
            .body("meanTimeToRestore.summary.value", equalTo(21.5F))
            .body("meanTimeToRestore.summary.level", equalTo("HIGH"))
            .body("meanTimeToRestore.details[0].value", equalTo(21.5F))
    }

    @Test
    fun `should calculate MTTR correctly for date range within which the 1st build is successful but the last build before the date range is aborted`() {
        val requestBody = """
            {
                "endTime": 1578842603000,
                "pipelineStages": [
                    {
                        "pipelineId": "6018c32f42fbb8439fc08b24",
                        "stage": "Deploy to UAT"
                    }
                ],
                "startTime": 1578758400000,
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
            .body("meanTimeToRestore.summary.value", equalTo(69.59F))
            .body("meanTimeToRestore.summary.level", equalTo("MEDIUM"))
            .body("meanTimeToRestore.details[0].value", equalTo(69.59F))
    }

    @Test
    fun `should calculate MTTR correctly for date range within which the 1st build is Failed but the last build before the date range is also Failed`() {
        val requestBody = """
            {
                "endTime": 1579363199000,
                "pipelineStages": [
                    {
                        "pipelineId": "6018c32f42fbb8439fc08b24",
                        "stage": "Deploy to UAT"
                    }
                ],
                "startTime": 1579104000000,
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
            .body("meanTimeToRestore.summary.value", equalTo(48.0F))
            .body("meanTimeToRestore.summary.level", equalTo("MEDIUM"))
            .body("meanTimeToRestore.details[0].value", equalTo(48.0F))
    }

    @Test
    fun `should calculate MTTR correctly for date range within which there are several failure restored (Monthly view)`() {
        val requestBody = """
            {
                "endTime": 1581004799000,
                "pipelineStages": [
                    {
                        "pipelineId": "6018c32f42fbb8439fc08b24",
                        "stage": "Deploy to UAT"
                    }
                ],
                "startTime": 1577808000000,
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
            .body("meanTimeToRestore.summary.value", equalTo(71.55F))
            .body("meanTimeToRestore.summary.level", equalTo("MEDIUM"))
            .body("meanTimeToRestore.details[0].value", equalTo(31.47F))
            .body("meanTimeToRestore.details[1].value", equalTo(312.0F))
    }
}
