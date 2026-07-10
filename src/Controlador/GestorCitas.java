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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GestorCitas implements Sujeto {

    private static GestorCitas instancia;
    private final CitaDAO citaDAO;
    private final List<Observador> observadores = new ArrayList<>();
    private final Set<String> salasOcupadas = new HashSet<>();

    private GestorCitas() {
        this.citaDAO = new CitaDAOImpl();
    }

    public static GestorCitas getInstancia() {
        if (instancia == null) instancia = new GestorCitas();
        return instancia;
    }
    public Cita registrarCita(Paciente paciente, String medico,
                              LocalDateTime fechaHora, String motivo, int numeroSala)
            throws ExcepcionesPersonalizadas.FechaInvalidaException,
            ExcepcionesPersonalizadas.CitaDuplicadaException,
            ExcepcionesPersonalizadas.SalaOcupadaException {

        if (fechaHora.isBefore(LocalDateTime.now())) {
            throw new ExcepcionesPersonalizadas.FechaInvalidaException();
        }

        boolean yaExiste = getCitas().stream().anyMatch(c ->
                c.getPaciente().getId() == paciente.getId()
                        && c.getMedico().equalsIgnoreCase(medico)
                        && c.getFechaHora().equals(fechaHora)
                        && c.getEstado() != Cita.Estado.CANCELADO
        );
        if (yaExiste) {
            throw new ExcepcionesPersonalizadas.CitaDuplicadaException(
                    "El paciente " + paciente.getNombre() + " ya tiene una cita con " + medico + " a esa misma hora.");
        }

        String claveSala = numeroSala + "@" + fechaHora;
        if (salasOcupadas.contains(claveSala)) {
            throw new ExcepcionesPersonalizadas.SalaOcupadaException(numeroSala);
        }

        Cita nueva = new Cita(paciente, medico, fechaHora, motivo);
        try {
            citaDAO.insertar(nueva);
        } catch (SQLException e) {
            throw new RuntimeException("Error al registrar la cita en la base de datos: " + e.getMessage(), e);
        }

        salasOcupadas.add(claveSala);
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


    public List<Cita> getCitasVigentes() {
        return getCitas().stream()
                .filter(c -> c.getEstado() == Cita.Estado.EN_ESPERA || c.getEstado() == Cita.Estado.EN_CONSULTORIO)
                .toList();
    }


    public List<Cita> getHistorial() {
        return getCitas().stream()
                .filter(c -> c.getEstado() == Cita.Estado.ATENDIDO || c.getEstado() == Cita.Estado.CANCELADO)
                .sorted(java.util.Comparator.comparingInt(Cita::getId))
                .toList();
    }

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