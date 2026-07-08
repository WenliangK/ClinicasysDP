package Controlador;

import DAO.PacienteDAO;
import DAO.PacienteDAOImpl;
import Modelo.Paciente;
import Utilidades.ExcepcionesPersonalizadas;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
public class GestorPacientes {

    private static GestorPacientes instancia;
    private final PacienteDAO pacienteDAO;

    private GestorPacientes() {
        this.pacienteDAO = new PacienteDAOImpl();
    }

    public static GestorPacientes getInstancia() {
        if (instancia == null) instancia = new GestorPacientes();
        return instancia;
    }

    public Paciente registrar(String nombre, String dni, String tel, String email) {
        Paciente p = new Paciente(nombre, dni, tel, email);
        try {
            pacienteDAO.insertar(p);
        } catch (SQLException e) {
            throw new RuntimeException("Error al registrar el paciente en la base de datos: " + e.getMessage(), e);
        }
        return p;
    }

    public Paciente buscarPorDni(String dni)
            throws ExcepcionesPersonalizadas.PacienteNoEncontradoException {
        try {
            Paciente p = pacienteDAO.buscarPorDni(dni);
            if (p == null) {
                throw new ExcepcionesPersonalizadas.PacienteNoEncontradoException(-1);
            }
            return p;
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar el paciente: " + e.getMessage(), e);
        }
    }

    public void eliminar(int id) {
        try {
            pacienteDAO.eliminar(id);
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar el paciente: " + e.getMessage(), e);
        }
    }

    public List<Paciente> getTodos() {
        try {
            return pacienteDAO.listarTodos();
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar pacientes desde la base de datos: " + e.getMessage(), e);
        }
    }
}
