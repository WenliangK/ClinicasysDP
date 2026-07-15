package Singleton;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Duration;

public final class ConexionAPI {
    private static final String URL_PREDETERMINADA = "http://100.112.174.23:8080/api";
    private static volatile ConexionAPI instancia;

    private final String baseUrl;
    private final HttpClient client;

    private ConexionAPI() {
        String configurada = System.getProperty("clinicasys.api.url");
        if (configurada == null || configurada.isBlank()) {
            configurada = System.getenv("CLINICASYS_API_URL");
        }
        this.baseUrl = normalizarBaseUrl(configurada);
        this.client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public static ConexionAPI getInstancia() {
        ConexionAPI actual = instancia;
        if (actual == null) {
            synchronized (ConexionAPI.class) {
                actual = instancia;
                if (actual == null) {
                    actual = new ConexionAPI();
                    instancia = actual;
                }
            }
        }
        return actual;
    }

    public static ConexionAPI getInstance() {
        return getInstancia();
    }

    public HttpClient getClient() {
        return client;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public HttpRequest.Builder requestBuilder(String endpoint) {
        String ruta = endpoint.startsWith("/") ? endpoint : "/" + endpoint;
        return HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + ruta))
                .header("Content-Type", "application/json; charset=UTF-8")
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(20));
    }

    private static String normalizarBaseUrl(String valor) {
        String url = valor == null || valor.isBlank() ? URL_PREDETERMINADA : valor.trim();
        while (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        if (!url.endsWith("/api")) {
            url += "/api";
        }
        URI uri = URI.create(url);
        if (!"http".equalsIgnoreCase(uri.getScheme()) && !"https".equalsIgnoreCase(uri.getScheme())) {
            throw new IllegalArgumentException("CLINICASYS_API_URL debe usar http o https");
        }
        return url;
    }
}
