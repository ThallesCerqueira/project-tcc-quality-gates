package tests;

import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pages.ProductPage;
import utils.BaseTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProductTest extends BaseTest {

    private final ProductPage productPage = new ProductPage();
    private Response response;
    private int productId;
    private boolean shouldDelete;

    @BeforeEach
    public void setup() {

        shouldDelete = true;
        response = productPage.postProduct();
        productId = response.path("id");

    }

    @AfterEach
    public void tearDown() {

        if(shouldDelete) {
            productPage.deleteProductId(productId);

        }

    }

    @Test
    public void postProductTest() {

        assertEquals(201, response.statusCode());

    }

    @Test
    public void putProductTest() {

        response = productPage.putProduct(productId);
        assertEquals(200, response.statusCode());

    }

    @Test
    public void deleteProductIdTest() {

        shouldDelete = false;
        response = productPage.deleteProductId(productId);
        assertEquals(204, response.statusCode());

    }

    @Test
    public void getAllProductsTest() {

        response = productPage.getAllProducts();
        assertEquals(200, response.statusCode());
        assertThat(response.jsonPath().getList("$"), is(not(empty())));

    }

    @Test
    public void getByProductIdTest() {

        response = productPage.getByProductId(productId);
        assertEquals(200, response.statusCode());

    }

}
