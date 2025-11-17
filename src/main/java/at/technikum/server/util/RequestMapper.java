package at.technikum.server.util;

import at.technikum.server.http.Method;
import at.technikum.server.http.Request;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class RequestMapper {

    public Request fromExchange(HttpExchange exchange) throws IOException {
        Request request = new Request();
        request.setMethod(Method.valueOf(exchange.getRequestMethod()));
        request.setPath(exchange.getRequestURI().getPath());

        InputStream is = exchange.getRequestBody();

        if (is == null) {
            return request;
        }

        byte[] buf = is.readAllBytes();
        request.setBody(new String(buf, StandardCharsets.UTF_8));

        return request;
    }
}
