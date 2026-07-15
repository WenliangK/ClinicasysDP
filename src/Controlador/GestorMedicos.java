package Controlador;

import DAO.MedicoDAO;
import DAOImpl.MedicoDAOImpl;
import Modelo.Medico;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class GestorMedicos {
    private static volatile GestorMedicos instancia;
    private final MedicoDAO medicoDAO;

    private GestorMedicos() {
        this(new MedicoDAOImpl());
    }

    GestorMedicos(MedicoDAO medicoDAO) {
        this.medicoDAO = medicoDAO;
    }

    public static GestorMedicos getInstancia() {
        GestorMedicos actual = instancia;
        if (actual == null) {
            synchronized (GestorMedicos.class) {
                actual = instancia;
                if (actual == null) {
                    actual = new GestorMedicos();
                    instancia = actual;
                }
            }
        }
        return actual;
    }

    public CompletableFuture<List<Medico>> getTodos() {
        return medicoDAO.listarTodos()
                .thenApply(lista -> lista.stream().filter(Medico::isActivo).toList());
    }

    public CompletableFuture<List<Medico>> listarMedicos() {
        return medicoDAO.listarTodos();
    }

    public CompletableFuture<Medico> guardar(Medico medico) {
        return medicoDAO.guardar(medico);
    }

    public CompletableFuture<Medico> cambiarActivo(long id, boolean activo) {
        return medicoDAO.cambiarActivo(id, activo);
    }
}
