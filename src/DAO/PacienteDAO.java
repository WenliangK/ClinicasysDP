package DAO;

import Modelo.Paciente;

import java.sql.SQLException;
import java.util.List;

public interface PacienteDAO {
    void insertar(Paciente paciente) throws SQLException;
    void actualizar(Paciente paciente) throws SQLException;
    void eliminar(int id) throws SQLException;
    Paciente buscarPorId(int id) throws SQLException;
    Paciente buscarPorDni(String dni) throws SQLException;
    List<Paciente> listarTodos() throws SQLException;
}
