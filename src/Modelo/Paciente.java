package Modelo;

public class Paciente {
    private Long id;
    private String nombre;
    private String dni;
    private String telefono;
    private String email;
    private boolean activo = true;

    public Paciente() {
    }

    public Paciente(Long id, String nombre, String dni, String telefono, String email, boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.dni = dni;
        this.telefono = telefono;
        this.email = email;
        this.activo = activo;
    }

    public Paciente(Long id, String nombre, String dni, String telefono, String email) {
        this(id, nombre, dni, telefono, email, true);
    }

    public Paciente(String nombre, String dni, String telefono, String email) {
        this(null, nombre, dni, telefono, email, true);
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

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    // AÑADIDO: sin esto, cualquier JComboBox<Paciente> o JList<Paciente>
    // muestra "Modelo.Paciente@<hashcode>" en vez del nombre del paciente.
    @Override
    public String toString() {
        return nombre + " - " + dni;
    }
}