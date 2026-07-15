package Controlador;

import DAO.MedicoDAO;
import DAOImpl.MedicoDAOImpl;
import Modelo.Medico;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class GestorMedicos {
    private static GestorMedicos instancia;
    private final MedicoDAO medicoDAO = new MedicoDAOImpl();

    private GestorMedicos() {}

    public static GestorMedicos getInstancia() {
        if (instancia == null) instancia = new GestorMedicos();
        return instancia;
    }

    public CompletableFuture<List<Medico>> getTodos() {
        return medicoDAO.listarTodos();
    }
}