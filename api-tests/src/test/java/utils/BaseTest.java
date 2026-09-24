import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;

public abstract class BaseTest {

    @BeforeAll
    static void configurarApi() {
        RestAssured.baseURI = System.getenv()
                .getOrDefault("BASE_URL", "http://localhost:3002");
    }
}