package DAO;

import Modelo.Cita;

import java.sql.SQLException;
import java.util.List;

public interface CitaDAO {
    void insertar(Cita cita) throws SQLException;
    void actualizarEstado(int citaId, Cita.Estado nuevoEstado) throws SQLException;
    void eliminar(int id) throws SQLException;
    Cita buscarPorId(int id) throws SQLException;
    List<Cita> listarTodas() throws SQLException;
}
