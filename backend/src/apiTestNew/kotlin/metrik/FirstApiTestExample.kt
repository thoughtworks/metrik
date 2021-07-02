package metrik

import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.Test

internal class FirstApiTestExample : ApiTestBase() {
    @Test
    fun `good first sample test case`() {
        RestAssured
            .given()
            .contentType(ContentType.JSON)
            .get("/api/project")
            .then()
            .statusCode(200)
            .body("size()", `is`(1))
            .body("[0].id", `is`("project id"))
            .body("[0].name", `is`("project name"))
            .body("[0].synchronizationTimestamp", `is`(98708))
    }
}
