package demo;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AppTest {

    private String get(HttpServer server, String path) throws Exception {
        int port = server.getAddress().getPort();
        HttpRequest req = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path)).build();
        HttpResponse<String> res = HttpClient.newHttpClient().send(req, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, res.statusCode());
        return res.body();
    }

    @Test
    void healthEndpointReturnsOk() throws Exception {
        HttpServer server = App.create(0);
        server.start();
        try {
            assertEquals("OK", get(server, "/health"));
        } finally {
            server.stop(0);
        }
    }

    @Test
    void helloEndpointGreetsByName() throws Exception {
        HttpServer server = App.create(0);
        server.start();
        try {
            assertTrue(get(server, "/hello?name=Sam").contains("Hello, Sam!"));
        } finally {
            server.stop(0);
        }
    }
}
