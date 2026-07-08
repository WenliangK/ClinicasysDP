package DAO;

import Modelo.Medico;
import Singleton.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MedicoDAOImpl implements MedicoDAO {

    @Override
    public void insertar(Medico medico) throws SQLException {
        String sql = "INSERT INTO medicos (nombre, especialidad) VALUES (?, ?)";
        Connection con = ConexionDB.getInstancia().getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, medico.getNombre());
            ps.setString(2, medico.getEspecialidad());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    medico.setId(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public void actualizar(Medico medico) throws SQLException {
        String sql = "UPDATE medicos SET nombre = ?, especialidad = ?, tipo = ? WHERE id = ?";
        Connection con = ConexionDB.getInstancia().getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, medico.getNombre());
            ps.setString(2, medico.getEspecialidad());
            ps.setString(3, medico.getTipo());
            ps.setInt(4, medico.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM medicos WHERE id = ?";
        Connection con = ConexionDB.getInstancia().getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public Medico buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, nombre, especialidad, tipo FROM medicos WHERE id = ?";
        Connection con = ConexionDB.getInstancia().getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    @Override
    public List<Medico> listarTodos() throws SQLException {
        List<Medico> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, especialidad, tipo FROM medicos ORDER BY id";
        Connection con = ConexionDB.getInstancia().getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    private Medico mapear(ResultSet rs) throws SQLException {
        Medico m = new Medico(
                rs.getString("nombre"),
                rs.getString("especialidad"),
                rs.getString("tipo")
        );
        m.setId(rs.getInt("id"));
        return m;
    }
}
