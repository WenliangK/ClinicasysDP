package Modelo;

import java.time.LocalDateTime;

/**
 * Representa una factura/boleta persistida en la BD.
 * El costo y la descripcion ya vienen calculados por la cadena de Decorator
 * (CitaBase + RadiografiaDecorator + AnalisisSangreDecorator); esta clase
 * solo modela la fila que se guarda/lee de la tabla "facturas".
 *
 * Los datos del paciente (nombre, dni) se guardan como "snapshot" en el
 * momento de emision: si el paciente cambia sus datos despues, esta boleta
 * ya emitida no cambia retroactivamente, igual que una factura real.
 */
public class Factura {
    private int id;
    private Integer citaId;       // puede ser null si la factura no esta asociada a una cita guardada
    private Integer pacienteId;   // puede ser null si no se selecciono paciente
    private String pacienteNombre;
    private String pacienteDni;
    private String descripcion;
    private double costo;
    private LocalDateTime fechaEmision;

    /** Constructor para una factura nueva (todavia no existe en la BD), sin paciente asociado. */
    public Factura(Integer citaId, String descripcion, double costo) {
        this(citaId, null, null, null, descripcion, costo);
    }

    /** Constructor para una factura nueva con paciente asociado. */
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

    /** Constructor completo: usado por el DAO al reconstruir una fila existente de la BD. */
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
