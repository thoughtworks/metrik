package metrik

import io.restassured.RestAssured
import io.restassured.http.ContentType
import metrik.base.ApiTestBase
import metrik.project.domain.model.Build
import metrik.project.domain.model.Commit
import metrik.project.domain.model.Pipeline
import metrik.project.domain.model.PipelineType
import metrik.project.domain.model.Stage
import metrik.project.domain.model.Status
import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class DeploymentFrequencyCalculationApiTest : ApiTestBase() {
    @BeforeAll
    fun setUpBuildData() {
        // init pipeline config
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
        // init build data
        val collectionName = "build"
        mongoTemplate.save(dfBuild1, collectionName)
        mongoTemplate.save(dfBuild2, collectionName)
        mongoTemplate.save(dfBuild3, collectionName)
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

val dfBuild1 = Build(
    pipelineId = "601cbb3425c1392117aa053b",
    number = 1,
    duration = 40000,
    result = Status.SUCCESS,
    timestamp = 1609462800000,
    url = "http://localhost:8001/job/4km-backend/1/",
    stages = listOf(
        Stage(
            name = "build",
            status = Status.SUCCESS,
            startTimeMillis = 1609462800100,
            durationMillis = 30000,
            pauseDurationMillis = 0
        ),
        Stage(
            name = "Deploy to DEV",
            status = Status.SUCCESS,
            startTimeMillis = 1609462830200,
            durationMillis = 5000,
            pauseDurationMillis = 0
        ),
        Stage(
            name = "Deploy to UAT",
            status = Status.SUCCESS,
            startTimeMillis = 1609462835300,
            durationMillis = 5000,
            pauseDurationMillis = 2000
        )
    ),
    changeSets = listOf(
        Commit(
            commitId = "b9b775059d120a0dbf09fd40d2becea69a112400",
            msg = "commit on 31st",
            timestamp = 1609408800000,
            date = "2020-12-31 00:00:00 +0800"
        )
    )
)
val dfBuild2 = Build(
    pipelineId = "601cbb3425c1392117aa053b",
    number = 1,
    duration = 40000,
    result = Status.FAILED,
    timestamp = 1609488000000,
    url = "http://localhost:8001/job/4km-backend/1/",
    stages = listOf(
        Stage(
            name = "build",
            status = Status.SUCCESS,
            startTimeMillis = 1609488000000,
            durationMillis = 30000,
            pauseDurationMillis = 0
        ),
        Stage(
            name = "Deploy to DEV",
            status = Status.FAILED,
            startTimeMillis = 1609466700000,
            durationMillis = 5000,
            pauseDurationMillis = 0
        )
    ),
    changeSets = listOf(
        Commit(
            commitId = "b9b775059d120a0dbf09fd40d2becea69a112400",
            msg = "commit on 1st",
            timestamp = 1609466400000,
            date = "2021-01-01 10:00:00 +0800"
        )
    )
)
val dfBuild3 = Build(
    pipelineId = "601cbb3425c1392117aa053b",
    number = 1,
    duration = 40000,
    result = Status.FAILED,
    timestamp = 1609477200000,
    url = "http://localhost:8001/job/4km-backend/1/",
    stages = listOf(
        Stage(
            name = "build",
            status = Status.SUCCESS,
            startTimeMillis = 1609477200000,
            durationMillis = 30000,
            pauseDurationMillis = 0
        ),
        Stage(
            name = "Deploy to DEV",
            status = Status.OTHER,
            startTimeMillis = 1609477500000,
            durationMillis = 5000,
            pauseDurationMillis = 0
        )
    ),
    changeSets = listOf(
        Commit(
            commitId = "b9b775059d120a0dbf09fd40d2becea69a112400",
            msg = "commit on 1st",
            timestamp = 1609473600000,
            date = "2021-01-01 12:00:00 +0800"
        )
    )
)
