package utils;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class Unexpected5xxFilter implements Filter {

    @Override
    public Response filter(FilterableRequestSpecification request, FilterableResponseSpecification responseSpec,
            FilterContext context) {

        Response response = context.next(request, responseSpec);

        if (response.statusCode() >= 500 &&
                response.statusCode() <= 599) {

            registrar5xx(
                    request.getMethod(),
                    request.getURI(),
                    response.statusCode()
            );
        }

        return response;
    }

    private synchronized void registrar5xx(String method, String uri, int statusCode) {

        try {
            Path path = Path.of(
                    "target",
                    "unexpected-5xx.log"
            );

            Files.createDirectories(path.getParent());

            String log = method + " " + uri + " -> " + statusCode + System.lineSeparator();

            Files.writeString(path, log, StandardOpenOption.CREATE, StandardOpenOption.APPEND);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}