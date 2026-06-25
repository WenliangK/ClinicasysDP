package Modelo;

public class Paciente {
    private int id;
    private String nombre;
    private String dni;
    private String telefono;
    private String email;

    /**
     * Constructor completo: usado por el DAO al reconstruir un registro existente de la BD.
     */
    public Paciente(int id, String nombre, String dni, String telefono, String email) {
        this.id = id;
        this.nombre = nombre;
        this.dni = dni;
        this.telefono = telefono;
        this.email = email;
    }

    /**
     * Constructor sin id: usado al crear un paciente nuevo, antes de insertarlo en la BD.
     * El id real lo asigna PostgreSQL (SERIAL) y el DAO lo setea con setId().
     */
    public Paciente(String nombre, String dni, String telefono, String email) {
        this(0, nombre, dni, telefono, email);
    }

    public int getId() {
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

    public void setId(int id) {
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

    @Override
    public String toString() {
        // Esto es lo que se verá en el desplegable
        return this.nombre + " - " + this.dni;
    }
}
