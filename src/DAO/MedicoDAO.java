package DAO;

import Modelo.Medico;

import java.sql.SQLException;
import java.util.List;

public interface MedicoDAO {
    void insertar(Medico medico) throws SQLException;
    void actualizar(Medico medico) throws SQLException;
    void eliminar(int id) throws SQLException;
    Medico buscarPorId(int id) throws SQLException;
    List<Medico> listarTodos() throws SQLException;
}
