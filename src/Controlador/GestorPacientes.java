package Controlador;

import DAO.PacienteDAO;
import DAOImpl.PacienteDAOImpl;
import Modelo.Paciente;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class GestorPacientes {
    private static GestorPacientes instancia;
    private final PacienteDAO pacienteDAO = new PacienteDAOImpl();

    private GestorPacientes() {}

    public static GestorPacientes getInstancia() {
        if (instancia == null) instancia = new GestorPacientes();
        return instancia;
    }

    public CompletableFuture<List<Paciente>> getTodos() {
        return pacienteDAO.listarTodos();
    }

    public CompletableFuture<List<Paciente>> listarPacientes() {
        return pacienteDAO.listarTodos();
    }
}