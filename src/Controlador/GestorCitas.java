package Controlador;

import DAO.CitaDAO;
import DAO.CitaDAOImpl;
import Modelo.Cita;
import Modelo.Paciente;
import Observer.Observador;
import Observer.Sujeto;
import Utilidades.ExcepcionesPersonalizadas;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** CONTROLADOR de citas. Actua como puente entre la Vista y el CitaDAO; mantiene el Observer intacto. */
public class GestorCitas implements Sujeto {

    private static GestorCitas instancia;
    private final CitaDAO citaDAO;
    private final List<Observador> observadores = new ArrayList<>();

    private GestorCitas() {
        this.citaDAO = new CitaDAOImpl();
    }

    public static GestorCitas getInstancia() {
        if (instancia == null) instancia = new GestorCitas();
        return instancia;
    }

    // ─── CRUD de Citas ───────────────────────────────────────────────

    public Cita registrarCita(Paciente paciente, String medico,
                               LocalDateTime fechaHora, String motivo)
            throws ExcepcionesPersonalizadas.FechaInvalidaException {
        if (fechaHora.isBefore(LocalDateTime.now())) {
            throw new ExcepcionesPersonalizadas.FechaInvalidaException();
        }
        Cita nueva = new Cita(paciente, medico, fechaHora, motivo);
        try {
            citaDAO.insertar(nueva);
        } catch (SQLException e) {
            throw new RuntimeException("Error al registrar la cita en la base de datos: " + e.getMessage(), e);
        }
        notificar("NUEVA_CITA", nueva.getId());
        return nueva;
    }

    public void cambiarEstado(int citaId, Cita.Estado nuevoEstado) {
        try {
            citaDAO.actualizarEstado(citaId, nuevoEstado);
            notificar(nuevoEstado.name(), citaId);
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar el estado de la cita: " + e.getMessage(), e);
        }
    }

    public void cancelarCita(int citaId) {
        cambiarEstado(citaId, Cita.Estado.CANCELADO);
    }

    public List<Cita> getCitas() {
        try {
            return citaDAO.listarTodas();
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar citas desde la base de datos: " + e.getMessage(), e);
        }
    }

    public List<Cita> getCitasActivas() {
        return getCitas().stream()
                .filter(c -> c.getEstado() != Cita.Estado.CANCELADO)
                .toList();
    }

    // ─── Observer ────────────────────────────────────────────────────

    @Override
    public void suscribir(Observador o)   { observadores.add(o); }

    @Override
    public void desuscribir(Observador o) { observadores.remove(o); }

    @Override
    public void notificar(String estado, int citaId) {
        for (Observador o : observadores) {
            o.actualizar(estado, citaId);
        }
    }
}
