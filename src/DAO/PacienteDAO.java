package DAO;

import Modelo.Paciente;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface PacienteDAO {
    CompletableFuture<List<Paciente>> listarTodos();
    CompletableFuture<Paciente> guardar(Paciente paciente);
    CompletableFuture<Paciente> cambiarActivo(long id, boolean activo);
    CompletableFuture<Void> eliminar(long id);
}
