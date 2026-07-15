package DAOImpl;

import DAO.FacturaDAO;
import Modelo.Factura;
import Singleton.ConexionAPI;
import Utilidades.GsonFactory;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import com.google.gson.Gson;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class FacturaDAOImpl implements FacturaDAO {

    private static final String ENDPOINT = "/facturas";

    private final Gson gson = GsonFactory.getInstancia();
    private final ConexionAPI conexion = ConexionAPI.getInstancia();

    @Override
    public CompletableFuture<List<Factura>> listarTodos() {
        HttpRequest request = conexion.requestBuilder(ENDPOINT)
                .GET()
                .build();

        return conexion.getClient().sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(resp -> {
                    if (resp.statusCode() == 200) {
                        Type tipoLista = new TypeToken<List<Factura>>() {}.getType();
                        return (List<Factura>) gson.fromJson(resp.body(), tipoLista);
                    }
                    throw new RuntimeException("Error al listar facturas: " + resp.statusCode());
                });
import java.time.Duration;

public class FacturaDAOImpl implements FacturaDAO {
    private final Gson gson = new Gson();
    private final ConexionAPI conexion = ConexionAPI.getInstancia();
    private final String baseUrl = "http://100.115.247.43:8080/api";

    @Override
    public CompletableFuture<List<Factura>> listarTodos() {
        return null;
    }

    @Override
    public CompletableFuture<Factura> guardar(Factura factura) {
        String json = gson.toJson(factura);
        HttpRequest request = conexion.requestBuilder(ENDPOINT)
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/facturas"))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(15))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        return conexion.getClient().sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(resp -> {
                    if (resp.statusCode() == 200 || resp.statusCode() == 201) {
                        return gson.fromJson(resp.body(), Factura.class);
                    }
                    throw new RuntimeException("Error al guardar factura: " + resp.statusCode());
                });
    }

    @Override
    public CompletableFuture<Void> eliminar(int id) {
        HttpRequest request = conexion.requestBuilder(ENDPOINT + "/" + id)
                .DELETE()
                .build();

        return conexion.getClient().sendAsync(request, HttpResponse.BodyHandlers.discarding())
                .thenApply(resp -> {
                    if (resp.statusCode() == 200 || resp.statusCode() == 204) {
                        return null;
                    }
                    throw new RuntimeException("Error al eliminar factura: " + resp.statusCode());
                });
    }
}
        return null;
    }
}
