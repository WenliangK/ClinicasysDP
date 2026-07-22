package Controlador;

import DAO.CitaDAO;
import DAOImpl.CitaDAOImpl;
import Modelo.Cita;
import Modelo.Medico;
import Modelo.Paciente;
import Observer.Sujeto;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public final class GestorCitas extends Sujeto {
    private static volatile GestorCitas instancia;
    private static final int PRIMERA_SALA = 1;
    private static final int TOTAL_SALAS = 20;

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
                .filter(this::estaVigente)
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

    /**
     * Consulta la sala que se mostrara automaticamente en el formulario.
     * Tambin comprueba que el mdico no est ocupado en ese horario.
     */
    public CompletableFuture<Integer> obtenerSalaDisponible(
            Medico medico,
            LocalDateTime fechaHora
    ) {
        LocalDateTime horario = normalizarHorario(fechaHora);

        if (medico == null || medico.getId() == null || horario == null) {
            return CompletableFuture.completedFuture(PRIMERA_SALA);
        }

        return citaDAO.listarTodos().thenApply(citas -> {
            validarDisponibilidadMedico(citas, medico, horario);
            return buscarSalaDisponible(citas, horario);
        });
    }

    /**
     * Registra una cita y asigna la primera sala disponible de forma automatica.
     */
    public CompletableFuture<Cita> registrarCita(
            Paciente paciente,
            Medico medico,
            LocalDateTime fechaHora,
            String motivo
    ) {
        LocalDateTime horario = normalizarHorario(fechaHora);

        return citaDAO.listarTodos().thenCompose(citas -> {
            validarDatos(paciente, medico, horario, motivo);
            validarDisponibilidadMedico(citas, medico, horario);
            validarDisponibilidadPaciente(citas, paciente, horario);

            int salaDisponible = buscarSalaDisponible(citas, horario);
            Cita nuevaCita = new Cita(
                    paciente,
                    medico,
                    horario,
                    motivo.trim(),
                    salaDisponible
            );

            return guardar(nuevaCita);
        });
    }

    private void validarDatos(
            Paciente paciente,
            Medico medico,
            LocalDateTime fechaHora,
            String motivo
    ) {
        if (paciente == null || paciente.getId() == null) {
            lanzarError("Debes seleccionar un paciente valido.");
        }
        if (!paciente.isActivo()) {
            lanzarError("El paciente seleccionado esta inactivo.");
        }
        if (medico == null || medico.getId() == null) {
            lanzarError("Debes seleccionar un medico valido.");
        }
        if (!medico.isActivo()) {
            lanzarError("El medico seleccionado esta inactivo.");
        }
        if (fechaHora == null) {
            lanzarError("Debes seleccionar una fecha y hora.");
        }
        if (fechaHora.isBefore(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))) {
            lanzarError("La fecha y hora de la cita no pueden estar en el pasado.");
        }
        if (motivo == null || motivo.isBlank()) {
            lanzarError("Debes ingresar el motivo de la consulta.");
        }
    }

    private void validarDisponibilidadMedico(
            List<Cita> citas,
            Medico medico,
            LocalDateTime fechaHora
    ) {
        boolean ocupado = citas.stream()
                .filter(this::estaVigente)
                .anyMatch(cita -> Objects.equals(cita.getMedicoId(), medico.getId())
                        && mismoHorario(cita.getFechaHora(), fechaHora));

        if (ocupado) {
            lanzarError("El medico seleccionado ya tiene una cita reservada para esa fecha y hora.");
        }
    }

    private void validarDisponibilidadPaciente(
            List<Cita> citas,
            Paciente paciente,
            LocalDateTime fechaHora
    ) {
        boolean ocupado = citas.stream()
                .filter(this::estaVigente)
                .anyMatch(cita -> cita.getPaciente() != null
                        && Objects.equals(cita.getPaciente().getId(), paciente.getId())
                        && mismoHorario(cita.getFechaHora(), fechaHora));

        if (ocupado) {
            lanzarError("El paciente ya tiene una cita reservada para esa fecha y hora.");
        }
    }

    private int buscarSalaDisponible(List<Cita> citas, LocalDateTime fechaHora) {
        Set<Integer> salasOcupadas = new HashSet<>();

        citas.stream()
                .filter(this::estaVigente)
                .filter(cita -> mismoHorario(cita.getFechaHora(), fechaHora))
                .map(Cita::getSalaId)
                .filter(Objects::nonNull)
                .forEach(salasOcupadas::add);

        for (int sala = PRIMERA_SALA; sala <= TOTAL_SALAS; sala++) {
            if (!salasOcupadas.contains(sala)) {
                return sala;
            }
        }

        lanzarError("No hay salas disponibles para la fecha y hora seleccionadas.");
        return PRIMERA_SALA;
    }

    private boolean estaVigente(Cita cita) {
        return cita != null
                && cita.getEstado() != Cita.Estado.ATENDIDO
                && cita.getEstado() != Cita.Estado.CANCELADO;
    }

    private boolean mismoHorario(LocalDateTime fecha1, LocalDateTime fecha2) {
        return fecha1 != null
                && fecha2 != null
                && normalizarHorario(fecha1).equals(normalizarHorario(fecha2));
    }

    private LocalDateTime normalizarHorario(LocalDateTime fechaHora) {
        return fechaHora == null ? null : fechaHora.truncatedTo(ChronoUnit.MINUTES);
    }

    private void lanzarError(String mensaje) {
        throw new CompletionException(new IllegalStateException(mensaje));
    }
}