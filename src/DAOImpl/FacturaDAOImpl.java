package DAOImpl;

import DAO.FacturaDAO;
import Modelo.Factura;
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
import java.util.concurrent.CompletableFuture;

public class FacturaDAOImpl implements FacturaDAO {
    private static final String ENDPOINT = "/facturas";
    private static final Type LISTA_FACTURAS = new TypeToken<List<Factura>>() { }.getType();

    private final Gson gson = GsonFactory.getInstancia();
    private final ConexionAPI conexion = ConexionAPI.getInstancia();

    @Override
    public CompletableFuture<List<Factura>> listarTodos() {
        HttpRequest request = conexion.requestBuilder(ENDPOINT).GET().build();
        return conexion.getClient().sendAsync(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8))
                .thenApply(respuesta -> {
                    if (!RespuestaHttp.esExitosa(respuesta)) {
                        throw RespuestaHttp.error("No se pudieron listar las facturas", respuesta);
                    }
                    return gson.fromJson(respuesta.body(), LISTA_FACTURAS);
                });
    }

    @Override
    public CompletableFuture<Factura> guardar(Factura factura) {
        boolean esNueva = factura.getId() == null;
        String endpoint = esNueva ? ENDPOINT : ENDPOINT + "/" + factura.getId();
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(
                gson.toJson(factura), StandardCharsets.UTF_8);
        HttpRequest request = esNueva
                ? conexion.requestBuilder(endpoint).POST(body).build()
                : conexion.requestBuilder(endpoint).PUT(body).build();

        return conexion.getClient().sendAsync(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8))
                .thenApply(respuesta -> {
                    if (!RespuestaHttp.esExitosa(respuesta)) {
                        throw RespuestaHttp.error("No se pudo guardar la factura", respuesta);
                    }
                    return gson.fromJson(respuesta.body(), Factura.class);
                });
    }

    @Override
    public CompletableFuture<Void> eliminar(long id) {
        HttpRequest request = conexion.requestBuilder(ENDPOINT + "/" + id).DELETE().build();
        return conexion.getClient().sendAsync(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8))
                .thenApply(respuesta -> {
                    if (!RespuestaHttp.esExitosa(respuesta)) {
                        throw RespuestaHttp.error("No se pudo eliminar la factura", respuesta);
                    }
                    return null;
                });
    }
}
