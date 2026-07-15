package Singleton;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Duration;

public class ConexionAPI {
    private static ConexionAPI instancia;
    private final String ipServidor = "100.112.174.23";
    private final String puerto = "8080";
    private final String baseUrl;
    private final HttpClient client;

    private ConexionAPI() {
        this.baseUrl = "http://" + ipServidor.trim() + ":" + puerto.trim() + "/api";
        this.client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public static synchronized ConexionAPI getInstance() {
        if (instancia == null) {
            instancia = new ConexionAPI();
        }
        return instancia;
    }

    public static ConexionAPI getInstancia() {
        if (instancia == null) {
            instancia = new ConexionAPI();
        }
        return instancia;
    }
    public HttpClient getClient() {
        return client;
    }

    public HttpRequest.Builder requestBuilder(String endpoint) {
        return HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + endpoint))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(15));
    }
}