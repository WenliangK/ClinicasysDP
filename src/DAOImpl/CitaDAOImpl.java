package DAOImpl;

import DAO.CitaDAO;
import Modelo.Cita;
import Singleton.ConexionAPI;
import Utilidades.GsonFactory;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
package DAOImpl; // Ajusta este paquete según tu estructura real

import DAO.CitaDAO;
import Modelo.Cita;
import Singleton.ConexionAPI; // Tu clase Singleton
import com.google.gson.Gson;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CitaDAOImpl implements CitaDAO {

    private static final String ENDPOINT = "/citas";

    private final Gson gson = GsonFactory.getInstancia();
    private final ConexionAPI conexion = ConexionAPI.getInstancia();

    @Override
    public CompletableFuture<List<Cita>> listarTodos() {
        HttpRequest request = conexion.requestBuilder(ENDPOINT)
                .GET()
                .build();

        return conexion.getClient().sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(resp -> {
                    if (resp.statusCode() == 200) {
                        Type tipoLista = new TypeToken<List<Cita>>() {}.getType();
                        return (List<Cita>) gson.fromJson(resp.body(), tipoLista);
                    }
                    throw new RuntimeException("Error al listar citas: " + resp.statusCode());
                });
import java.time.Duration;

public class CitaDAOImpl implements CitaDAO {
    private final Gson gson = new Gson();
    // Obtener la instancia del Singleton correctamente
    private final ConexionAPI conexion = ConexionAPI.getInstancia();
    private final String baseUrl = "http://100.115.247.43:8080/api";

    @Override
    public CompletableFuture<List<Cita>> listarTodos() {
        return null;
    }

    @Override
    public CompletableFuture<Cita> guardar(Cita cita) {
        String json = gson.toJson(cita);

        HttpRequest request = conexion.requestBuilder(ENDPOINT)
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/citas"))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(15))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        return conexion.getClient().sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(resp -> {
                    if (resp.statusCode() == 200 || resp.statusCode() == 201) {
                        return gson.fromJson(resp.body(), Cita.class);
                    }
                    throw new RuntimeException("Error al guardar cita: " + resp.statusCode());
                    } else {
                        throw new RuntimeException("Error: " + resp.statusCode());
                    }
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
                    throw new RuntimeException("Error al eliminar cita: " + resp.statusCode());
                });
    }
}
        return null;
    }
}
