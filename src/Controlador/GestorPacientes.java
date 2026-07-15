package Controlador;

import DAO.PacienteDAO;
import DAOImpl.PacienteDAOImpl;
import Modelo.Paciente;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class GestorPacientes {
    private static volatile GestorPacientes instancia;
    private final PacienteDAO pacienteDAO;

    private GestorPacientes() {
        this(new PacienteDAOImpl());
    }

    GestorPacientes(PacienteDAO pacienteDAO) {
        this.pacienteDAO = pacienteDAO;
    }

    public static GestorPacientes getInstancia() {
        GestorPacientes actual = instancia;
        if (actual == null) {
            synchronized (GestorPacientes.class) {
                actual = instancia;
                if (actual == null) {
                    actual = new GestorPacientes();
                    instancia = actual;
                }
            }
        }
        return actual;
    }

    public CompletableFuture<List<Paciente>> getTodos() {
        return pacienteDAO.listarTodos()
                .thenApply(lista -> lista.stream().filter(Paciente::isActivo).toList());
    }

    public CompletableFuture<List<Paciente>> listarPacientes() {
        return pacienteDAO.listarTodos();
    }

    public CompletableFuture<Paciente> guardar(Paciente paciente) {
        return pacienteDAO.guardar(paciente);
    }

    public CompletableFuture<Paciente> cambiarActivo(long id, boolean activo) {
        return pacienteDAO.cambiarActivo(id, activo);
    }
}
