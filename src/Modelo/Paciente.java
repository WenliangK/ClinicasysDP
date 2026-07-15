package Modelo;

public class Paciente {
    private Long id;
    private String nombre;
    private String dni;
    private String telefono;
    private String email;

    public Paciente(Long id, String nombre, String dni, String telefono, String email) {
        this.id = id;
        this.nombre = nombre;
        this.dni = dni;
        this.telefono = telefono;
        this.email = email;
    }

    public Paciente(String nombre, String dni, String telefono, String email) {
        this(null, nombre, dni, telefono, email);
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDni() {
        return dni;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // AÑADIDO: sin esto, cualquier JComboBox<Paciente> o JList<Paciente>
    // muestra "Modelo.Paciente@<hashcode>" en vez del nombre del paciente.
    @Override
    public String toString() {
        return this.nombre + " - " + this.dni;
    }
}