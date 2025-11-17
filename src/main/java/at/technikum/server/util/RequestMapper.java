package at.technikum.server.util;

import at.technikum.server.http.Method;
import at.technikum.server.http.Request;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestMapper {

    public Request fromExchange(HttpExchange exchange) throws IOException {
        Request request = new Request();

        // Set HTTP method
        request.setMethod(Method.valueOf(exchange.getRequestMethod()));

        // Parse URI for path and query parameters
        URI uri = exchange.getRequestURI();
        String path = uri.getPath();
        request.setPath(path);

        // Parse query parameters
        String query = uri.getQuery();
        if (query != null && !query.isEmpty()) {
            Map<String, String> queryParams = parseQueryString(query);
            request.setQueryParams(queryParams);
        }

        // Extract headers
        Map<String, String> headers = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : exchange.getRequestHeaders().entrySet()) {
            if (!entry.getValue().isEmpty()) {
                headers.put(entry.getKey(), entry.getValue().get(0));
            }
        }
        request.setHeaders(headers);

        // Read body
        InputStream is = exchange.getRequestBody();
        if (is != null) {
            byte[] buf = is.readAllBytes();
            if (buf.length > 0) {
                request.setBody(new String(buf, StandardCharsets.UTF_8));
            }
        }

        return request;
    }

    private Map<String, String> parseQueryString(String query) {
        Map<String, String> params = new HashMap<>();

        if (query == null || query.isEmpty()) {
            return params;
        }

        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                String key = urlDecode(keyValue[0]);
                String value = urlDecode(keyValue[1]);
                params.put(key, value);
            } else if (keyValue.length == 1) {
                params.put(urlDecode(keyValue[0]), "");
            }
        }

        return params;
    }

    private String urlDecode(String value) {
        try {
            return java.net.URLDecoder.decode(value, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return value;
        }
    }
}