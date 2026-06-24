package Decorator;
public class CitaBase implements Facturable {
    private static final double PRECIO_BASE = 50.0;
    private String descripcionCita;

    public CitaBase(String descripcionCita) {
        this.descripcionCita = descripcionCita;
    }

    @Override
    public double getCosto() { return PRECIO_BASE; }

    @Override
    public String getDescripcion() { return "Consulta: " + descripcionCita; }
}
