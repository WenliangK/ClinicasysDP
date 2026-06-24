package Modelo;

public class Paciente {
    private int id;
    private String nombre;
    private String dni;
    private String telefono;
    private String email;

    public Paciente(int id, String nombre, String dni, String telefono, String email) {
        this.id = id;
        this.nombre = nombre;
        this.dni = dni;
        this.telefono = telefono;
        this.email = email;
    }

    public int getId()           { return id; }
    public String getNombre()    { return nombre; }
    public String getDni()       { return dni; }
    public String getTelefono()  { return telefono; }
    public String getEmail()     { return email; }
    public void setNombre(String nombre)     { this.nombre = nombre; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public void setEmail(String email)       { this.email = email; }

    @Override
    public String toString() { return nombre + " (DNI: " + dni + ")"; }
}
