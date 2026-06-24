package Controlador;



import Modelo.Paciente;

import java.util.ArrayList;
import java.util.List;

/** CONTROLADOR CRUD de pacientes. */
public class GestorPacientes {

    private static GestorPacientes instancia;
    private List<Paciente> pacientes = new ArrayList<>();
    private int contadorId = 1;

    private GestorPacientes() {
        // Datos de muestra
        pacientes.add(new Paciente(contadorId++, "Maria Lopez",    "12345678", "987654321", "maria@mail.com"));
        pacientes.add(new Paciente(contadorId++, "Carlos Ruiz",    "87654321", "912345678", "carlos@mail.com"));
        pacientes.add(new Paciente(contadorId++, "Ana Torres",     "11223344", "945678901", "ana@mail.com"));
    }

    public static GestorPacientes getInstancia() {
        if (instancia == null) instancia = new GestorPacientes();
        return instancia;
    }

    public Paciente registrar(String nombre, String dni, String tel, String email) {
        Paciente p = new Paciente(contadorId++, nombre, dni, tel, email);
        pacientes.add(p);
        return p;
    }

    public Paciente buscarPorDni(String dni)
            throws ExcepcionesPersonalizadas.PacienteNoEncontradoException {
        return pacientes.stream()
                .filter(p -> p.getDni().equals(dni))
                .findFirst()
                .orElseThrow(() ->
                        new ExcepcionesPersonalizadas.PacienteNoEncontradoException(-1));
    }

    public void eliminar(int id) { pacientes.removeIf(p -> p.getId() == id); }

    public List<Paciente> getTodos() { return pacientes; }
}
