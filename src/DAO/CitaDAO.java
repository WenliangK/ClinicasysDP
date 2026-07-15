package DAO;

import Modelo.Cita;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface CitaDAO {
    CompletableFuture<List<Cita>> listarTodos();
    CompletableFuture<Cita> guardar(Cita cita);
    CompletableFuture<Cita> cambiarEstado(long id, Cita.Estado estado);
    CompletableFuture<Void> eliminar(long id);
}
