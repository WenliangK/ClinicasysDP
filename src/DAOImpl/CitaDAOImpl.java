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
                    } else {
                        throw new RuntimeException("Error: " + resp.statusCode());
                    }
                });
    }

    @Override
    public CompletableFuture<Void> eliminar(int id) {
        return null;
    }
}