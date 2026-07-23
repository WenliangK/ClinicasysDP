package Controlador;

import DAO.CitaDAO;
import DAOImpl.CitaDAOImpl;
import Modelo.Cita;
import Modelo.Medico;
import Modelo.Paciente;
import Observer.Sujeto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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

    private static final LocalTime HORA_APERTURA =
            LocalTime.of(8, 0);

    private static final LocalTime HORA_CIERRE =
            LocalTime.of(18, 0);

    private static final int INTERVALO_MINUTOS = 30;

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
        return citaDAO
                .listarTodos()
                .thenApply(citas ->
                        citas.stream()
                                .filter(this::estaVigente)
                                .toList()
                );
    }

    @Override
    public CompletableFuture<Cita> cambiarEstado(
            long id,
            Cita.Estado estado
    ) {
        return citaDAO
                .cambiarEstado(id, estado)
                .thenApply(cita -> {
                    notificar(
                            cita.getEstado().name(),
                            cita.getId()
                    );

                    return cita;
                });
    }

    @Override
    public CompletableFuture<Cita> guardar(
            Cita nuevaCita
    ) {
        return citaDAO
                .guardar(nuevaCita)
                .thenApply(cita -> {
                    notificar(
                            cita.getEstado().name(),
                            cita.getId()
                    );

                    return cita;
                });
    }

    public CompletableFuture<List<LocalTime>> obtenerHorariosDisponibles(
            Paciente paciente,
            Medico medico,
            LocalDate fecha
    ) {
        if (fecha == null
                || medico == null
                || medico.getId() == null) {
            return CompletableFuture.completedFuture(
                    List.of()
            );
        }

        if (fecha.isBefore(LocalDate.now())) {
            return CompletableFuture.completedFuture(
                    List.of()
            );
        }

        return citaDAO
                .listarTodos()
                .thenApply(citas ->
                        construirHorariosDisponibles(
                                citas,
                                paciente,
                                medico,
                                fecha
                        )
                );
    }

    private List<LocalTime> construirHorariosDisponibles(
            List<Cita> citas,
            Paciente paciente,
            Medico medico,
            LocalDate fecha
    ) {
        List<LocalTime> disponibles =
                new ArrayList<>();

        LocalDateTime ahora =
                LocalDateTime.now()
                        .truncatedTo(ChronoUnit.MINUTES);

        for (
                LocalTime hora = HORA_APERTURA;
                hora.isBefore(HORA_CIERRE);
                hora = hora.plusMinutes(INTERVALO_MINUTOS)
        ) {
            LocalDateTime fechaHora =
                    LocalDateTime.of(fecha, hora);

            if (!fechaHora.isAfter(ahora)) {
                continue;
            }

            if (medicoEstaOcupado(
                    citas,
                    medico,
                    fechaHora
            )) {
                continue;
            }

            if (pacienteEstaOcupado(
                    citas,
                    paciente,
                    fechaHora
            )) {
                continue;
            }

            if (!haySalaDisponible(
                    citas,
                    fechaHora
            )) {
                continue;
            }

            disponibles.add(hora);
        }

        return List.copyOf(disponibles);
    }

    private boolean medicoEstaOcupado(
            List<Cita> citas,
            Medico medico,
            LocalDateTime fechaHora
    ) {
        return citas.stream()
                .filter(this::estaVigente)
                .anyMatch(cita ->
                        Objects.equals(
                                cita.getMedicoId(),
                                medico.getId()
                        )
                                && mismoHorario(
                                cita.getFechaHora(),
                                fechaHora
                        )
                );
    }

    private boolean pacienteEstaOcupado(
            List<Cita> citas,
            Paciente paciente,
            LocalDateTime fechaHora
    ) {
        if (paciente == null
                || paciente.getId() == null) {
            return false;
        }

        return citas.stream()
                .filter(this::estaVigente)
                .anyMatch(cita ->
                        cita.getPaciente() != null
                                && Objects.equals(
                                cita.getPaciente().getId(),
                                paciente.getId()
                        )
                                && mismoHorario(
                                cita.getFechaHora(),
                                fechaHora
                        )
                );
    }

    private boolean haySalaDisponible(
            List<Cita> citas,
            LocalDateTime fechaHora
    ) {
        Set<Integer> salasOcupadas =
                obtenerSalasOcupadas(
                        citas,
                        fechaHora
                );

        return salasOcupadas.size() < TOTAL_SALAS;
    }

    private Set<Integer> obtenerSalasOcupadas(
            List<Cita> citas,
            LocalDateTime fechaHora
    ) {
        Set<Integer> salasOcupadas =
                new HashSet<>();

        citas.stream()
                .filter(this::estaVigente)
                .filter(cita ->
                        mismoHorario(
                                cita.getFechaHora(),
                                fechaHora
                        )
                )
                .map(Cita::getSalaId)
                .filter(Objects::nonNull)
                .forEach(salasOcupadas::add);

        return salasOcupadas;
    }

    public CompletableFuture<Integer> obtenerSalaDisponible(
            Medico medico,
            LocalDateTime fechaHora
    ) {
        LocalDateTime horario =
                normalizarHorario(fechaHora);

        if (medico == null
                || medico.getId() == null
                || horario == null) {
            return CompletableFuture.completedFuture(
                    PRIMERA_SALA
            );
        }

        return citaDAO
                .listarTodos()
                .thenApply(citas -> {
                    validarDisponibilidadMedico(
                            citas,
                            medico,
                            horario
                    );

                    return buscarSalaDisponible(
                            citas,
                            horario
                    );
                });
    }

    public CompletableFuture<Cita> registrarCita(
            Paciente paciente,
            Medico medico,
            LocalDateTime fechaHora,
            String motivo
    ) {
        LocalDateTime horario =
                normalizarHorario(fechaHora);

        return citaDAO
                .listarTodos()
                .thenCompose(citas -> {
                    validarDatos(
                            paciente,
                            medico,
                            horario,
                            motivo
                    );

                    validarDisponibilidadMedico(
                            citas,
                            medico,
                            horario
                    );

                    validarDisponibilidadPaciente(
                            citas,
                            paciente,
                            horario
                    );

                    int salaDisponible =
                            buscarSalaDisponible(
                                    citas,
                                    horario
                            );

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
        if (paciente == null
                || paciente.getId() == null) {
            lanzarError(
                    "Debes seleccionar un paciente válido."
            );
        }

        if (!paciente.isActivo()) {
            lanzarError(
                    "El paciente seleccionado está inactivo."
            );
        }

        if (medico == null
                || medico.getId() == null) {
            lanzarError(
                    "Debes seleccionar un médico válido."
            );
        }

        if (!medico.isActivo()) {
            lanzarError(
                    "El médico seleccionado está inactivo."
            );
        }

        if (fechaHora == null) {
            lanzarError(
                    "Debes seleccionar una fecha y hora."
            );
        }

        if (fechaHora.isBefore(
                LocalDateTime.now()
                        .truncatedTo(ChronoUnit.MINUTES)
        )) {
            lanzarError(
                    "La fecha y hora de la cita no pueden estar en el pasado."
            );
        }

        if (fechaHora.toLocalTime().isBefore(HORA_APERTURA)
                || !fechaHora.toLocalTime().isBefore(HORA_CIERRE)) {
            lanzarError(
                    "La cita debe estar dentro del horario de atención: "
                            + "08:00 a 18:00."
            );
        }

        if (fechaHora.getMinute() % INTERVALO_MINUTOS != 0) {
            lanzarError(
                    "La hora debe seleccionarse en intervalos de "
                            + INTERVALO_MINUTOS
                            + " minutos."
            );
        }

        if (motivo == null || motivo.isBlank()) {
            lanzarError(
                    "Debes ingresar el motivo de la consulta."
            );
        }
    }

    private void validarDisponibilidadMedico(
            List<Cita> citas,
            Medico medico,
            LocalDateTime fechaHora
    ) {
        if (medicoEstaOcupado(
                citas,
                medico,
                fechaHora
        )) {
            lanzarError(
                    "El médico seleccionado ya tiene una cita reservada "
                            + "para esa fecha y hora."
            );
        }
    }

    private void validarDisponibilidadPaciente(
            List<Cita> citas,
            Paciente paciente,
            LocalDateTime fechaHora
    ) {
        if (pacienteEstaOcupado(
                citas,
                paciente,
                fechaHora
        )) {
            lanzarError(
                    "El paciente ya tiene una cita reservada "
                            + "para esa fecha y hora."
            );
        }
    }

    private int buscarSalaDisponible(
            List<Cita> citas,
            LocalDateTime fechaHora
    ) {
        Set<Integer> salasOcupadas =
                obtenerSalasOcupadas(
                        citas,
                        fechaHora
                );

        for (
                int sala = PRIMERA_SALA;
                sala <= TOTAL_SALAS;
                sala++
        ) {
            if (!salasOcupadas.contains(sala)) {
                return sala;
            }
        }

        lanzarError(
                "No hay salas disponibles para la fecha y hora seleccionadas."
        );

        return PRIMERA_SALA;
    }

    private boolean estaVigente(
            Cita cita
    ) {
        return cita != null
                && cita.getEstado() != Cita.Estado.ATENDIDO
                && cita.getEstado() != Cita.Estado.CANCELADO;
    }

    private boolean mismoHorario(
            LocalDateTime fecha1,
            LocalDateTime fecha2
    ) {
        return fecha1 != null
                && fecha2 != null
                && normalizarHorario(fecha1)
                .equals(
                        normalizarHorario(fecha2)
                );
    }

    private LocalDateTime normalizarHorario(
            LocalDateTime fechaHora
    ) {
        return fechaHora == null
                ? null
                : fechaHora.truncatedTo(
                ChronoUnit.MINUTES
        );
    }

    private void lanzarError(
            String mensaje
    ) {
        throw new CompletionException(
                new IllegalStateException(mensaje)
        );
    }
}