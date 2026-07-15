package DAOImpl;

import DAO.CitaDAO;
import Modelo.Cita;
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

public class CitaDAOImpl implements CitaDAO {
    private static final String ENDPOINT = "/citas";
    private static final Type LISTA_CITAS = new TypeToken<List<Cita>>() { }.getType();

    private final Gson gson = GsonFactory.getInstancia();
    private final ConexionAPI conexion = ConexionAPI.getInstancia();

    @Override
    public CompletableFuture<List<Cita>> listarTodos() {
        HttpRequest request = conexion.requestBuilder(ENDPOINT).GET().build();
        return conexion.getClient().sendAsync(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8))
                .thenApply(respuesta -> {
                    if (!RespuestaHttp.esExitosa(respuesta)) {
                        throw RespuestaHttp.error("No se pudieron listar las citas", respuesta);
                    }
                    return gson.fromJson(respuesta.body(), LISTA_CITAS);
                });
    }

    @Override
    public CompletableFuture<Cita> guardar(Cita cita) {
        boolean esNueva = cita.getId() == null;
        String endpoint = esNueva ? ENDPOINT : ENDPOINT + "/" + cita.getId();
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(
                gson.toJson(cita), StandardCharsets.UTF_8);
        HttpRequest request = esNueva
                ? conexion.requestBuilder(endpoint).POST(body).build()
                : conexion.requestBuilder(endpoint).PUT(body).build();

        return conexion.getClient().sendAsync(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8))
                .thenApply(respuesta -> {
                    if (!RespuestaHttp.esExitosa(respuesta)) {
                        throw RespuestaHttp.error("No se pudo guardar la cita", respuesta);
                    }
                    return gson.fromJson(respuesta.body(), Cita.class);
                });
    }

    @Override
    public CompletableFuture<Cita> cambiarEstado(long id, Cita.Estado estado) {
        String json = gson.toJson(Map.of("estado", estado.name()));
        HttpRequest request = conexion.requestBuilder(ENDPOINT + "/" + id + "/estado")
                .method("PATCH", HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();
        return conexion.getClient().sendAsync(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8))
                .thenApply(respuesta -> {
                    if (!RespuestaHttp.esExitosa(respuesta)) {
                        throw RespuestaHttp.error("No se pudo cambiar el estado de la cita", respuesta);
                    }
                    return gson.fromJson(respuesta.body(), Cita.class);
                });
    }

    @Override
    public CompletableFuture<Void> eliminar(long id) {
        HttpRequest request = conexion.requestBuilder(ENDPOINT + "/" + id).DELETE().build();
        return conexion.getClient().sendAsync(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8))
                .thenApply(respuesta -> {
                    if (!RespuestaHttp.esExitosa(respuesta)) {
                        throw RespuestaHttp.error("No se pudo eliminar la cita", respuesta);
                    }
                    return null;
                });
    }
}
