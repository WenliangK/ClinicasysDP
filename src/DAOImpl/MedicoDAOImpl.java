package DAOImpl;

import DAO.MedicoDAO;
import Modelo.Medico;
import Singleton.ConexionAPI;
import com.google.gson.Gson;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.time.Duration;

public class MedicoDAOImpl implements MedicoDAO {
    private final Gson gson = new Gson();
    private final ConexionAPI conexion = ConexionAPI.getInstancia();
    private final String baseUrl = "http://100.115.247.43:8080/api";

    @Override
    public CompletableFuture<List<Medico>> listarTodos() {
        return null;
    }

    @Override
    public CompletableFuture<Medico> guardar(Medico medico) {
        String json = gson.toJson(medico);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/medicos"))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(15))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        return conexion.getClient().sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(resp -> {
                    if (resp.statusCode() == 200 || resp.statusCode() == 201) {
                        return gson.fromJson(resp.body(), Medico.class);
                    }
                    throw new RuntimeException("Error al guardar médico: " + resp.statusCode());
                });
    }

    @Override
    public CompletableFuture<Void> eliminar(int id) {
        return null;
    }
}