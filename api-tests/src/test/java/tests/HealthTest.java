package tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import utils.BaseTest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class HealthTest extends BaseTest {

    @Test
    @DisplayName("TF-01 - Validar status do sistema")
    @Tag("critical")
    public void deveRetornarStatus200NoHealthCheck() {

        given()
                .when()
                .get("/health")
                .then()
                .statusCode(200)
                .body("status", equalTo("ok"));
    }
}