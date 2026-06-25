package Modelo;

public class Medico {
    private int id;
    private String nombre;
    private String especialidad;
    private String tipo;

    /** Constructor completo: usado por el DAO al reconstruir un registro existente de la BD. */
    public Medico(int id, String nombre, String especialidad, String tipo) {
        this.id = id;
        this.nombre = nombre;
        this.especialidad = especialidad;
        this.tipo = tipo;
    }

    /** Constructor sin id: usado al crear un medico nuevo, antes de insertarlo en la BD. */
    public Medico(String nombre, String especialidad, String tipo) {
        this(0, nombre, especialidad, tipo);
    }

    public int getId()              { return id; }
    public String getNombre()       { return nombre; }
    public String getEspecialidad() { return especialidad; }
    public String getTipo()         { return tipo; }

    public void setId(int id)                       { this.id = id; }
    public void setNombre(String nombre)            { this.nombre = nombre; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }
    public void setTipo(String tipo)                 { this.tipo = tipo; }

    @Override
    public String toString() { return "Dr. " + nombre + " - " + especialidad; }
}
