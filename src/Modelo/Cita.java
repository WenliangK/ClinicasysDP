package Modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Cita {
    public enum Estado { EN_ESPERA, EN_CONSULTORIO, ATENDIDO, CANCELADO }

    private int id;
    private Paciente paciente;
    private String medico;
    private LocalDateTime fechaHora;
    private Estado estado;
    private String motivo;
    private int medicoId;
    private int salaId;

    public int getMedicoId() {
        return this.medicoId;
    }

    public int getSalaId() {
        return this.salaId;
    }
    public Cita(Paciente paciente, String medico, LocalDateTime fechaHora, String motivo) {
        this(0, paciente, medico, fechaHora, motivo, Estado.EN_ESPERA);
    }

    public Cita(int id, Paciente paciente, String medico, LocalDateTime fechaHora, String motivo) {
        this(id, paciente, medico, fechaHora, motivo, Estado.EN_ESPERA);
    }

    public Cita(int id, Paciente paciente, String medico, LocalDateTime fechaHora, String motivo, Estado estado) {
        this.id = id;
        this.paciente = paciente;
        this.medico = medico;
        this.fechaHora = fechaHora;
        this.motivo = motivo;
        this.estado = estado;
    }

    public int getId()                  { return id; }
    public Paciente getPaciente()        { return paciente; }
    public String getMedico()            { return medico; }
    public LocalDateTime getFechaHora()  { return fechaHora; }
    public Estado getEstado()            { return estado; }
    public String getMotivo()            { return motivo; }

    public void setId(int id)             { this.id = id; }
    public void setEstado(Estado estado) { this.estado = estado; }

    public String getFechaFormateada() {
        return fechaHora.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    @Override
    public String toString() {
        return "Cita #" + id + " - " + paciente.getNombre() + " [" + estado + "]";
    }
}
