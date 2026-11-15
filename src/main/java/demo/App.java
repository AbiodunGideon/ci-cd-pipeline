package demo;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/** Tiny HTTP app using only the JDK, so there is nothing to install to run it. */
public class App {

    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
        HttpServer server = create(port);
        server.start();
        System.out.println("Listening on http://localhost:" + server.getAddress().getPort());
    }

    static HttpServer create(int port) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/health", ex -> respond(ex, 200, "text/plain", "OK"));
        server.createContext("/hello", ex -> {
            String name = queryParam(ex, "name", "world");
            // Intentionally unsafe: user input goes straight into HTML (see InsecureExamples).
            respond(ex, 200, "text/html", InsecureExamples.greetingHtml(name));
        });
        return server;
    }

    private static String queryParam(HttpExchange ex, String key, String fallback) {
        String q = ex.getRequestURI().getRawQuery();
        if (q == null) return fallback;
        for (String pair : q.split("&")) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2 && kv[0].equals(key)) {
                return URLDecoder.decode(kv[1], StandardCharsets.UTF_8);
            }
        }
        return fallback;
    }

    private static void respond(HttpExchange ex, int status, String type, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", type + "; charset=utf-8");
        ex.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(bytes);
        }
    }
}
