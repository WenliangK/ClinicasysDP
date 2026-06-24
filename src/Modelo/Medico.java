package Modelo;

public class Medico {
    private int id;
    private String nombre;
    private String especialidad;
    private String tipo;

    public Medico(int id, String nombre, String especialidad, String tipo) {
        this.id = id;
        this.nombre = nombre;
        this.especialidad = especialidad;
        this.tipo = tipo;
    }

    public int getId()              { return id; }
    public String getNombre()       { return nombre; }
    public String getEspecialidad() { return especialidad; }
    public String getTipo()         { return tipo; }

    @Override
    public String toString() { return "Dr. " + nombre + " - " + especialidad; }
}
