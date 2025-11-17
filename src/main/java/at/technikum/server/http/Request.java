package at.technikum.server.http;

import java.util.HashMap;
import java.util.Map;

public class Request {

    private Method method;
    private String path;
    private String body;
    private Map<String, String> headers;
    private Map<String, String> queryParams;

    public Request() {
        this.headers = new HashMap<>();
        this.queryParams = new HashMap<>();
    }

    public String getMethod() {
        return method.getVerb();
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getHeader(String name) {
        return headers.get(name);
    }

    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(Map<String, String> queryParams) {
        this.queryParams = queryParams;
    }

    public String getQueryParam(String name) {
        return queryParams.get(name);
    }

    public void addQueryParam(String name, String value) {
        queryParams.put(name, value);
    }
}