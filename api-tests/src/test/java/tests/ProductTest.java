package tests;

import io.restassured.response.Response;
import org.junit.jupiter.api.*;
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
    @DisplayName("TF-01 - Cadastrar produto com dados válidos")
    @Tag("critical")
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

        assertThat(response.path("id"), greaterThan(0));
        assertThat(response.path("quantidade"), greaterThanOrEqualTo(0));

    }

    @Test
    @DisplayName("TF-02 - Atualizar produto com dados válidos")
    @Tag("critical")
    public void putProductTest() {

        response = productPage.putProduct(productId);
        assertEquals(200, response.statusCode());

    }

    @Test
    @DisplayName("TF-03 - Excluir produto e confirmar exclusão")
    @Tag("critical")
    public void deleteProductAndConfirmDeletionTest() {

        shouldDelete = false;

        response = productPage.deleteProductId(productId);
        assertEquals(204, response.statusCode());

        Response getResponse = productPage.getByProductId(productId);

        assertEquals(404, getResponse.statusCode());
    }

    @Test
    @DisplayName("TF-04 - Listar todos os produtos")
    @Tag("critical")
    public void getAllProductsTest() {

        response = productPage.getAllProducts();
        assertEquals(200, response.statusCode());
        assertThat(response.jsonPath().getList("$"), is(not(empty())));

    }

    @Test
    @DisplayName("TF-05 - Listar produto específico")
    @Tag("critical")
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
    @DisplayName("TF-06 - Cadastrar produto sem nome")
    public void createProductWithoutNameTest() {

        response = productPage.createProductWithoutName();
        assertEquals(400, response.statusCode());

    }

    @Test
    @DisplayName("TF-07 - Cadastrar produto sem preço")
    public void createProductWithoutPriceTest() {

        response = productPage.createProductWithoutPrice();
        assertEquals(400, response.statusCode());

    }

    @Test
    @DisplayName("TF-08 - Cadastrar produto com preço negativo")
    public void createProductWithNegativePriceTest() {

        response = productPage.createProductWithNegativePrice();
        assertEquals(400, response.statusCode());

    }

    @Test
    @DisplayName("TF-09 - Cadastrar produto com quantidade negativa")
    public void createProductWithNegativeQuantityTest() {

        response = productPage.createProductWithNegativeQuantity();
        assertEquals(400, response.statusCode());

    }

    @Test
    @DisplayName("TF-10 - Listar produto inexistente")
    public void getProductByInexistentIdTest() {

        response = productPage.getByProductId(productId + 99);
        assertEquals(404, response.getStatusCode());

    }

    @Test
    @DisplayName("TF-11 - Atualizar produto inexistente")
    public void updateInexistentProductTest() {

      response = productPage.putProduct(productId + 123);
      assertEquals(404, response.statusCode());

    }

    @Test
    @DisplayName("TF-12 - Atualizar produto e validar persistência dos dados")
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
    @DisplayName("TF-13 - Atualizar produto com preço negativo")
    public void updateProductWithNegativePriceTest() {

        response = productPage.putProductWithNegativePrice(productId);

        assertEquals(400, response.statusCode());
    }

    @Test
    @DisplayName("TF-14 - Atualizar produto com quantidade negativa")
    public void updateProductWithNegativeQuantityTest() {

        response = productPage.putProductWithNegativeQuantity(productId);

        assertEquals(400, response.statusCode());
    }

    @Test
    @DisplayName("TF-15 - Atualizar produto sem nome")
    public void updateProductWithoutNameTest() {

        response = productPage.putProductWithoutName(productId);

        assertEquals(400, response.statusCode());
    }

    @Test
    @DisplayName("TF-16 - Atualizar produto sem preço")
    public void updateProductWithoutPriceTest() {

        response = productPage.putProductWithoutPrice(productId);

        assertEquals(400, response.statusCode());
    }

    @Test
    @DisplayName("TF-17 - Excluir produto inexistente")
    public void deleteInexistentProductTest() {

        response = productPage.deleteProductId(productId + 999);

        assertEquals(404, response.statusCode());
    }

}
