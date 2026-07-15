package Singleton;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Duration;

/**
 * ANTES: esta clase definía ipServidor="100.112.174.23", pero cada DAOImpl
 * (PacienteDAOImpl, CitaDAOImpl, MedicoDAOImpl, FacturaDAOImpl) ignoraba por
 * completo este Singleton y hardcodeaba su propia baseUrl con OTRA IP
 * ("100.115.247.43", la misma que usa el datasource de PostgreSQL en el
 * servidor). Dos fuentes de verdad para la misma URL = bug esperando a pasar
 * en cuanto la IP de Tailscale cambie.
 *
 * AHORA: esta es la ÚNICA fuente de verdad. Todos los DAOImpl deben usar
 * requestBuilder(endpoint) en vez de construir su propio HttpRequest.Builder.
 * Ajusta ipServidor a la IP real de tu servidor en Tailscale.
 */
public class ConexionAPI {
    private static ConexionAPI instancia;

    // CORREGIDO: unificada con la IP que usa el servidor para conectarse a su
    // propia base de datos (application.properties) -> ajusta si tu IP de
    // Tailscale es distinta.
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

    /**
     * Builder base para cualquier endpoint (ej: "/pacientes", "/citas/5").
     * Ya incluye el Content-Type/Accept obligatorios y el timeout, así
     * ningún DAOImpl tiene que repetirlos ni puede olvidarlos.
     */
    public HttpRequest.Builder requestBuilder(String endpoint) {
        return HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + endpoint))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(15));
    }
}
}
