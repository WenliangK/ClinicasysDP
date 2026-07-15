package Modelo;

import java.time.LocalDateTime;

public class Factura {
    private Long id;
    private Long citaId;
    private Long pacienteId;
    private String pacienteNombre;
    private String pacienteDni;
    private String descripcion;
    private double costo;
    private LocalDateTime fechaEmision;

    public Factura() {
    }

    public Factura(Long citaId, Long pacienteId, String pacienteNombre, String pacienteDni,
                   String descripcion, double costo) {
        this.citaId = citaId;
        this.pacienteId = pacienteId;
        this.pacienteNombre = pacienteNombre;
        this.pacienteDni = pacienteDni;
        this.descripcion = descripcion;
        this.costo = costo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCitaId() {
        return citaId;
    }

    public Long getPacienteId() {
        return pacienteId;
    }

    public String getPacienteNombre() {
        return pacienteNombre;
    }

    public String getPacienteDni() {
        return pacienteDni;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public double getCosto() {
        return costo;
    }

    public LocalDateTime getFechaEmision() {
        return fechaEmision;
    }

    @Override
    public String toString() {
        return "Factura #" + id + " - S/ " + String.format("%.2f", costo);
    }
}
