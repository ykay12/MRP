package at.technikum.server.util;

import at.technikum.application.todo.model.Todo;
import at.technikum.application.todo.repository.TodoRepository;
import at.technikum.server.http.Request;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpsExchange;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestMapperTest {

    private RequestMapper requestMapper = new RequestMapper();

    @Test
    public void givenGetExchange_whenMapping_thenGetInRequest() throws IOException {
        HttpExchange exchange = mock(HttpsExchange.class);
        when(exchange.getRequestMethod()).thenReturn("GET");
        when(exchange.getRequestURI()).thenReturn(URI.create("/todos"));

        Request request = requestMapper.fromExchange(exchange);

        assertEquals("GET", request.getMethod());
        verify(exchange, times(1)).getRequestMethod();
    }

    @Test
    public void givenPostExchange_whenMapping_thePostInRequest() throws IOException {
        HttpExchange exchange = mock(HttpsExchange.class);
        when(exchange.getRequestMethod()).thenReturn("POST");
        when(exchange.getRequestURI()).thenReturn(URI.create("/todos"));
        InputStream inputStream = new ByteArrayInputStream("{\"name\": \"todo\"}".getBytes());
        when(exchange.getRequestBody()).thenReturn(inputStream);

        Request request = requestMapper.fromExchange(exchange);

        assertEquals("POST", request.getMethod());
    }

    @Test
    public void givenPathExchange_whenMapping_thenPathInRequest() throws IOException {
        HttpExchange exchange = mock(HttpsExchange.class);
        when(exchange.getRequestMethod()).thenReturn("GET");
        when(exchange.getRequestURI()).thenReturn(URI.create("/todos"));

        Request request = requestMapper.fromExchange(exchange);

        assertEquals("/todos", request.getPath());
    }

    @Test
    public void givenNoBodyExchange_whenMapping_theNoBodyInRequest() throws IOException {
        HttpExchange exchange = mock(HttpsExchange.class);
        when(exchange.getRequestMethod()).thenReturn("GET");
        when(exchange.getRequestURI()).thenReturn(URI.create("/todos"));

        Request request = requestMapper.fromExchange(exchange);

        assertNull(request.getBody());
    }

    @Test
    public void givenBodyExchange_whenMapping_theBodyInRequest() throws IOException {
        HttpExchange exchange = mock(HttpsExchange.class);
        when(exchange.getRequestMethod()).thenReturn("POST");
        when(exchange.getRequestURI()).thenReturn(URI.create("/todos"));
        InputStream inputStream = new ByteArrayInputStream("{\"name\": \"todo\"}".getBytes());
        when(exchange.getRequestBody()).thenReturn(inputStream);

        Request request = requestMapper.fromExchange(exchange);

        assertEquals("{\"name\": \"todo\"}", request.getBody());
    }

}
