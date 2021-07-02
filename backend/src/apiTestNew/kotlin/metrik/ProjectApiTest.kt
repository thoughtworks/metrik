package metrik

import io.restassured.RestAssured
import io.restassured.http.ContentType
import metrik.base.ApiTestBase
import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.Test

internal class ProjectApiTest : ApiTestBase() {
    @Test
    fun `should get project list returned`() {
        RestAssured
            .given()
            .contentType(ContentType.JSON)
            .get("/api/project/")
            .then()
            .statusCode(200)
            .body("size()", `is`(1))
            .body("[0].id", `is`("601cbae825c1392117aa0429"))
            .body("[0].name", `is`("4-key"))
            .body("[0].synchronizationTimestamp", `is`(1580709600000))
    }

    @Test
    fun `should get project via project ID`() {
        RestAssured
            .given()
            .contentType(ContentType.JSON)
            .get("/api/project/601cbae825c1392117aa0429")
            .then()
            .statusCode(200)
            .body("id", `is`("601cbae825c1392117aa0429"))
            .body("name", `is`("4-key"))
            .body("synchronizationTimestamp", `is`(1580709600000))
    }

    @Test
    fun `should get 404 error returned given an invalid project ID`() {
        RestAssured
            .given()
            .contentType(ContentType.JSON)
            .get("/api/project/some-invalid-id")
            .then()
            .statusCode(404)
    }
}
