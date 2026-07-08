package Controlador;

import DAO.MedicoDAO;
import DAO.MedicoDAOImpl;
import Modelo.Medico;

import java.sql.SQLException;
import java.util.List;

public class GestorMedicos {

    private static GestorMedicos instancia;
    private final MedicoDAO medicoDAO;

    private GestorMedicos() {
        this.medicoDAO = new MedicoDAOImpl();
    }

    public static GestorMedicos getInstancia() {
        if (instancia == null) instancia = new GestorMedicos();
        return instancia;
    }

    public Medico registrar(String nombre, String especialidad, String tipo) {
        Medico m = new Medico(nombre, especialidad, tipo);
        try {
            medicoDAO.insertar(m);
        } catch (SQLException e) {
            throw new RuntimeException("Error al registrar el medico en la base de datos: " + e.getMessage(), e);
        }
        return m;
    }

    public void eliminar(int id) {
        try {
            medicoDAO.eliminar(id);
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar el medico: " + e.getMessage(), e);
        }
    }

    public List<Medico> getTodos() {
        try {
            return medicoDAO.listarTodos();
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar medicos desde la base de datos: " + e.getMessage(), e);
        }
    }
}