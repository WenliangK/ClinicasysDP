package DAO;

import Modelo.Paciente;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface PacienteDAO {
    CompletableFuture<List<Paciente>> listarTodos();
    CompletableFuture<Paciente> guardar(Paciente paciente);
    CompletableFuture<Void> eliminar(int id);
}