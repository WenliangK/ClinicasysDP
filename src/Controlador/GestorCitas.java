package Controlador;

import DAO.CitaDAO;
import DAOImpl.CitaDAOImpl;
import Modelo.Cita;
import Modelo.Medico;
import Modelo.Paciente;
import Observer.Sujeto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class GestorCitas extends Sujeto {
    private static volatile GestorCitas instancia;
    private final CitaDAO citaDAO;

    private GestorCitas() {
        this(new CitaDAOImpl());
    }

    GestorCitas(CitaDAO citaDAO) {
        this.citaDAO = citaDAO;
    }

    public static GestorCitas getInstancia() {
        GestorCitas actual = instancia;
        if (actual == null) {
            synchronized (GestorCitas.class) {
                actual = instancia;
                if (actual == null) {
                    actual = new GestorCitas();
                    instancia = actual;
                }
            }
        }
        return actual;
    }

    public CompletableFuture<List<Cita>> getTodas() {
        return citaDAO.listarTodos();
    }

    public CompletableFuture<List<Cita>> getCitasVigentes() {
        return citaDAO.listarTodos().thenApply(citas -> citas.stream()
                .filter(cita -> cita.getEstado() != Cita.Estado.ATENDIDO)
                .filter(cita -> cita.getEstado() != Cita.Estado.CANCELADO)
                .toList());
    }

    @Override
    public CompletableFuture<Cita> cambiarEstado(long id, Cita.Estado estado) {
        return citaDAO.cambiarEstado(id, estado)
                .thenApply(cita -> {
                    notificar(cita.getEstado().name(), cita.getId());
                    return cita;
                });
    }

    @Override
    public CompletableFuture<Cita> guardar(Cita nuevaCita) {
        return citaDAO.guardar(nuevaCita)
                .thenApply(cita -> {
                    notificar(cita.getEstado().name(), cita.getId());
                    return cita;
                });
    }

    public CompletableFuture<Cita> registrarCita(Paciente paciente, Medico medico,
                                                  LocalDateTime fechaHora, String motivo, int numeroSala) {
        return guardar(new Cita(paciente, medico, fechaHora, motivo, numeroSala));
    }
}
