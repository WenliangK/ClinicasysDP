package Modelo;

public class Sala {
    private int numero;
    private String descripcion;
    private boolean disponible;

    public Sala(int numero, String descripcion) {
        this.numero = numero;
        this.descripcion = descripcion;
        this.disponible = true;
    }

    public int getNumero()         { return numero; }
    public String getDescripcion() { return descripcion; }
    public boolean isDisponible()  { return disponible; }
    public void setDisponible(boolean d) { this.disponible = d; }

    @Override
    public String toString() {
        return "Sala " + numero + " - " + descripcion + (disponible ? " [Libre]" : " [Ocupada]");
    }
}
