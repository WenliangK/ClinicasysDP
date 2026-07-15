package DAOImpl;

import DAO.MedicoDAO;
import Modelo.Medico;
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

public class MedicoDAOImpl implements MedicoDAO {
    private static final String ENDPOINT = "/medicos";
    private static final Type LISTA_MEDICOS = new TypeToken<List<Medico>>() { }.getType();

    private final Gson gson = GsonFactory.getInstancia();
    private final ConexionAPI conexion = ConexionAPI.getInstancia();

    @Override
    public CompletableFuture<List<Medico>> listarTodos() {
        HttpRequest request = conexion.requestBuilder(ENDPOINT).GET().build();
        return conexion.getClient().sendAsync(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8))
                .thenApply(respuesta -> {
                    if (!RespuestaHttp.esExitosa(respuesta)) {
                        throw RespuestaHttp.error("No se pudieron listar los médicos", respuesta);
                    }
                    return gson.fromJson(respuesta.body(), LISTA_MEDICOS);
                });
    }

    @Override
    public CompletableFuture<Medico> guardar(Medico medico) {
        boolean esNuevo = medico.getId() == null;
        String endpoint = esNuevo ? ENDPOINT : ENDPOINT + "/" + medico.getId();
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(
                gson.toJson(medico), StandardCharsets.UTF_8);
        HttpRequest request = esNuevo
                ? conexion.requestBuilder(endpoint).POST(body).build()
                : conexion.requestBuilder(endpoint).PUT(body).build();

        return conexion.getClient().sendAsync(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8))
                .thenApply(respuesta -> {
                    if (!RespuestaHttp.esExitosa(respuesta)) {
                        throw RespuestaHttp.error("No se pudo guardar el médico", respuesta);
                    }
                    return gson.fromJson(respuesta.body(), Medico.class);
                });
    }

    @Override
    public CompletableFuture<Medico> cambiarActivo(long id, boolean activo) {
        String json = gson.toJson(Map.of("activo", activo));
        HttpRequest request = conexion.requestBuilder(ENDPOINT + "/" + id + "/activo")
                .method("PATCH", HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();
        return conexion.getClient().sendAsync(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8))
                .thenApply(respuesta -> {
                    if (!RespuestaHttp.esExitosa(respuesta)) {
                        throw RespuestaHttp.error("No se pudo cambiar el estado del médico", respuesta);
                    }
                    return gson.fromJson(respuesta.body(), Medico.class);
                });
    }

    @Override
    public CompletableFuture<Void> eliminar(long id) {
        HttpRequest request = conexion.requestBuilder(ENDPOINT + "/" + id).DELETE().build();
        return conexion.getClient().sendAsync(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8))
                .thenApply(respuesta -> {
                    if (!RespuestaHttp.esExitosa(respuesta)) {
                        throw RespuestaHttp.error("No se pudo desactivar el médico", respuesta);
                    }
                    return null;
                });
    }
}
