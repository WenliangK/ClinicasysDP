package Modelo;

public class Medico {
    private Long id;
    private String nombre;
    private String especialidad;
    private String tipo;
    private boolean activo = true;

    public Medico() {
    }

    public Medico(Long id, String nombre, String especialidad, String tipo, boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.especialidad = especialidad;
        this.tipo = tipo;
        this.activo = activo;
    }

    public Medico(Long id, String nombre, String especialidad, String tipo) {
        this(id, nombre, especialidad, tipo, true);
    }

    public Medico(String nombre, String especialidad, String tipo) {
        this(null, nombre, especialidad, tipo, true);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return "Dr. " + nombre + " - " + especialidad;
    }
}
