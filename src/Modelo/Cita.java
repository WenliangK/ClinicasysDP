package Modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Cita {
    public enum Estado { EN_ESPERA, EN_CONSULTORIO, ATENDIDO, CANCELADO }

    private Long id;
    private Paciente paciente;
    private String medico;
    private LocalDateTime fechaHora;
    private Estado estado = Estado.EN_ESPERA;
    private String motivo;
    private Long medicoId;
    private Integer salaId;
    private LocalDateTime fechaActualizacion;

    public Cita() {
    }

    public Cita(Paciente paciente, Medico medico, LocalDateTime fechaHora, String motivo, int salaId) {
        this.paciente = paciente;
        this.medico = medico == null ? null : medico.getNombre();
        this.medicoId = medico == null ? null : medico.getId();
        this.fechaHora = fechaHora;
        this.motivo = motivo;
        this.salaId = salaId;
        this.estado = Estado.EN_ESPERA;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public String getMedico() {
        return medico;
    }

    public void setMedico(String medico) {
        this.medico = medico;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public Long getMedicoId() {
        return medicoId;
    }

    public void setMedicoId(Long medicoId) {
        this.medicoId = medicoId;
    }

    public Integer getSalaId() {
        return salaId;
    }

    public void setSalaId(Integer salaId) {
        this.salaId = salaId;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public String getFechaFormateada() {
        return fechaHora == null ? "-" : fechaHora.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public String getFechaActualizacionFormateada() {
        return fechaActualizacion == null
                ? "-"
                : fechaActualizacion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    @Override
    public String toString() {
        String nombrePaciente = paciente == null ? "Sin paciente" : paciente.getNombre();
        return "Cita #" + id + " - " + nombrePaciente + " [" + estado + "]";
    }
}
