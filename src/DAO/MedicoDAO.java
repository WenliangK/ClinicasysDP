package DAO;
import Modelo.Medico;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface MedicoDAO {
    CompletableFuture<List<Medico>> listarTodos();
    CompletableFuture<Medico> guardar(Medico medico);
    CompletableFuture<Void> eliminar(int id);
}