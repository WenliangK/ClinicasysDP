package DAOImpl;

import DAO.PacienteDAO;
import Modelo.Paciente;
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

public class PacienteDAOImpl implements PacienteDAO {

    private static final String ENDPOINT = "/pacientes";

    private final Gson gson = GsonFactory.getInstancia();
    private final ConexionAPI conexion = ConexionAPI.getInstancia();

    @Override
    public CompletableFuture<List<Paciente>> listarTodos() {
        HttpRequest request = conexion.requestBuilder(ENDPOINT)
                .GET()
                .build();

        return conexion.getClient().sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(resp -> {
                    if (resp.statusCode() == 200) {
                        // CORREGIDO: el servidor devuelve un JSON Array [ ].
                        // Sin TypeToken, Gson intentaría mapearlo a un solo
                        // Paciente (Object) y explotaría. TypeToken<List<T>>
                        // es obligatorio para listas.
                        Type tipoLista = new TypeToken<List<Paciente>>() {}.getType();
                        return (List<Paciente>) gson.fromJson(resp.body(), tipoLista);
                    }
                    throw new RuntimeException("Error al listar pacientes: " + resp.statusCode());
                });
import java.time.Duration;

public class PacienteDAOImpl implements PacienteDAO {
    private final Gson gson = new Gson();
    private final ConexionAPI conexion = ConexionAPI.getInstancia();
    private final String baseUrl = "http://100.115.247.43:8080/api";

    @Override
    public CompletableFuture<List<Paciente>> listarTodos() {
        return null;
    }

    @Override
    public CompletableFuture<Paciente> guardar(Paciente paciente) {
        String json = gson.toJson(paciente);
        HttpRequest request = conexion.requestBuilder(ENDPOINT)
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/pacientes"))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(15))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        return conexion.getClient().sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(resp -> {
                    if (resp.statusCode() == 200 || resp.statusCode() == 201) {
                        return gson.fromJson(resp.body(), Paciente.class);
                    }
                    throw new RuntimeException("Error al guardar paciente: " + resp.statusCode());
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
                    throw new RuntimeException("Error al eliminar paciente: " + resp.statusCode());
                });
    }
}
        return null;
    }
}
