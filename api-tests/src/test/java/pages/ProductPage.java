package pages;

import com.github.javafaker.Faker;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import static io.restassured.RestAssured.given;

public class ProductPage {

    private final Faker faker = new Faker(new Locale("pt-Br"));
    private static final String ENDPOINT = "/api/produtos";
    private Map<String, Object> payloadProduct;

    private final String nomeProduto = faker.commerce().productName();
    private final String descricaoProduto = faker.lorem().sentence();
    private final float precoProduto = (float) faker.number().randomDouble(2, 10, 5000);
    private final int quantidadeProduto = faker.number().numberBetween(1, 100);
    private final String categoriaProduto = faker.lorem().word();

    public Response postProduct() {

        return
                given()
                        .contentType(ContentType.JSON)
                        .body(setParamsProduct())
                        .when()
                        .post(ENDPOINT)
                        .then()
                        .extract().response()
                ;

    }

    public Response putProduct(int productId) {

        payloadProduct.put("nome", nomeProduto + " Editado");

        return
                given()
                        .contentType(ContentType.JSON)
                        .body(payloadProduct)
                        .when()
                        .put(ENDPOINT + "/" + productId)
                        .then()
                        .extract().response()
                ;

    }

    public Response deleteProductId(int productId) {

        return
                given()
                        .when()
                        .delete(ENDPOINT + "/" + productId)
                        .then()
                        .extract().response()
                ;

    }

    public Response getAllProducts() {

        return given()
                .when()
                .get(ENDPOINT)
                .then()
                .extract().response();
    }

    public Response getByProductId(int productId) {
        return given()
                .when()
                .get(ENDPOINT + "/" + productId)
                .then()
                .extract()
                .response();
    }

    private Map<String, Object> setParamsProduct() {

        payloadProduct = new HashMap<>();

        payloadProduct.put("nome", nomeProduto);
        payloadProduct.put("descricao", descricaoProduto);
        payloadProduct.put("preco", precoProduto);
        payloadProduct.put("quantidade", quantidadeProduto);
        payloadProduct.put("categoria", categoriaProduto);

        return payloadProduct;

    }

}