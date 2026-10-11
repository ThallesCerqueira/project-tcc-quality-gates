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
        assertThat(response.path("id"), notNullValue());

        assertThat(response.path("id"), notNullValue());
        assertThat(response.path("nome"), notNullValue());
        assertThat(response.path("descricao"), notNullValue());
        assertThat(response.path("preco"), notNullValue());
        assertThat(response.path("quantidade"), notNullValue());
        assertThat(response.path("categoria"), notNullValue());
        assertThat(response.path("criado_em"), notNullValue());
        assertThat(response.path("atualizado_em"), notNullValue());

        assertThat((Integer) response.path("id"), greaterThan(0));
        assertThat((Integer) response.path("quantidade"), greaterThanOrEqualTo(0));

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

        assertEquals(201, response.statusCode());

        Response getResponse = productPage.getByProductId(productId);

        assertEquals(200, getResponse.statusCode());

        assertThat(getResponse.path("id"), equalTo(response.path("id")));
        assertThat(getResponse.path("nome"), equalTo(response.path("nome")));
        assertThat(getResponse.path("descricao"), equalTo(response.path("descricao")));
        assertThat(getResponse.path("preco"), equalTo(response.path("preco")));
        assertThat(getResponse.path("quantidade"), equalTo(response.path("quantidade")));
        assertThat(getResponse.path("categoria"), equalTo(response.path("categoria")));
        assertThat(getResponse.path("criado_em"), equalTo(response.path("criado_em")));
        assertThat(getResponse.path("atualizado_em"), equalTo(response.path("atualizado_em")));

    }

    @Test
    public void createProductWithoutNameTest() {

        response = productPage.createProductWithoutName();
        assertEquals(400, response.statusCode());

    }

    @Test
    public void createProductWithoutPriceTest() {

        response = productPage.createProductWithoutPrice();
        assertEquals(400, response.statusCode());

    }

    @Test
    public void createProductWithNegativePriceTest() {

        response = productPage.createProductWithNegativePrice();
        assertEquals(400, response.statusCode());

    }

    @Test
    public void createProductWithNegativeQuantityTest() {

        response = productPage.createProductWithNegativeQuantity();
        assertEquals(400, response.statusCode());

    }

    @Test
    public void getProductByInexistentIdTest() {

        response = productPage.getByProductId(productId + 99);
        assertEquals(404, response.getStatusCode());

    }

    @Test
    public void updateInexistentProductTest() {

      response = productPage.putProduct(productId + 123);
      assertEquals(404, response.statusCode());

    }

    @Test
    public void putProductPersistenceTest() {

        Response putResponse = productPage.putProduct(productId);
        assertEquals(200, putResponse.statusCode());

        Response getResponse = productPage.getByProductId(productId);

        assertEquals(200, getResponse.statusCode());
        assertThat(
                getResponse.path("nome"),
                equalTo(putResponse.path("nome"))
        );
    }

    @Test
    public void updateProductWithNegativePriceTest() {

        response = productPage.putProductWithNegativePrice(productId);

        assertEquals(400, response.statusCode());
    }

    @Test
    public void updateProductWithNegativeQuantityTest() {

        response = productPage.putProductWithNegativeQuantity(productId);

        assertEquals(400, response.statusCode());
    }

    @Test
    public void updateProductWithoutNameTest() {

        response = productPage.putProductWithoutName(productId);

        assertEquals(400, response.statusCode());
    }

    @Test
    public void updateProductWithoutPriceTest() {

        response = productPage.putProductWithoutPrice(productId);

        assertEquals(400, response.statusCode());
    }

    @Test
    public void deleteInexistentProductTest() {

        response = productPage.deleteProductId(productId + 999);

        assertEquals(404, response.statusCode());
    }

    @Test
    public void deleteProductAndConfirmDeletionTest() {

        response = productPage.deleteProductId(productId);

        assertEquals(204, response.statusCode());

        shouldDelete = false;

        Response getResponse = productPage.getByProductId(productId);

        assertEquals(404, getResponse.statusCode());
    }

}
