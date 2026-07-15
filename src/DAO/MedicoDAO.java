package DAO;

import Modelo.Medico;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface MedicoDAO {
    CompletableFuture<List<Medico>> listarTodos();
    CompletableFuture<Medico> guardar(Medico medico);
    CompletableFuture<Medico> cambiarActivo(long id, boolean activo);
    CompletableFuture<Void> eliminar(long id);
}
