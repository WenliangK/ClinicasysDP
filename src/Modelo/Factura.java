package Modelo;

import java.time.LocalDateTime;
public class Factura {
    private int id;
    private Integer citaId;
    private Integer pacienteId;
    private String pacienteNombre;
    private String pacienteDni;
    private String descripcion;
    private double costo;
    private LocalDateTime fechaEmision;

    public Factura(Integer citaId, String descripcion, double costo) {
        this(citaId, null, null, null, descripcion, costo);
    }

    public Factura(Integer citaId, Integer pacienteId, String pacienteNombre, String pacienteDni,
                   String descripcion, double costo) {
        this.citaId = citaId;
        this.pacienteId = pacienteId;
        this.pacienteNombre = pacienteNombre;
        this.pacienteDni = pacienteDni;
        this.descripcion = descripcion;
        this.costo = costo;
        this.fechaEmision = LocalDateTime.now();
    }

    public Factura(int id, Integer citaId, Integer pacienteId, String pacienteNombre, String pacienteDni,
                   String descripcion, double costo, LocalDateTime fechaEmision) {
        this.id = id;
        this.citaId = citaId;
        this.pacienteId = pacienteId;
        this.pacienteNombre = pacienteNombre;
        this.pacienteDni = pacienteDni;
        this.descripcion = descripcion;
        this.costo = costo;
        this.fechaEmision = fechaEmision;
    }

    public int getId()                       { return id; }
    public Integer getCitaId()               { return citaId; }
    public Integer getPacienteId()           { return pacienteId; }
    public String getPacienteNombre()        { return pacienteNombre; }
    public String getPacienteDni()           { return pacienteDni; }
    public String getDescripcion()           { return descripcion; }
    public double getCosto()                 { return costo; }
    public LocalDateTime getFechaEmision()   { return fechaEmision; }

    public void setId(int id) { this.id = id; }

    @Override
    public String toString() {
        return "Factura #" + id + " - S/ " + String.format("%.2f", costo);
    }
}
