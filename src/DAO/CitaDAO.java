package DAO;

import Modelo.Cita;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface CitaDAO {
    CompletableFuture<List<Cita>> listarTodos();
    CompletableFuture<Cita> guardar(Cita cita);
    CompletableFuture<Void> eliminar(int id);
}