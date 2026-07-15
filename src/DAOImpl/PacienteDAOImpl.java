package DAOImpl;

import DAO.PacienteDAO;
import Modelo.Paciente;
import Singleton.ConexionAPI;
import Utilidades.GsonFactory;
import Utilidades.RespuestaHttp;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class PacienteDAOImpl implements PacienteDAO {
    private static final String ENDPOINT = "/pacientes";
    private static final Type LISTA_PACIENTES = new TypeToken<List<Paciente>>() { }.getType();

    private final Gson gson = GsonFactory.getInstancia();
    private final ConexionAPI conexion = ConexionAPI.getInstancia();

    @Override
    public CompletableFuture<List<Paciente>> listarTodos() {
        HttpRequest request = conexion.requestBuilder(ENDPOINT).GET().build();
        return conexion.getClient().sendAsync(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8))
                .thenApply(respuesta -> {
                    if (!RespuestaHttp.esExitosa(respuesta)) {
                        throw RespuestaHttp.error("No se pudieron listar los pacientes", respuesta);
                    }
                    return gson.fromJson(respuesta.body(), LISTA_PACIENTES);
                });
    }

    @Override
    public CompletableFuture<Paciente> guardar(Paciente paciente) {
        boolean esNuevo = paciente.getId() == null;
        String endpoint = esNuevo ? ENDPOINT : ENDPOINT + "/" + paciente.getId();
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(
                gson.toJson(paciente), StandardCharsets.UTF_8);
        HttpRequest request = esNuevo
                ? conexion.requestBuilder(endpoint).POST(body).build()
                : conexion.requestBuilder(endpoint).PUT(body).build();

        return conexion.getClient().sendAsync(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8))
                .thenApply(respuesta -> {
                    if (!RespuestaHttp.esExitosa(respuesta)) {
                        throw RespuestaHttp.error("No se pudo guardar el paciente", respuesta);
                    }
                    return gson.fromJson(respuesta.body(), Paciente.class);
                });
    }

    @Override
    public CompletableFuture<Paciente> cambiarActivo(long id, boolean activo) {
        String json = gson.toJson(Map.of("activo", activo));
        HttpRequest request = conexion.requestBuilder(ENDPOINT + "/" + id + "/activo")
                .method("PATCH", HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();
        return conexion.getClient().sendAsync(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8))
                .thenApply(respuesta -> {
                    if (!RespuestaHttp.esExitosa(respuesta)) {
                        throw RespuestaHttp.error("No se pudo cambiar el estado del paciente", respuesta);
                    }
                    return gson.fromJson(respuesta.body(), Paciente.class);
                });
    }

    @Override
    public CompletableFuture<Void> eliminar(long id) {
        HttpRequest request = conexion.requestBuilder(ENDPOINT + "/" + id).DELETE().build();
        return conexion.getClient().sendAsync(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8))
                .thenApply(respuesta -> {
                    if (!RespuestaHttp.esExitosa(respuesta)) {
                        throw RespuestaHttp.error("No se pudo desactivar el paciente", respuesta);
                    }
                    return null;
                });
    }
}
